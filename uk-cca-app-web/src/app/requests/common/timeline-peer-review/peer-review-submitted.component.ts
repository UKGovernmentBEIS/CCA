import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { adminTerminationPeerReviewQuery, toPeerReviewSummaryData } from '@requests/common';
import { SummaryComponent } from '@shared/components';

@Component({
  selector: 'cca-peer-review-submitted',
  template: `<cca-summary [data]="summaryData" />`,
  standalone: true,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PeerReviewSubmittedComponent {
  private readonly store = inject(RequestActionStore);
  private readonly payload = this.store.select(adminTerminationPeerReviewQuery.selectPayload)();
  private readonly submitter = this.store.select(requestActionQuery.selectSubmitter)();
  protected readonly summaryData = toPeerReviewSummaryData(this.payload, this.submitter);
}
