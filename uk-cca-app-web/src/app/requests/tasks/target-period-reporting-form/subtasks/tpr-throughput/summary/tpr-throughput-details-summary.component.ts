import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  calculateThroughputValues,
  decideVariableEnergyType,
  ThroughputDetailsSummaryComponent,
  toTotalsOnlySummaryData,
} from '@requests/common';
import { SummaryComponent } from '@shared/components';

import { tprFormQuery } from '../../../target-period-reporting-form.selectors';

@Component({
  selector: 'cca-tpr-throughput-details-summary',
  template: `
    <div>
      <netz-page-heading caption="Provide target period throughput details">Summary</netz-page-heading>

      @if (variableEnergyType() === 'BY_PRODUCT') {
        <cca-throughput-details-summary
          [referenceData]="referenceData()"
          [performanceData]="performanceData()"
          [isEditable]="isEditable()"
          [reportType]="reportType()"
          [targetPeriodType]="targetPeriodType()"
        />
      } @else {
        <cca-summary [data]="summaryData()" />
      }
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [
    PageHeadingComponent,
    ReturnToTaskOrActionPageComponent,
    SummaryComponent,
    ThroughputDetailsSummaryComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TprThroughputDetailsSummaryComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly referenceData = this.requestTaskStore.select(tprFormQuery.selectReferenceData);
  protected readonly performanceData = this.requestTaskStore.select(tprFormQuery.selectPerformanceData);
  protected readonly reportType = this.requestTaskStore.select(tprFormQuery.selectReportType);
  protected readonly targetPeriodType = this.requestTaskStore.select(tprFormQuery.selectTargetPeriodType);
  protected readonly targetPeriodYear = this.requestTaskStore.select(tprFormQuery.selectTargetPeriodYear);
  protected readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);

  private readonly calculations = computed(() =>
    calculateThroughputValues({
      referenceData: this.referenceData(),
      performanceData: this.performanceData(),
      reportType: this.reportType(),
      targetPeriodType: this.targetPeriodType(),
      actualThroughput: this.performanceData().throughputDetails?.actualThroughput ?? null,
    }),
  );

  protected readonly variableEnergyType = computed(() =>
    decideVariableEnergyType(this.referenceData()?.baselineAndTargets?.variableEnergyType),
  );

  protected readonly summaryData = computed(() =>
    toTotalsOnlySummaryData(
      this.referenceData(),
      this.performanceData(),
      this.calculations().targetVariableEnergy,
      this.isEditable(),
    ),
  );
}
