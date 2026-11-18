import { TitleCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { catchError, EMPTY, switchMap, take } from 'rxjs';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { ItemLinkPipe } from '@netz/common/pipes';
import {
  ButtonDirective,
  DetailsComponent,
  ErrorSummaryComponent,
  GovukValidators,
  SelectComponent,
} from '@netz/govuk-components';
import { TP_REPORTING_ERROR_MESSAGES, TpReportingErrorCode } from '@requests/common';

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
          const message = TP_REPORTING_ERROR_MESSAGES[err.error.code as TpReportingErrorCode];
          if (message) {
            this.form.controls.targetPeriodType.setErrors({ responseError: message });
          }

          this.hasFormErrors.set(true);
          return EMPTY;
        }),
      )
      .subscribe(({ items }) => {
        const link = items?.length === 1 ? new ItemLinkPipe().transform(items[0]) : ['/dashboard'];

        this.router.navigate(link, { relativeTo: this.activatedRoute, replaceUrl: true });
      });
  }
}
