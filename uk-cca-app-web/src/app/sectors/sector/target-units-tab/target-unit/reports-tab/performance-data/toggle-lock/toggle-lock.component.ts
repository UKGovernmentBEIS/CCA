import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { catchError } from 'rxjs';

import { ButtonDirective, GovukValidators, RadioComponent, RadioOptionComponent } from '@netz/govuk-components';
import { PerformanceDataTargetPeriodEnum } from '@requests/common';
import { logger } from '@shared/utils';

import { TargetPeriodPerformanceDataReportOfTheAccountService } from 'cca-api';

import { PerformanceReportStore } from '../../../performance-report-store';

@Component({
  selector: 'cca-unlock-performance-report',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <form [formGroup]="form" (ngSubmit)="onSubmit()">
          <div
            [legend]="legendText()"
            legendSize="heading"
            radioSize="large"
            formControlName="toggle"
            govuk-radio
            [hint]="hint()"
          >
            <govuk-radio-option [value]="true" [label]="'Yes'" />
            <govuk-radio-option [value]="false" [label]="'No'" />
          </div>
          <button govukButton type="submit">Confirm</button>
        </form>
      </div>
    </div>
  `,
  imports: [RadioComponent, RadioOptionComponent, ReactiveFormsModule, ButtonDirective],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ToggleLockComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly performanceReportStore = inject(PerformanceReportStore);
  private readonly targetPeriodPerformanceDataReportOfTheAccountService = inject(
    TargetPeriodPerformanceDataReportOfTheAccountService,
  );

  protected readonly isTargetUnitLocked = computed(() => this.performanceReportStore.stateAsSignal().statusInfo.locked);

  protected readonly legendText = computed(() =>
    this.isTargetUnitLocked
      ? 'Do you want to unlock the performance reporting task?'
      : 'Do you want to lock the performance reporting task?',
  );

  protected readonly hint = computed(() =>
    this.isTargetUnitLocked
      ? 'If you unlock the performance reporting task, sector users will be able to download a newer version of the target period reporting (TPR) spreadsheet. Existing performance data will be overwritten after they upload the new spreadsheet.'
      : 'If you lock the performance reporting task, the existing data will be kept and sector users will not be able to upload a newer version of the target period reporting (TPR) spreadsheet.',
  );

  protected readonly form = new FormGroup({
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

    this.targetPeriodPerformanceDataReportOfTheAccountService
      .updateAccountPerformanceDataLock(+this.activatedRoute.snapshot.paramMap.get('targetUnitId'), {
        targetPeriodType,
        locked: !this.isTargetUnitLocked(), // here toggle is true, so we just reverse the current lock state
      })
      .pipe(
        catchError((err) => {
          logger.error(err);
          throw new Error();
        }),
      )
      .subscribe(() =>
        this.router.navigate(['../../'], { fragment: 'reports', relativeTo: this.activatedRoute, replaceUrl: true }),
      );
  }
}
