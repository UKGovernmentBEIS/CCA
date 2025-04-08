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
import { AnalyticsService, AuthService } from '@shared/services';

import { CookiesService } from './cookies/cookies.service';
import { CookiesContainerComponent } from './cookies/cookies-container.component';
import { TimeoutBannerComponent } from './timeout/timeout-banner/timeout-banner.component';

@Component({
  selector: 'cca-root',
  templateUrl: './app.component.html',
  standalone: true,
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

  private readonly userState = this.authStore.select(selectUserState);
  private readonly roleType = this.authStore.select(selectUserRoleType);
  readonly isLoggedIn = this.authStore.select(selectIsLoggedIn);
  readonly subsistenceFeesHideMenu = this.configService.isFeatureEnabled('subsistenceFeesHideMenu');

  isAuthorized = computed(() => this.isLoggedIn() && this.userState().status === 'ENABLED');
  showRegulators = computed(() => this.isAuthorized() && this.roleType() === 'REGULATOR');
  showSectors = computed(() => this.isAuthorized());

  showCookiesBanner = toSignal(this.cookiesService.accepted$.pipe(map((cookiesAccepted) => !cookiesAccepted)));

  ngOnInit(): void {
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
