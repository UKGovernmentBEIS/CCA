import { ChangeDetectionStrategy, Component } from '@angular/core';

import { WarningTextComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-underlying-agreement-wait-activation',
  standalone: true,
  imports: [WarningTextComponent],
  template: ` <govuk-warning-text assistiveText="">Waiting for the operator's assent/activation</govuk-warning-text> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnderlyingAgreementWaitActivationComponent {}
