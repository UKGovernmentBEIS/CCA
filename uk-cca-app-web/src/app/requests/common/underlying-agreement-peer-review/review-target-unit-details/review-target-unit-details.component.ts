import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestTaskStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { toReviewTargetUnitDetailsSummaryDataWithDecision } from '../../underlying-agreement/summaries';
import { underlyingAgreementPeerReviewQuery } from '../underlying-agreement-peer-review.selectors';

@Component({
  selector: 'cca-una-peer-review-target-unit-details',
  template: `
    <div>
      <netz-page-heading>Target unit details</netz-page-heading>
      <cca-summary [data]="summaryData()" />
    </div>

    <a routerLink="../../" class="govuk-link">Return to: Peer review application for underlying agreement</a>
  `,
  imports: [PageHeadingComponent, SummaryComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewTargetUnitDetailsComponent {
  private readonly store = inject(RequestTaskStore);

  protected readonly summaryData = computed(() =>
    toReviewTargetUnitDetailsSummaryDataWithDecision(
      this.store.select(underlyingAgreementPeerReviewQuery.selectUnderlyingAgreementTargetUnitDetails)(),
      this.store.select(underlyingAgreementPeerReviewQuery.selectSubtaskDecision('TARGET_UNIT_DETAILS'))(),
      this.store.select(underlyingAgreementPeerReviewQuery.selectReviewAttachments)(),
      '../../file-download',
      false,
    ),
  );
}
