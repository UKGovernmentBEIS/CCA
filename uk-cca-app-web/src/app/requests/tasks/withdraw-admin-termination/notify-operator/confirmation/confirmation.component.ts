import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds govuk-body">
        <govuk-panel>Admin termination withdrawal notice sent to operator</govuk-panel>

        <p>The admin termination agreement has been withdrawn.</p>
        <p>The selected users will receive an email notification of your decision.</p>

        <a class="govuk-link" routerLink="/dashboard" [replaceUrl]="true"> Return to: Dashboard </a>
      </div>
    </div>
  `,
  standalone: true,
  imports: [PanelComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ConfirmationComponent {}
