import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-tpr-csv-upload-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="Target period report submitted"></govuk-panel>

        <h2 class="govuk-heading-m">What happens next</h2>
        <p class="govuk-body">
          The service will calculate and store the performance for each facility using the energy and throughput data
          you uploaded.
        </p>

        <p class="govuk-!-margin-bottom-0">You can find the results of these calculations on:</p>
        <ul class="govuk-list govuk-list--bullet govuk-list--spaced">
          <li>the Reports tab of each individual facility</li>
          <li>the Reports tab for the Sector for all successfully processed facilities</li>
        </ul>

        <a class="govuk-link" routerLink="/dashboard" [replaceUrl]="true"> Return to: Dashboard </a>
      </div>
    </div>
  `,
  imports: [PanelComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TprCsvUploadConfirmationComponent {}
