import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-confirmation',
  template: `<div class="govuk-grid-row">
    <div class="govuk-grid-column-two-thirds">
      <govuk-panel>Admin termination returned to regulator</govuk-panel>

      <a routerLink="/dashboard" class="govuk-link">Return to: Dashboard</a>
    </div>
  </div> `,
  imports: [PanelComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent {}
