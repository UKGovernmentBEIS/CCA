import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import {
  DetailsComponent,
  SummaryListComponent,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
} from '@netz/govuk-components';
import {
  boolToString,
  toComparisonDataSummaryData,
  toResultsDetailsSummaryData,
  toTPRBaselineDataDetails,
} from '@requests/common';
import { SummaryComponent } from '@shared/components';

import { FacilityTargetPeriodReportStore } from '../../../facility-target-period-report.store';

@Component({
  selector: 'cca-tpr-details',
  templateUrl: './tpr-details.component.html',
  imports: [
    PageHeadingComponent,
    DetailsComponent,
    SummaryComponent,
    SummaryListComponent,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TprDetailsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly facilityTargetPeriodReportStore = inject(FacilityTargetPeriodReportStore);

  private readonly targetPeriodYear = +this.activatedRoute.snapshot.paramMap.get('targetPeriodYear');

  private readonly state = this.facilityTargetPeriodReportStore.stateAsSignal;

  private readonly currentStatusInfo = computed(() =>
    this.state()?.statusInfo.find((i) => i.targetPeriodYear === this.targetPeriodYear),
  );

  protected readonly details = computed(() => this.state().details);
  protected readonly targetPeriodType = computed(() => this.state().reportType);
  protected readonly isInterimReport = computed(() => this.targetPeriodType() === 'INTERIM');

  protected readonly atLeastSeventyPercentEnergyUsed = computed(() =>
    boolToString(this.details()?.atLeastSeventyPercentEnergyUsed),
  );

  private readonly referenceData = computed(() => ({
    baselineAndTargets: this.details().baselineAndTargets,
  }));

  protected readonly baselineDetails = computed(() => toTPRBaselineDataDetails(this.referenceData(), '.'));

  protected readonly comparisonData = computed(() =>
    toComparisonDataSummaryData({
      targetPeriodType: this.currentStatusInfo()?.targetPeriodType,
      reportType: this.targetPeriodType(),
      referenceData: this.referenceData(),
      calculatedResults: this.details()?.calculatedResults,
    }),
  );

  protected readonly resultsDetails = computed(() =>
    toResultsDetailsSummaryData({
      reportType: this.targetPeriodType(),
      calculatedResults: this.details()?.calculatedResults,
    }),
  );
}
