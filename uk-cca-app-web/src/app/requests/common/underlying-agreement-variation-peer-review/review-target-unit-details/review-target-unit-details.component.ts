import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { RequestTaskStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { toVariationReviewTargetUnitDetailsSummaryDataWithDecision } from '../../underlying-agreement/summaries';
import { underlyingAgreementVariationPeerReviewQuery } from '../underlying-agreement-variation-peer-review.selectors';

@Component({
  selector: 'cca-una-variation-peer-review-target-unit-details',
  template: `
    <div>
      <netz-page-heading>Target unit details</netz-page-heading>
      <cca-summary [data]="summaryData()" />
    </div>
    <netz-return-to-task-or-action-page />
  `,
  imports: [PageHeadingComponent, SummaryComponent, ReturnToTaskOrActionPageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewTargetUnitDetailsComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly summaryData = computed(() =>
    toVariationReviewTargetUnitDetailsSummaryDataWithDecision(
      this.requestTaskStore.select(
        underlyingAgreementVariationPeerReviewQuery.selectUnderlyingAgreementTargetUnitDetails,
      )(),
      this.requestTaskStore.select(
        underlyingAgreementVariationPeerReviewQuery.selectSubtaskDecision('TARGET_UNIT_DETAILS'),
      )(),
      this.requestTaskStore.select(underlyingAgreementVariationPeerReviewQuery.selectReviewAttachments)(),
      '../../file-download',
      false,
    ),
  );
}
