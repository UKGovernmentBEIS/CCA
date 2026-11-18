import { ChangeDetectionStrategy, Component } from '@angular/core';

import { WarningTextComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-underlying-agreement-variation-wait-for-peer-review-precontent',
  template: `
    <govuk-warning-text assistiveText="">Waiting for peer review, you cannot make any changes</govuk-warning-text>
  `,
  imports: [WarningTextComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnderlyingAgreementVariationWaitForPeerReviewPrecontentComponent {}
