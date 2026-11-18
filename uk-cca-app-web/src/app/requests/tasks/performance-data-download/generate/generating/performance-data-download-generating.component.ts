import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { NotificationBannerComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-performance-data-download-generating',
  template: `
    <govuk-notification-banner>
      <h3 class="govuk-heading-m">Files are being generated</h3>
      <span>
        This may take several minutes to complete.
        <br />
        You can wait or return to the dashboard and click on the task to continue.
      </span>
    </govuk-notification-banner>
    <a class="govuk-link" routerLink="/dashboard" [replaceUrl]="true">Return to: Dashboard</a>
  `,
  imports: [NotificationBannerComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PerformanceDataDownloadGeneratingComponent {}
