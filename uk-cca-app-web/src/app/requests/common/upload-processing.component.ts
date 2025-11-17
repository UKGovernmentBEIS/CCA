import { ChangeDetectionStrategy, Component } from '@angular/core';

import { NotificationBannerComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-upload-processing',
  template: `
    <govuk-notification-banner data-testid="upload-processing-banner">
      <h3 class="govuk-heading-m">Your files are being uploaded</h3>
      <p>This page will automatically update once the files are uploaded.</p>
    </govuk-notification-banner>
  `,
  imports: [NotificationBannerComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UploadProcessingComponent {}
