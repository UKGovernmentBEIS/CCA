import { ChangeDetectionStrategy, Component } from '@angular/core';

import { SendForPeerReviewComponent } from '@requests/common';

@Component({
  selector: 'cca-admin-termination-send-for-peer-review',
  template: `
    <cca-send-for-peer-review
      requestTaskActionType="ADMIN_TERMINATION_REQUEST_PEER_REVIEW"
      payloadType="ADMIN_TERMINATION_PEER_REVIEW_REQUEST_PAYLOAD"
      data-testid="admin-termination-send-for-peer-review-form"
      returnToText="Return to: Admin Termination"
    />
  `,
  standalone: true,
  imports: [SendForPeerReviewComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class AdminTerminationSendForPeerReviewComponent {}
