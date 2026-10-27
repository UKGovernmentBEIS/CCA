import { Component, computed, inject, OnInit } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterOutlet } from '@angular/router';

import { map } from 'rxjs';

import { selectIsLoggedIn, selectUserRoleType, selectUserState } from '@netz/common/auth';
import { AuthStore } from '@netz/common/auth';
import { BackLinkComponent, BreadcrumbsComponent } from '@netz/common/navigation';
import {
  GovukFooterComponent,
  GovukHeaderComponent,
  GovukLinkItem,
  GovukLinkTarget,
  GovukServiceNavigationComponent,
  GovukServiceNavigationItem,
  SkipLinkComponent,
} from '@netz/govuk-components';
import { PhaseBarComponent, WorkflowTaskHeaderComponent } from '@shared/components';
import { ConfigService } from '@shared/config';
import { MainContentFocusDirective } from '@shared/directives';
import { AnalyticsService, AuthService, CountryService, CountyService, SignInTrackingService } from '@shared/services';

import { CookiesService } from './cookies/cookies.service';
import { CookiesContainerComponent } from './cookies/cookies-container.component';
import { TimeoutBannerComponent } from './timeout/timeout-banner/timeout-banner.component';

@Component({
  selector: 'cca-root',
  templateUrl: './app.component.html',
  imports: [
    CookiesContainerComponent,
    GovukHeaderComponent,
    GovukServiceNavigationComponent,
    SkipLinkComponent,
    PhaseBarComponent,
    BreadcrumbsComponent,
    RouterOutlet,
    BackLinkComponent,
    GovukFooterComponent,
    TimeoutBannerComponent,
    WorkflowTaskHeaderComponent,
    MainContentFocusDirective,
  ],
})
export class AppComponent implements OnInit {
  private readonly authStore = inject(AuthStore);
  private readonly cookiesService = inject(CookiesService);
  private readonly analyticsService = inject(AnalyticsService);
  private readonly configService = inject(ConfigService);
  private readonly signInTrackingService = inject(SignInTrackingService);
  protected readonly authService = inject(AuthService);
  protected readonly countryService = inject(CountryService);
  protected readonly countyService = inject(CountyService);

  private readonly userState = this.authStore.select(selectUserState);
  protected readonly roleType = this.authStore.select(selectUserRoleType);
  protected readonly isLoggedIn = this.authStore.select(selectIsLoggedIn);
  protected readonly subsistenceFeesHideMenu = this.configService.isFeatureEnabled('subsistenceFeesHideMenu');

  protected readonly isAuthorized = computed(() => this.isLoggedIn() && this.userState().status === 'ENABLED');
  protected readonly showRegulators = computed(() => this.isAuthorized() && this.roleType() === 'REGULATOR');
  protected readonly showSectors = computed(() => this.isAuthorized());

  protected readonly serviceHomepage = computed<GovukLinkTarget>(() =>
    this.isAuthorized() ? { routerLink: '/dashboard' } : { routerLink: '/landing' },
  );

  protected readonly navigation = computed<GovukServiceNavigationItem[]>(() => {
    const items: GovukServiceNavigationItem[] = [];
    if (!this.isAuthorized()) return items;

    items.push({ text: 'Tasks', routerLink: ['/dashboard'] });
    items.push({ text: 'Target unit accounts', routerLink: ['/target-unit-accounts'] });

    if (this.showSectors()) {
      items.push({ text: 'Manage sectors', routerLink: ['/sectors'] });

      if (this.roleType() === 'SECTOR_USER') {
        items.push({ text: 'Templates', routerLink: ['/sector-templates'] });
      }
    }

    if (this.showRegulators()) {
      if (!this.subsistenceFeesHideMenu) {
        items.push({ text: 'Subsistence fees', routerLink: ['/subsistence-fees'] });
      }

      items.push({
        text: 'Buy-out and surplus',
        routerLink: ['/buyout-surplus'],
        queryParams: { paymentStatus: 'AWAITING_PAYMENT' },
      });
      items.push({ text: 'Regulator users', routerLink: ['/user/regulators'] });
      items.push({ text: 'MI Reports', routerLink: ['/mi-reports'] });
      items.push({ text: 'Templates', routerLink: ['/templates'] });
    }

    return items;
  });

  protected readonly footerMeta: GovukLinkItem[] = [
    { text: 'Privacy Notice', routerLink: '/privacy-notice' },
    { text: 'Contact us', routerLink: '/contact-us' },
    { text: 'Accessibility Statement', routerLink: '/accessibility' },
    { text: 'CCA legislation', routerLink: '/legislation' },
  ];

  protected readonly showCookiesBanner = toSignal(
    this.cookiesService.accepted$.pipe(map((cookiesAccepted) => !cookiesAccepted)),
  );

  ngOnInit() {
    if (this.cookiesService.accepted$.getValue() && this.cookiesService.hasAnalyticsConsent()) {
      this.analyticsService.enableGoogleTagManager();
    }

    const signInCompleted = this.signInTrackingService.consumeSignInStarted();

    // Only signed in page loads are reported. Sessions that could not be established (expired session, failed Keycloak
    // check, public sign-in and error pages) are treated as signed out and skipped.
    if (!this.isLoggedIn()) return;

    this.analyticsService.push({
      event: 'service_context',
      // sector_user | regulator | operator, or unknown when the role is missing; unfamiliar roles pass through
      user_type: (this.roleType() || 'unknown').toLowerCase(),
      ...(signInCompleted ? { login: true } : {}),
    });
  }
}
