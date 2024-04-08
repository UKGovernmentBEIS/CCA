import { inject } from '@angular/core';
import { Router, UrlTree } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { AuthStore, selectIsLoggedIn, selectUserState } from '@core/store/auth';
import { hasNoAuthority, shouldShowAccepted, shouldShowDisabled } from '@core/util/user-status-util';

export function LandingPageGuard(): Observable<boolean | UrlTree> {
  const authStore = inject(AuthStore);
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.checkUser().pipe(
    switchMap(() => combineLatest([authStore.pipe(selectIsLoggedIn), authStore.pipe(selectUserState)])),
    map(([isLoggedIn, userState]) => {
      if (!isLoggedIn) {
        return true;
      }

      if (['REGULATOR', 'VERIFIER'].includes(userState.roleType) && hasNoAuthority(userState)) {
        return router.parseUrl('dashboard');
      }

      if (
        shouldShowDisabled(userState) ||
        hasNoAuthority(userState) ||
        shouldShowAccepted(userState) ||
        router.getCurrentNavigation()?.extras?.state?.addAnotherInstallation
      ) {
        return true;
      }

      return router.parseUrl('dashboard');
    }),
    first(),
  );
}
