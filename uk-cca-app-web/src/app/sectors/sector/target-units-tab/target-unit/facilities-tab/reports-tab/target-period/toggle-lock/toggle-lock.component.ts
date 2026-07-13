import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { catchError } from 'rxjs';

import { PageHeadingComponent } from '@netz/common/components';
import { ButtonDirective, GovukValidators, RadioComponent, RadioOptionComponent } from '@netz/govuk-components';

import { TargetPeriodPerformanceDataReportOfTheFacilityService } from 'cca-api';

import { FacilityTargetPeriodReportStore } from '../../../facility-target-period-report.store';

@Component({
  selector: 'cca-unlock-performance-report',
  templateUrl: './toggle-lock.component.html',
  imports: [PageHeadingComponent, RadioComponent, RadioOptionComponent, ReactiveFormsModule, ButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ToggleLockComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly facilityTargetPeriodReportStore = inject(FacilityTargetPeriodReportStore);

  private readonly targetPeriodPerformanceDataReportOfTheFacilityService = inject(
    TargetPeriodPerformanceDataReportOfTheFacilityService,
  );

  private readonly facilityId = signal(+this.activatedRoute.snapshot.paramMap.get('facilityId'));
  private readonly targetPeriodYear = signal(+this.activatedRoute.snapshot.paramMap.get('targetPeriodYear'));

  private readonly state = this.facilityTargetPeriodReportStore.stateAsSignal;

  private readonly targetPeriodType = computed(
    () => this.state().statusInfo.find((i) => Number(i.targetPeriodYear) === this.targetPeriodYear())?.targetPeriodType,
  );

  protected readonly form = new FormGroup({
    toggle: new FormControl<boolean>(null, GovukValidators.required('Please select an option')),
  });

  onSubmit() {
    if (this.form.invalid) return;

    this.targetPeriodPerformanceDataReportOfTheFacilityService
      .updateFacilityPerformanceDataLock(this.facilityId(), {
        targetPeriodType: this.targetPeriodType(),
        targetPeriodYear: this.targetPeriodYear(),
        locked: this.form.value.toggle,
      })
      .pipe(
        catchError((err) => {
          console.error(err);
          throw new Error();
        }),
      )
      .subscribe(() =>
        this.router.navigate(['../../../'], { fragment: 'reports', relativeTo: this.activatedRoute, replaceUrl: true }),
      );
  }
}
