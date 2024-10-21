import { ChangeDetectionStrategy, Component } from '@angular/core';

import { BackToTopComponent, PageHeadingComponent } from '@shared/components';

@Component({
  selector: 'cca-request-two-fa-reset',
  templateUrl: './request-two-fa-reset.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [PageHeadingComponent, BackToTopComponent],
})
export class RequestTwoFaResetComponent {}
