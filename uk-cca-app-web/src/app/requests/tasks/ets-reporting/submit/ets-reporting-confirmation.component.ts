import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-ets-reporting-confirmation',
  template: `
    <div class="govuk-grid-row govuk-!-margin-top-6">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel>
          <h1 class="govuk-panel__title govuk-!-margin-bottom-0">
            UK emissions trading scheme (UK ETS) report submitted
          </h1>
        </govuk-panel>
        <h2 class="govuk-heading-m">What happens next</h2>
        <p>Your UK ETS report has been submitted. You can return to the dashboard to view your tasks.</p>
        <div class="govuk-!-margin-top-8">
          <a class="govuk-link" routerLink="/dashboard" [replaceUrl]="true">Return to: Dashboard</a>
        </div>
      </div>
    </div>
  `,
  imports: [PanelComponent, RouterLink],
})
export default class EtsReportingConfirmationComponent {}
