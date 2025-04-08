import { inject } from '@angular/core';
import { Router, UrlTree } from '@angular/router';

import { map, Observable, take } from 'rxjs';

import { AuthStore, selectIsLoggedIn } from '@netz/common/auth';
import { AuthService, LatestTermsStore } from '@shared/services';
import { loginDisabled } from '@shared/utils';

import { selectIsFeatureEnabled } from '../config/config.selectors';
import { ConfigStore } from '../config/config.store';

export function AuthGuard(): Observable<boolean | UrlTree> {
  const router = inject(Router);
  const authService = inject(AuthService);
  const authStore = inject(AuthStore);
  const latestTermStore = inject(LatestTermsStore);
  const configStore = inject(ConfigStore);

  return authService.checkUser().pipe(
    take(1),
    map(() => {
      const userState = authStore.state.userState;
      const termsEnabled = configStore.select(selectIsFeatureEnabled('terms'));
      const isLoggedIn = authStore.select(selectIsLoggedIn);
      const hasValidTerms = latestTermStore.state.version === authStore.state.userTerms?.termsVersion;

      if (!isLoggedIn()) return router.parseUrl('/landing');
      if (termsEnabled() && !hasValidTerms) return router.parseUrl('/terms');
      if (!loginDisabled(userState) && (!termsEnabled() || hasValidTerms)) return true;

      return router.parseUrl('/landing');
    }),
  );
}
