import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-tpr-form-submit-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="Target period report submitted"></govuk-panel>

        <h2 class="govuk-heading-m">What happens next</h2>
        <p>
          The service will calculate and store the performance for the facility using the energy and throughput data you
          entered.
        </p>
        <p>You can find the results of these calculations on the Reports tab for the facility.</p>

        <a class="govuk-link" routerLink="/dashboard" [replaceUrl]="true">Return to: Dashboard</a>
      </div>
    </div>
  `,
  imports: [PanelComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TprFormSubmitConfirmationComponent {}
