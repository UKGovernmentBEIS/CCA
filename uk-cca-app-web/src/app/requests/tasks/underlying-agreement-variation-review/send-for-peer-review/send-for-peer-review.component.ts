import { ChangeDetectionStrategy, Component } from '@angular/core';

import { SendForPeerReviewComponent } from '@requests/common';

@Component({
  selector: 'cca-underlying-agreement-variation-send-for-peer-review',
  template: `
    <cca-send-for-peer-review
      requestTaskActionType="UNDERLYING_AGREEMENT_VARIATION_REQUEST_PEER_REVIEW"
      payloadType="UNDERLYING_AGREEMENT_VARIATION_PEER_REVIEW_REQUEST_PAYLOAD"
      returnToText="Return to: Apply to vary the underlying agreement"
    />
  `,
  imports: [SendForPeerReviewComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class UnderlyingAgreementReviewSendForPeerReviewComponent {}
