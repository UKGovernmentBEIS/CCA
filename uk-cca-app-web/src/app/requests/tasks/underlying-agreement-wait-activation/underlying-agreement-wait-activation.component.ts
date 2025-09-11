import { ChangeDetectionStrategy, Component } from '@angular/core';

import { WarningTextComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-underlying-agreement-wait-activation',
  template: `<govuk-warning-text assistiveText="">Waiting for the operator's assent/activation</govuk-warning-text>`,
  standalone: true,
  imports: [WarningTextComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnderlyingAgreementWaitActivationComponent {}
