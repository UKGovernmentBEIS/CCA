import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { adminTerminationPeerReviewQuery } from './peer-review-submitted.component.selectors';
import { toPeerReviewSummaryData } from './peer-review-submitted-summary-data';

@Component({
  selector: 'cca-peer-review-submitted',
  template: `<cca-summary [data]="summaryData" />`,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PeerReviewSubmittedComponent {
  private readonly store = inject(RequestActionStore);
  private readonly payload = this.store.select(adminTerminationPeerReviewQuery.selectPayload)();
  private readonly submitter = this.store.select(requestActionQuery.selectSubmitter)();
  protected readonly summaryData = toPeerReviewSummaryData(this.payload, this.submitter);
}
