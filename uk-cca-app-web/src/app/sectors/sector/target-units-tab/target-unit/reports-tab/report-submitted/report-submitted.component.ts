import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { SummaryComponent } from '@shared/components';

import { PerformanceReportStore } from '../../performance-report-store';
import { toReportSubmittedSummaryData } from './report-submitted-summary-data';

@Component({
  selector: 'cca-report-submitted',
  standalone: true,
  imports: [SummaryComponent],
  templateUrl: './report-submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReportSubmittedComponent {
  private readonly performanceReportStore = inject(PerformanceReportStore);
  readonly targetPeriodName = this.performanceReportStore.state.statusInfo.targetPeriodName;
  readonly summaryData = toReportSubmittedSummaryData(this.performanceReportStore.state.reportDetails);
}
