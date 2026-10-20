import { ChangeDetectionStrategy, Component, computed, effect, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { take, tap } from 'rxjs';

import { GovukSelectOption, SelectComponent } from '@netz/govuk-components';
import { SummaryComponent } from '@shared/components';

import { TargetPeriodPerformanceDataReportOfTheFacilityService } from 'cca-api';

import { FacilityTargetPeriodReportStore } from '../../facility-target-period-report.store';
import { toFacilityReportsSummaryData } from '../facility-reports-summary-data';
import {
  FACILITY_TARGET_PERIOD_REPORT_FORM,
  FacilityTargetPeriodReportFormModel,
  FacilityTargetPeriodReportFormProvider,
} from './target-period-report-form.provider';

@Component({
  selector: 'cca-target-period-report',
  templateUrl: './target-period-report.component.html',
  imports: [ReactiveFormsModule, SelectComponent, SummaryComponent, RouterLink],
  providers: [FacilityTargetPeriodReportFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TargetPeriodReportComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly facilityTargetPeriodReportStore = inject(FacilityTargetPeriodReportStore);

  private readonly targetPeriodPerformanceDataReportOfTheFacilityService = inject(
    TargetPeriodPerformanceDataReportOfTheFacilityService,
  );

  protected readonly form = inject<FacilityTargetPeriodReportFormModel>(FACILITY_TARGET_PERIOD_REPORT_FORM);

  protected readonly facilityId = +this.activatedRoute.snapshot.paramMap.get('facilityId');

  private readonly state = this.facilityTargetPeriodReportStore.stateAsSignal;

  protected readonly targetPeriodsOptions: GovukSelectOption<'TP7' | 'TP8' | 'TP9'>[] = [
    { value: 'TP7', text: 'TP7' },
    { value: 'TP8', text: 'TP8' },
    { value: 'TP9', text: 'TP9' },
  ];

  protected readonly reportTypeOptions: GovukSelectOption<'INTERIM' | 'FINAL'>[] = [
    { value: 'INTERIM', text: 'Interim' },
    { value: 'FINAL', text: 'Final' },
  ];

  private readonly targetPeriodValue = toSignal(this.form.controls.targetPeriodType.valueChanges, {
    initialValue: this.form.controls.targetPeriodType.value,
  });

  private readonly reportTypeValue = toSignal(this.form.controls.reportType.valueChanges, {
    initialValue: this.form.controls.reportType.value,
  });

  protected readonly summaryData = computed(() =>
    this.state().statusInfo.map((info) => ({
      summary: toFacilityReportsSummaryData(info, this.reportTypeValue()),
      targetPeriodYear: info.targetPeriodYear,
    })),
  );

  constructor() {
    effect(() => {
      const targetPeriod = this.targetPeriodValue();
      const reportType = this.reportTypeValue();

      if (targetPeriod && reportType) {
        this.fetchAccountPerformanceData(this.facilityId, targetPeriod, reportType);
      }
    });
  }

  private fetchAccountPerformanceData(
    facilityId: number,
    targetPeriod: 'TP5' | 'TP6' | 'TP7' | 'TP8' | 'TP9',
    reportType: 'INTERIM' | 'FINAL',
  ) {
    return this.targetPeriodPerformanceDataReportOfTheFacilityService
      .getFacilityPerformanceDataStatus(facilityId, targetPeriod, reportType)
      .pipe(
        take(1),
        tap((statusInfo) =>
          this.facilityTargetPeriodReportStore.updateState({ statusInfo, reportType, targetPeriodType: targetPeriod }),
        ),
      )
      .subscribe();
  }
}
