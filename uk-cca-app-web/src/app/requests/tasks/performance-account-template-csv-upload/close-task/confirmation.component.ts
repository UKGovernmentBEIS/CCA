import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { PanelComponent } from '@netz/govuk-components';

import { patCsvUploadQuery } from '../performance-account-template-csv-upload.selectors';

@Component({
  selector: 'cca-pat-csv-close-task-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="PAT report closed"></govuk-panel>

        @if (processingStatus() === 'COMPLETED') {
          <h2 class="govuk-heading-m">What happens next</h2>
          <p>The service will calculate and store the PAT performance for each facility using the data you uploaded.</p>

          <p class="govuk-!-margin-bottom-0">You can find the results of these calculations on:</p>
          <ul class="govuk-list govuk-list--bullet govuk-list--spaced">
            <li>the Reports tab of each individual facility</li>
            <li>the Reports tab for the Sector for all successfully processed facilities</li>
          </ul>
        }

        <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
        <a class="govuk-link" routerLink="/dashboard" [replaceUrl]="true"> Return to: Dashboard </a>
      </div>
    </div>
  `,
  imports: [RouterLink, PanelComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PatCsvCloseTaskConfirmationComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly processingStatus = this.requestTaskStore.select(patCsvUploadQuery.selectProcessingStatus);
}
