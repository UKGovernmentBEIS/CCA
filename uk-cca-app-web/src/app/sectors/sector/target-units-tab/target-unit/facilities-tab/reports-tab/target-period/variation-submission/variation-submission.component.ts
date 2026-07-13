import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { catchError } from 'rxjs';

import { PageHeadingComponent } from '@netz/common/components';
import { ButtonDirective, GovukValidators, RadioComponent, RadioOptionComponent } from '@netz/govuk-components';

import { TargetPeriodPerformanceDataReportOfTheFacilityService } from 'cca-api';

import { FacilityTargetPeriodReportStore } from '../../../facility-target-period-report.store';

@Component({
  selector: 'cca-variation-submission',
  templateUrl: './variation-submission.component.html',
  imports: [PageHeadingComponent, RadioComponent, RadioOptionComponent, ReactiveFormsModule, ButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VariationSubmissionComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly facilityTargetPeriodReportStore = inject(FacilityTargetPeriodReportStore);

  private readonly targetPeriodPerformanceDataReportOfTheFacilityService = inject(
    TargetPeriodPerformanceDataReportOfTheFacilityService,
  );

  private readonly facilityId = signal(+this.activatedRoute.snapshot.paramMap.get('facilityId'));
  private readonly targetPeriodYear = signal(+this.activatedRoute.snapshot.paramMap.get('targetPeriodYear'));

  private readonly state = this.facilityTargetPeriodReportStore.stateAsSignal;

  private readonly statusInfo = computed(() =>
    this.state().statusInfo.find((i) => Number(i.targetPeriodYear) === this.targetPeriodYear()),
  );

  readonly form = new FormGroup({
    toggle: new FormControl<boolean>(
      this.statusInfo()?.variationIndicator,
      GovukValidators.required('Please select an option'),
    ),
  });

  onSubmit() {
    if (this.form.invalid) return;
    const toggle = this.form.value.toggle;

    this.targetPeriodPerformanceDataReportOfTheFacilityService
      .updateFacilityPerformanceDataVariationIndicator(this.facilityId(), {
        targetPeriodYear: this.targetPeriodYear(),
        variationIndicator: toggle,
      })
      .pipe(
        catchError((err) => {
          console.error(err);
          throw new Error();
        }),
      )
      .subscribe(() => {
        const updatedStatusInfo = this.state().statusInfo.map((info) =>
          Number(info.targetPeriodYear) === this.targetPeriodYear() ? { ...info, variationIndicator: toggle } : info,
        );

        this.facilityTargetPeriodReportStore.updateState({
          ...this.state(),
          statusInfo: updatedStatusInfo,
        });

        this.router.navigate(['../../../'], { fragment: 'reports', relativeTo: this.activatedRoute, replaceUrl: true });
      });
  }
}
