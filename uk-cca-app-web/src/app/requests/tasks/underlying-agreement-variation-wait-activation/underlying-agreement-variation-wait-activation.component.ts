import { ChangeDetectionStrategy, Component } from '@angular/core';

import { WarningTextComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-underlying-agreement-variation-wait-activation',
  template: `
    <govuk-warning-text assistiveText="">Waiting for the regulator to make a determination</govuk-warning-text>
  `,
  imports: [WarningTextComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnderlyingAgreementVariationWaitActivationComponent {}
