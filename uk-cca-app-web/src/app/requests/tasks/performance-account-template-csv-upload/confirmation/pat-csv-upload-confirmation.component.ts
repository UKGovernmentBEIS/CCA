import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PanelComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-pat-csv-upload-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="PAT report submitted"></govuk-panel>

        <h2 class="govuk-heading-m">What happens next</h2>
        <p>The service will store the submitted data for each facility.</p>
        <p>You can find this data in the reports tab of the facility.</p>

        <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
        <a class="govuk-link" routerLink="/dashboard" [replaceUrl]="true"> Return to: Dashboard </a>
      </div>
    </div>
  `,
  imports: [PanelComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PatCsvUploadConfirmationComponent {}
