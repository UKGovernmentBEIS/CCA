import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { CcaPeerReviewDecisionSubmittedRequestActionPayload } from 'cca-api';

import { toNonCompliancePeerReviewDecisionSummaryData } from '../non-compliance-peer-reviewer-decision-summary-data';

@Component({
  selector: 'cca-non-compliance-notice-of-intent-peer-reviewer-decision',
  template: `<cca-summary [data]="summaryData()" />`,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NonComplianceNoticeOfIntentPeerReviewerDecisionComponent {
  private readonly store = inject(RequestActionStore);
  private readonly payload = this.store.select(requestActionQuery.selectActionPayload);

  protected readonly summaryData = computed(() =>
    toNonCompliancePeerReviewDecisionSummaryData(this.payload() as CcaPeerReviewDecisionSubmittedRequestActionPayload),
  );
}
