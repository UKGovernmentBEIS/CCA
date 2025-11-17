import { ChangeDetectionStrategy, Component } from '@angular/core';

import { SendForPeerReviewComponent } from '@requests/common';

@Component({
  selector: 'cca-underlying-agreement-review-send-for-peer-review',
  template: `
    <cca-send-for-peer-review
      requestTaskActionType="UNDERLYING_AGREEMENT_REQUEST_PEER_REVIEW"
      payloadType="UNDERLYING_AGREEMENT_PEER_REVIEW_REQUEST_PAYLOAD"
      returnToText="Return to: Review application for underlying agreement"
    />
  `,
  imports: [SendForPeerReviewComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class UnderlyingAgreementReviewSendForPeerReviewComponent {}
