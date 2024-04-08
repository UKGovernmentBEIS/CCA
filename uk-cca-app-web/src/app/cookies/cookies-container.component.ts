import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';

import { toggleAnalytics } from '@core/analytics';

import { CookiesPopUpComponent } from 'govuk-components';

import { CookiesService } from './cookies.service';

@Component({
  selector: 'cca-cookies-container',
  standalone: true,
  template: `
    <govuk-cookies-pop-up
      cookiesExpirationTime="1"
      [areBrowserCookiesEnabled]="cookiesEnabled"
      [cookiesAccepted]="cookiesAccepted$ | async"
      (cookiesAcceptedEmitter)="acceptCookies($event)"
    >
    </govuk-cookies-pop-up>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CookiesPopUpComponent, AsyncPipe],
})
export class CookiesContainerComponent {
  constructor(private cookiesService: CookiesService) {}
  cookiesEnabled = this.cookiesService.cookiesEnabled();
  cookiesAccepted$ = this.cookiesService.accepted$;

  acceptCookies(expired: string) {
    this.cookiesService.acceptAllCookies(+expired);
    toggleAnalytics(true);
  }
}
