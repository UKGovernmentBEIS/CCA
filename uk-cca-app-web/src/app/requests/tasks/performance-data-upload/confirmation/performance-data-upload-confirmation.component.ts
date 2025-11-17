import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { PanelComponent } from '@netz/govuk-components';

import { performanceDataUploadQuery } from '../+state/performance-data-upload-selectors';

@Component({
  selector: 'cca-performance-data-upload-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        @if (errorMessage()) {
          <govuk-panel>TPR spreadsheets could not be uploaded</govuk-panel>
          <p>
            You can try uploading the files again. Contact
            <a class="govuk-link" href="mailto:cca-help@environment-agency.gov.uk">
              cca-help&#64;environment-agency.gov.uk
            </a>
            if you need further help.
          </p>
        } @else {
          <govuk-panel>Process completed</govuk-panel>
          <p>Refer to the Upload_summary.csv file to see the errors for files not uploaded successfully.</p>
        }
        <a class="govuk-link" routerLink="/dashboard" [replaceUrl]="true">Return to: Dashboard</a>
      </div>
    </div>
  `,
  imports: [PanelComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PerformanceDataUploadConfirmationComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly errorMessage = this.requestTaskStore.select(performanceDataUploadQuery.selectErrorMessage);
}
