import { ChangeDetectionStrategy, Component } from '@angular/core';

import { SendForPeerReviewComponent } from '@requests/common';

@Component({
  selector: 'cca-notice-of-intent-send-for-peer-review',
  template: `
    <cca-send-for-peer-review
      requestTaskActionType="NON_COMPLIANCE_NOTICE_OF_INTENT_REQUEST_PEER_REVIEW"
      payloadType="NON_COMPLIANCE_PEER_REVIEW_REQUEST_PAYLOAD"
      returnToText="Return to: Upload notice of intent"
    />
  `,
  imports: [SendForPeerReviewComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class NoticeOfIntentSendForPeerReviewComponent {}
