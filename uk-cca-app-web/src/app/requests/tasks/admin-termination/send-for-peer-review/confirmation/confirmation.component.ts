import { ChangeDetectionStrategy, Component } from '@angular/core';

import { PeerReviewConfirmationComponent } from '@requests/common';

@Component({
  selector: 'cca-admin-termination-peer-review-confirmation',
  template: `<cca-peer-review-confirmation />`,
  standalone: true,
  imports: [PeerReviewConfirmationComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class AdminTerminationPeerReviewConfirmationComponent {}
