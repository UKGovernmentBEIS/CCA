import { ChangeDetectionStrategy, Component } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { BackToTopComponent } from '@shared/components';

@Component({
  selector: 'cca-privacy-notice',
  standalone: true,
  templateUrl: './privacy-notice.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [PageHeadingComponent, BackToTopComponent],
})
export class PrivacyNoticeComponent {}
