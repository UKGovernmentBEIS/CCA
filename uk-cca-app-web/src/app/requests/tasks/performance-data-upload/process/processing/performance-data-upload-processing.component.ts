import { ChangeDetectionStrategy, Component } from '@angular/core';

import { NotificationBannerComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-performance-data-upload-processing',
  standalone: true,
  imports: [NotificationBannerComponent],
  template: `
    <govuk-notification-banner>
      <h3 class="govuk-heading-m">Your files are being uploaded</h3>
      <p class="govuk-body">This page will automatically update once the files are uploaded.</p>
    </govuk-notification-banner>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PerformanceDataUploadProcessingComponent {}
