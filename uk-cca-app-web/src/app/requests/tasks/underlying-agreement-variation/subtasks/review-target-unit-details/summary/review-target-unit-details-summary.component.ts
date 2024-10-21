import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  toReviewTargetUnitDetailsOriginalSummaryData,
  toReviewTargetUnitDetailsSummaryData,
  underlyingAgreementQuery,
} from '@requests/common';
import { HighlightDiffComponent, PageHeadingComponent, SummaryComponent } from '@shared/components';

@Component({
  selector: 'cca-una-summary-target-unit-details',
  templateUrl: './review-target-unit-details-summary.component.html',
  standalone: true,
  imports: [
    PageHeadingComponent,
    SummaryComponent,
    ReturnToTaskOrActionPageComponent,
    HighlightDiffComponent,
    NgTemplateOutlet,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ReviewTargetUnitDetailsSummaryComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly summaryDataOriginal = toReviewTargetUnitDetailsOriginalSummaryData(
    this.requestTaskStore.select(underlyingAgreementQuery.selectAccountReferenceData)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
  );

  protected readonly summaryDataCurrent = toReviewTargetUnitDetailsSummaryData(
    this.requestTaskStore.select(underlyingAgreementQuery.selectAccountReferenceData)(),
    this.requestTaskStore.select(underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
  );
}
