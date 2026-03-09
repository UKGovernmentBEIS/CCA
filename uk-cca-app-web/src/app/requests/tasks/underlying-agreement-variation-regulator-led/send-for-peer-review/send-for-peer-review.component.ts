import { ChangeDetectionStrategy, Component } from '@angular/core';

import { SendForPeerReviewComponent } from '@requests/common';

@Component({
  selector: 'cca-underlying-agreement-variation-regulator-led-send-for-peer-review',
  template: `
    <cca-send-for-peer-review
      requestTaskActionType="UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_REQUEST_PEER_REVIEW"
      payloadType="UNDERLYING_AGREEMENT_VARIATION_PEER_REVIEW_REQUEST_PAYLOAD"
      returnToText="Return to: Vary the underlying agreement"
    />
  `,
  imports: [SendForPeerReviewComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class UnAVariationRegulatorLedSendForPeerReviewComponent {}
