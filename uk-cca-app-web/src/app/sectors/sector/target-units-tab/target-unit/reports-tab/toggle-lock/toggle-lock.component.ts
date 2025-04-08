import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { catchError } from 'rxjs';

import { ButtonDirective, GovukValidators, RadioComponent, RadioOptionComponent } from '@netz/govuk-components';
import { PerformanceDataTargetPeriodEnum } from '@requests/common';

import { AccountTargetPeriodReportingService } from 'cca-api';

import { PerformanceReportStore } from '../../performance-report-store';

@Component({
  selector: 'cca-unlock-performance-report',
  standalone: true,
  imports: [RadioComponent, RadioOptionComponent, ReactiveFormsModule, ButtonDirective],
  templateUrl: './toggle-lock.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ToggleLockComponent {
  private readonly performanceReportStore = inject(PerformanceReportStore);
  private readonly accountPerformanceDataStatusService = inject(AccountTargetPeriodReportingService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  readonly isTargetUnitLocked = this.performanceReportStore.state.statusInfo.locked;

  readonly legendText = this.isTargetUnitLocked
    ? 'Do you wish to unlock the performance reporting task?'
    : 'Do you want to lock the performance reporting task?';

  readonly hint = this.isTargetUnitLocked
    ? 'If you unlock the performance reporting task, sector users will be able to download a newer version of the target period reporting (TPR) spreadsheet.  Existing performance data will be overwritten after they upload the new spreadsheet.'
    : 'If you lock the performance reporting task, the existing data will be kept and sector users will not be able to upload a newer version of the target period reporting (TPR) spreadsheet.';

  readonly form = new FormGroup({
    toggle: new FormControl<boolean>(null, GovukValidators.required('Please select an option')),
  });

  onSubmit() {
    if (this.form.invalid) return;
    const toggle = this.form.value.toggle;

    if (!toggle) {
      this.router.navigate(['../../'], { fragment: 'reports', relativeTo: this.activatedRoute, replaceUrl: true });
      return;
    }

    const targetPeriodType = this.activatedRoute.snapshot.paramMap.get(
      'targetPeriodType',
    ) as PerformanceDataTargetPeriodEnum;

    this.accountPerformanceDataStatusService
      .updateAccountPerformanceDataLock(+this.activatedRoute.snapshot.paramMap.get('targetUnitId'), {
        targetPeriodType: targetPeriodType,
        locked: !this.isTargetUnitLocked, // here toggle is true, so we just reverse the current lock state
      })
      .pipe(
        catchError((err) => {
          console.error(err);
          throw new Error();
        }),
      )
      .subscribe(() => {
        this.router.navigate(['../../'], { fragment: 'reports', relativeTo: this.activatedRoute, replaceUrl: true });
      });
  }
}
