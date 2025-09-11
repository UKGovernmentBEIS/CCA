import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { SummaryComponent } from '@shared/components';

import { PatReportStore } from '../../../pat-report-store';
import { toPatReportSubmittedSummaryData } from './report-submitted-summary-data';

@Component({
  selector: 'cca-report-submitted',
  template: `
    <h1 class="govuk-heading-l">{{ targetPeriodName }}</h1>
    <cca-summary [data]="summaryData" />
  `,
  standalone: true,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReportSubmittedComponent {
  private readonly patReportStore = inject(PatReportStore);

  protected readonly targetPeriodName = this.patReportStore.state.reportDetails.targetPeriodName;
  protected readonly summaryData = toPatReportSubmittedSummaryData(this.patReportStore.state.reportDetails);
}
