import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { SummaryComponent } from '@shared/components';

import { PerformanceReportStore } from '../../../performance-report-store';
import { toReportSubmittedSummaryData } from './report-submitted-summary-data';

@Component({
  selector: 'cca-report-submitted',
  template: `
    <h1 class="govuk-heading-l">{{ targetPeriodName }}</h1>
    <cca-summary [data]="summaryData" />
  `,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReportSubmittedComponent {
  private readonly performanceReportStore = inject(PerformanceReportStore);
  protected readonly targetPeriodName = this.performanceReportStore.state.statusInfo.targetPeriodName;
  protected readonly summaryData = toReportSubmittedSummaryData(this.performanceReportStore.state.reportDetails);
}
