import { Component, computed, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

import { filter, map } from 'rxjs';

import { selectIsLoggedIn, selectUserRoleType, selectUserState } from '@netz/common/auth';
import { AuthStore } from '@netz/common/auth';
import { BackLinkComponent, BreadcrumbsComponent } from '@netz/common/navigation';
import {
  FooterComponent,
  HeaderActionsListComponent,
  HeaderComponent,
  HeaderNavListComponent,
  MetaInfoComponent,
  SkipLinkComponent,
} from '@netz/govuk-components';
import { PhaseBarComponent, WorkflowTaskHeaderComponent } from '@shared/components';
import { ConfigService } from '@shared/config';
import { AnalyticsService, AuthService, CountryService, CountyService } from '@shared/services';

import { CookiesService } from './cookies/cookies.service';
import { CookiesContainerComponent } from './cookies/cookies-container.component';
import { TimeoutBannerComponent } from './timeout/timeout-banner/timeout-banner.component';

@Component({
  selector: 'cca-root',
  templateUrl: './app.component.html',
  imports: [
    CookiesContainerComponent,
    HeaderComponent,
    SkipLinkComponent,
    HeaderActionsListComponent,
    HeaderNavListComponent,
    RouterLink,
    PhaseBarComponent,
    BreadcrumbsComponent,
    RouterOutlet,
    BackLinkComponent,
    FooterComponent,
    TimeoutBannerComponent,
    MetaInfoComponent,
    WorkflowTaskHeaderComponent,
    RouterLinkActive,
  ],
})
export class AppComponent implements OnInit {
  private readonly destroyRef = inject(DestroyRef);
  private readonly authStore = inject(AuthStore);
  private readonly titleService = inject(Title);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly cookiesService = inject(CookiesService);
  private readonly analyticsService = inject(AnalyticsService);
  private readonly configService = inject(ConfigService);
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

  protected readonly showCookiesBanner = toSignal(
    this.cookiesService.accepted$.pipe(map((cookiesAccepted) => !cookiesAccepted)),
  );

  ngOnInit() {
    const appTitle = this.titleService.getTitle();

    this.router.events
      .pipe(
        filter((event) => event instanceof NavigationEnd),
        map(() => {
          let child = this.activatedRoute.firstChild;

          while (child.firstChild) {
            child = child.firstChild;
          }

          if (child.snapshot.data['pageTitle']) {
            return child.snapshot.data['pageTitle'];
          }

          return appTitle;
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((title: string) => this.titleService.setTitle(`${title} - GOV.UK`));

    if (this.cookiesService.accepted$.getValue() && this.cookiesService.hasAnalyticsConsent()) {
      this.analyticsService.enableGoogleTagManager();
    }
  }
}
