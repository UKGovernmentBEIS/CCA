import { ChangeDetectionStrategy, Component } from '@angular/core';

import { WarningTextComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-underlying-agreement-wait-review',
  template: `
    <govuk-warning-text assistiveText="">Waiting for the regulator to complete the review</govuk-warning-text>
  `,
  standalone: true,
  imports: [WarningTextComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnderlyingAgreementWaitReviewComponent {}
