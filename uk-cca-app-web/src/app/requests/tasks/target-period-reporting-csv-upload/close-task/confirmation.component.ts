import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { PanelComponent } from '@netz/govuk-components';

import { tprCSVUploadQuery } from '../target-period-reporting-csv-upload.selectors';

@Component({
  selector: 'cca-close-task-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="Target period report closed"></govuk-panel>

        @if (processingStatus() === 'COMPLETED') {
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
        }

        <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
        <a class="govuk-link" routerLink="/dashboard" [replaceUrl]="true"> Return to: Dashboard </a>
      </div>
    </div>
  `,
  imports: [RouterLink, PanelComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CloseTaskConfirmationComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly processingStatus = this.requestTaskStore.select(tprCSVUploadQuery.selectProcessingStatus);
}
