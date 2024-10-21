import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { LinkDirective, PanelComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-underlying-agreement-activation-notify-operator-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds govuk-body">
        <govuk-panel>Underlying agreement activated and sent to operator</govuk-panel>
        <a govukLink routerLink="/dashboard" [replaceUrl]="true"> Return to: Dashboard </a>
      </div>
    </div>
  `,
  standalone: true,
  imports: [PanelComponent, LinkDirective, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class UnderlyingAgreementActivationNotifyOperatorConfirmationComponent {}
