import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

@Component({
  selector: 'netz-cancel-confirmation',
  standalone: true,
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="Task cancelled"></govuk-panel>
      </div>
    </div>
    <ng-content />
    <a class="gov uk-link" routerLink="/dashboard"> Return to dashboard </a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, PanelComponent],
})
export class CancelConfirmationComponent {}
