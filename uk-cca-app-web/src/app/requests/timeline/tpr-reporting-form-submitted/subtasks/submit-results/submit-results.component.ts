import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import { DetailsComponent } from '@netz/govuk-components';
import {
  toComparisonDataSummaryData,
  toResultsDetailsSummaryData,
  toTPRBaselineDataDetails,
  tprFormActionQuery,
} from '@requests/common';
import { SummaryComponent } from '@shared/components';

@Component({
  selector: 'cca-submit-results',
  templateUrl: './submit-results.component.html',
  imports: [PageHeadingComponent, DetailsComponent, SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmitResultsComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly details = this.requestActionStore.select(tprFormActionQuery.selectDetails);
  private readonly performanceData = this.requestActionStore.select(tprFormActionQuery.selectPerformanceData);

  protected readonly reportType = computed(() => this.details()?.reportType);
  protected readonly targetPeriodType = computed(() => this.details()?.targetPeriodType);
  protected readonly isInterimReport = computed(() => this.reportType() === 'INTERIM');

  private readonly referenceData = computed(() => ({
    baselineAndTargets: this.performanceData()?.baselineAndTargets,
  }));

  protected readonly baselineDetails = computed(() => toTPRBaselineDataDetails(this.referenceData(), '..'));

  protected readonly comparisonData = computed(() =>
    toComparisonDataSummaryData({
      targetPeriodType: this.targetPeriodType(),
      reportType: this.reportType(),
      referenceData: this.referenceData(),
      calculatedResults: this.performanceData()?.calculatedResults,
    }),
  );

  protected readonly resultsDetails = computed(() =>
    toResultsDetailsSummaryData({
      reportType: this.reportType(),
      calculatedResults: this.performanceData()?.calculatedResults,
    }),
  );
}
