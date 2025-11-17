import { ChangeDetectionStrategy, Component } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { BackToTopComponent } from '@shared/components';

@Component({
  selector: 'cca-privacy-notice',
  templateUrl: './privacy-notice.component.html',
  imports: [PageHeadingComponent, BackToTopComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PrivacyNoticeComponent {}
