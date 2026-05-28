import { ChangeDetectionStrategy, Component } from '@angular/core';

import { SendForPeerReviewComponent } from '@requests/common';

@Component({
  selector: 'cca-enforcement-response-notice-send-for-peer-review',
  template: `
    <cca-send-for-peer-review
      requestTaskActionType="NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_REQUEST_PEER_REVIEW"
      payloadType="NON_COMPLIANCE_PEER_REVIEW_REQUEST_PAYLOAD"
      returnToText="Return to: Upload enforcement response notice"
    />
  `,
  imports: [SendForPeerReviewComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class EnforcementResponseNoticeSendForPeerReviewComponent {}
