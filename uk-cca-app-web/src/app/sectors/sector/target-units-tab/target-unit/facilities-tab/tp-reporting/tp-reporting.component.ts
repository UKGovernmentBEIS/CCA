import { TitleCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { catchError, EMPTY, switchMap, take } from 'rxjs';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import {
  ButtonDirective,
  DetailsComponent,
  ErrorSummaryComponent,
  GovukValidators,
  SelectComponent,
} from '@netz/govuk-components';

import {
  PerformanceDataFacilityDigitalFormRequestCreateActionPayload,
  PerformanceDataReportTypeDTO,
  RequestItemsService,
  RequestsService,
} from 'cca-api';

@Component({
  selector: 'cca-tp-reporting',
  templateUrl: './tp-reporting.component.html',
  imports: [
    ReactiveFormsModule,
    ButtonDirective,
    PendingButtonDirective,
    PageHeadingComponent,
    ErrorSummaryComponent,
    SelectComponent,
    DetailsComponent,
    TitleCasePipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TpReportingComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly requestsService = inject(RequestsService);
  private readonly requestItemsService = inject(RequestItemsService);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  readonly hasFormErrors = signal(false);

  private readonly availablePeriods = this.activatedRoute.snapshot.data
    .availablePeriods as PerformanceDataReportTypeDTO[];

  readonly form = new FormGroup({
    targetPeriodType: new FormControl(null, [GovukValidators.required('Select a target period')]),
  });

  private readonly targetPeriodTypeValue = toSignal(this.form.controls.targetPeriodType.valueChanges, {
    initialValue: this.form.controls.targetPeriodType.value,
  });

  readonly availablePeriodTypes = this.availablePeriods.map((t) => ({
    text: t.targetPeriodType,
    value: t.targetPeriodType,
  }));

  readonly selectedReportType = computed(
    () => this.availablePeriods.find((p) => p.targetPeriodType === this.targetPeriodTypeValue())?.reportType,
  );

  onSubmit() {
    if (this.form.invalid) {
      this.hasFormErrors.set(true);
      return;
    }

    this.requestsService
      .processRequestCreateAction(
        {
          requestType: 'PERFORMANCE_DATA_FACILITY_DIGITAL_FORM',
          requestCreateActionPayload: {
            payloadType: 'PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_CREATE_ACTION_PAYLOAD',
            targetPeriodType: this.form.value.targetPeriodType,
            reportType: this.selectedReportType(),
          } as PerformanceDataFacilityDigitalFormRequestCreateActionPayload,
        },
        this.facilityId,
      )
      .pipe(
        take(1),
        switchMap(({ requestId }) => this.requestItemsService.getItemsByRequest(requestId)),
        catchError((err) => {
          switch (err.error.code) {
            case 'TPRDF1001':
              this.form.controls.targetPeriodType.setErrors({
                responseError:
                  'There is already a TPR task in progress for the target period you selected. You can locate the relevant task through the main dashboard.',
              });
              break;

            case 'TPRDF1002':
              this.form.controls.targetPeriodType.setErrors({
                responseError:
                  'The selected facility is not eligible to report for this target period. Select a different target period or exit the task.',
              });
              break;

            case 'TPRDF1003':
              this.form.controls.targetPeriodType.setErrors({
                responseError: 'The combination you selected has expired. Make a new selection.',
              });
              break;

            case 'TPRDF1004':
              this.form.controls.targetPeriodType.setErrors({
                responseError:
                  'The secondary reporting for this target period must be unlocked before submitting reports or corrections. Contact your regulator to make an unlocking request.',
              });
              break;

            case 'TPRDF1005':
              this.form.controls.targetPeriodType.setErrors({
                responseError:
                  'Facility must have at least one eligible product for this period. Correct product data through a variation.',
              });
              break;
          }

          this.hasFormErrors.set(true);
          return EMPTY;
        }),
      )
      .subscribe(() => this.router.navigate(['/'], { replaceUrl: true }));
  }
}
