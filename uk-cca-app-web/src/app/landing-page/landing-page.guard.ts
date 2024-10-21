import { inject } from '@angular/core';
import { Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { selectIsFeatureEnabled } from '@core/config/config.selectors';
import { ConfigStore } from '@core/config/config.store';
import { AuthService } from '@core/services/auth.service';
import { LatestTermsStore } from '@core/store/latest-terms.store';
import { hasNoAuthority, shouldShowDisabled, statusAccepted } from '@core/util/user-status-util';
import { AuthStore, selectIsLoggedIn, selectUserRoleType, selectUserState, selectUserTerms } from '@netz/common/auth';

export function LandingPageGuard(): Observable<boolean | UrlTree> {
  const authStore = inject(AuthStore);
  const authService = inject(AuthService);
  const router = inject(Router);
  const configStore = inject(ConfigStore);
  const latestTermsStore = inject(LatestTermsStore);

  return authService.checkUser().pipe(
    first(),
    map(() => {
      const userState = authStore.select(selectUserState)();
      const userTerms = authStore.select(selectUserTerms)();
      const userRoleType = authStore.select(selectUserRoleType)();
      const termsFeatureEnabled = configStore.select(selectIsFeatureEnabled('terms'));
      const latestTerms = latestTermsStore.state;

      if (!authStore.select(selectIsLoggedIn)()) {
        return true;
      }

      if (!userRoleType) {
        return true;
      }

      if (termsFeatureEnabled && latestTerms.version !== userTerms.termsVersion) {
        return router.parseUrl('terms');
      }

      if (['REGULATOR', 'SECTOR_USER'].includes(userRoleType) && hasNoAuthority(userState)) {
        return router.parseUrl('dashboard');
      }

      if (hasNoAuthority(userState) || shouldShowDisabled(userState) || statusAccepted(userState)) {
        return true;
      }

      return router.parseUrl('dashboard');
    }),
  );
}
