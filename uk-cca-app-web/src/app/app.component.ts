import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';

import { combineLatest, filter, map, Observable, of, switchMap, takeUntil } from 'rxjs';

import { gtagIsAvailable, toggleAnalytics } from '@core/analytics';
import { AuthService } from '@core/services/auth.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { selectIsLoggedIn, selectUserState } from '@core/store/auth/auth.selectors';
import { AuthStore } from '@core/store/auth/auth.store';
import { hasNoAuthority, loginDisabled } from '@core/util/user-status-util';

import { CookiesService } from './cookies/cookies.service';

interface Permissions {
  showRegulators: boolean;
  showAuthorizedOperators: boolean;
}

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'cca-root',
  templateUrl: './app.component.html',
  providers: [DestroySubject],
})
export class AppComponent implements OnInit {
  permissions$: Observable<null | Permissions>;
  isLoggedIn$ = this.authStore.pipe(selectIsLoggedIn, takeUntil(this.destroy$));
  showCookiesBanner$ = this.cookiesService.accepted$.pipe(
    map((cookiesAccepted) => !cookiesAccepted && gtagIsAvailable()),
  );
  private readonly userState$ = this.authStore.pipe(selectUserState, takeUntil(this.destroy$));
  private readonly roleType$ = this.userState$.pipe(
    map((userState) => userState?.roleType),
    takeUntil(this.destroy$),
  );

  constructor(
    public readonly authStore: AuthStore,
    public readonly authService: AuthService,
    private readonly titleService: Title,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
    private readonly cookiesService: CookiesService,
  ) {}

  ngOnInit(): void {
    this.permissions$ = combineLatest([this.isLoggedIn$]).pipe(
      switchMap(([isLoggedIn]) =>
        isLoggedIn
          ? combineLatest([
              this.userState$.pipe(map((userState) => loginDisabled(userState))),
              this.roleType$.pipe(map((role) => role === 'REGULATOR')),
              this.userState$.pipe(
                map(
                  (userState) => userState !== null && userState.roleType === 'OPERATOR' && !hasNoAuthority(userState),
                ),
              ),
            ]).pipe(
              map(
                ([isDisabled, showRegulators, showAuthorizedOperators]) =>
                  !isDisabled &&
                  Object.values({ showRegulators, showAuthorizedOperators }).some((permission) => !!permission) && {
                    showRegulators,
                    showAuthorizedOperators,
                  },
              ),
            )
          : of(null),
      ),
    );

    const appTitle = this.titleService.getTitle();

    this.router.events
      .pipe(
        filter((event) => event instanceof NavigationEnd),
        map(() => {
          let child = this.route.firstChild;
          while (child.firstChild) {
            child = child.firstChild;
          }
          if (child.snapshot.data['pageTitle']) {
            return child.snapshot.data['pageTitle'];
          }
          return appTitle;
        }),
        takeUntil(this.destroy$),
      )
      .subscribe((title: string) => this.titleService.setTitle(`${title} - GOV.UK`));

    if (this.cookiesService.accepted$.getValue()) {
      toggleAnalytics(true);
    }
  }
}
