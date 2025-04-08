import { ChangeDetectionStrategy, Component, computed, effect, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { EMPTY, switchMap, tap } from 'rxjs';

import { GovukSelectOption, SelectComponent } from '@netz/govuk-components';
import { PerformanceDataTargetPeriodEnum } from '@requests/common';
import { SummaryComponent } from '@shared/components';

import { AccountTargetPeriodReportingService } from 'cca-api';

import { PerformanceReportStore } from '../../performance-report-store';
import { toTuReportsSummaryData } from '../tu-reports-summary-data';

@Component({
  selector: 'cca-performance-report',
  standalone: true,
  imports: [ReactiveFormsModule, SelectComponent, SummaryComponent, RouterLink],
  templateUrl: './performance-report.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PerformanceReportComponent {
  private readonly accountTargetPeriodReportingService = inject(AccountTargetPeriodReportingService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly performanceReportStore = inject(PerformanceReportStore);
  readonly targetUnitId = +this.activatedRoute.snapshot.paramMap.get('targetUnitId');
  readonly targetPeriodsOptions: GovukSelectOption<PerformanceDataTargetPeriodEnum.TP6>[] = [
    {
      value: PerformanceDataTargetPeriodEnum.TP6,
      text: PerformanceDataTargetPeriodEnum.TP6,
    },
  ];

  readonly form = new FormGroup({
    targetPeriodType: new FormControl<PerformanceDataTargetPeriodEnum>(PerformanceDataTargetPeriodEnum.TP6),
  });

  readonly targetPeriodValue = toSignal(this.form.controls.targetPeriodType.valueChanges, {
    initialValue: this.form.controls.targetPeriodType.value,
  });

  readonly performanceReport = computed(() =>
    this.performanceReportStore.stateAsSignal().statusInfo.reportVersion > 0
      ? this.performanceReportStore.stateAsSignal()
      : null,
  );

  constructor() {
    effect(() => {
      const selectedValue = this.targetPeriodValue();
      if (selectedValue) {
        this.fetchAccountPerformanceData(selectedValue)
          .pipe(
            switchMap((statusInfo) => {
              this.performanceReportStore.setState({ statusInfo: statusInfo, reportDetails: null });
              if (statusInfo.reportVersion > 0) {
                return this.fetchAccountPerformanceSubmittedData(selectedValue).pipe(
                  tap((reportDetails) => {
                    this.performanceReportStore.setState({ statusInfo, reportDetails });
                  }),
                );
              }
              return EMPTY;
            }),
          )
          .subscribe();
      }
    });
  }

  readonly accountPerformanceDataSummaryData = computed(() => {
    return toTuReportsSummaryData(this.performanceReportStore.stateAsSignal().statusInfo, this.targetPeriodValue());
  });

  private fetchAccountPerformanceData(targetPeriodType: PerformanceDataTargetPeriodEnum) {
    return this.accountTargetPeriodReportingService.getAccountPerformanceDataStatus(
      this.targetUnitId,
      targetPeriodType,
    );
  }

  private fetchAccountPerformanceSubmittedData(targetPeriodType: PerformanceDataTargetPeriodEnum) {
    return this.accountTargetPeriodReportingService.getAccountPerformanceReportDetails(
      this.targetUnitId,
      targetPeriodType,
    );
  }
}
