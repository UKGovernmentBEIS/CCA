import { ChangeDetectionStrategy, Component } from '@angular/core';

import { WarningTextComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-underlying-agreement-variation-wait-activation',
  standalone: true,
  imports: [WarningTextComponent],
  template: `
    <govuk-warning-text assistiveText="">Waiting for the regulator to make a determination</govuk-warning-text>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnderlyingAgreementVariationWaitActivationComponent {}
