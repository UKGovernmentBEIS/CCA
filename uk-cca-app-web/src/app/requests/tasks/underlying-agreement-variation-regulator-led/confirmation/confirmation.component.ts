import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-variation-regulator-led-confirmation',
  template: `
    <govuk-panel>Notification sent to users</govuk-panel>
    <p>You have approved the underlying agreement variation.</p>
    <p>The selected users will receive an email notification of your decision.</p>
    <a class="govuk-link" [routerLink]="['/dashboard']"> Return to: Dashboard </a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [PanelComponent, RouterLink],
})
export class NotifyOperatorRegulatorLedVariationConfirmationComponent {}
