import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { CookiesPopUpComponent } from '@netz/govuk-components';
import { AnalyticsService } from '@shared/services';

import { CookiesService } from './cookies.service';

@Component({
  selector: 'cca-cookies-container',
  template: `
    <govuk-cookies-pop-up
      cookiesExpirationTime="1"
      [areBrowserCookiesEnabled]="cookiesEnabled"
      [cookiesAccepted]="cookiesAccepted$ | async"
      (cookiesAcceptedEmitter)="acceptCookies($event)"
    />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CookiesPopUpComponent, AsyncPipe],
})
export class CookiesContainerComponent {
  private readonly cookiesService = inject(CookiesService);
  private readonly analyticsService = inject(AnalyticsService);

  protected readonly cookiesEnabled = this.cookiesService.cookiesEnabled();
  protected readonly cookiesAccepted$ = this.cookiesService.accepted$;

  acceptCookies(expired: string) {
    this.cookiesService.acceptAllCookies(+expired);
    this.analyticsService.enableGoogleTagManager();
  }
}
