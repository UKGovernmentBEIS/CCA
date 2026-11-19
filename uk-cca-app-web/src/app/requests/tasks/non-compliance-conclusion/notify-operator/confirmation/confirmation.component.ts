import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-conclusion-notify-operator-confirmation',
  template: `
    <govuk-panel title="Withdrawal sent to operator" />

    <a class="govuk-link" routerLink="/dashboard" [replaceUrl]="true">Return to: Dashboard</a>
  `,
  imports: [PanelComponent, RouterLink],
})
export class ConclusionNotifyOperatorConfirmationComponent {}
