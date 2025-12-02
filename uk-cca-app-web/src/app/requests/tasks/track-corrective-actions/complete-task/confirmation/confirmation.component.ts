import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-track-corrective-actions-confirmation',
  template: `
    <govuk-panel title="Track corrective actions complete">
      Outcome <br />
      <span style="font-weight: bold;">All actions finalised</span>
    </govuk-panel>

    <a class="govuk-link" [routerLink]="['/dashboard']"> Return to: Dashboard </a>
  `,
  imports: [PanelComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TrackCorrectiveActionsConfirmationComponent {}
