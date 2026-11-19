import { Component } from '@angular/core';

import { PeerReviewConfirmationComponent } from '@requests/common';

@Component({
  selector: 'cca-admin-termination-peer-review-confirmation',
  template: `<cca-peer-review-confirmation />`,
  imports: [PeerReviewConfirmationComponent],
})
export default class AdminTerminationPeerReviewConfirmationComponent {}
