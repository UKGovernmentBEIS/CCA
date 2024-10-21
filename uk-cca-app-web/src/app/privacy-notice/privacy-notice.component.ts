import { ChangeDetectionStrategy, Component } from '@angular/core';

import { BackToTopComponent, PageHeadingComponent } from '@shared/components';

@Component({
  selector: 'cca-privacy-notice',
  standalone: true,
  templateUrl: './privacy-notice.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [PageHeadingComponent, BackToTopComponent],
})
export class PrivacyNoticeComponent {}
