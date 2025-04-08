import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-underlying-agreement-activation-notify-operator-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds govuk-body">
        <govuk-panel>Underlying agreement variation activated and sent to operator</govuk-panel>
        <a class="govuk-link" routerLink="/dashboard" [replaceUrl]="true"> Return to: Dashboard </a>
      </div>
    </div>
  `,
  standalone: true,
  imports: [PanelComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class UnderlyingAgreementVariationActivationNotifyOperatorConfirmationComponent {}
