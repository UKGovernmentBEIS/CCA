import { inject } from '@angular/core';
import { Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AuthStore } from '@netz/common/auth';
import { AuthService, LatestTermsStore } from '@shared/services';

import { selectIsFeatureEnabled } from '../config/config.selectors';
import { ConfigStore } from '../config/config.store';

export function TermsAndConditionsGuard(_, state: RouterStateSnapshot): Observable<true | UrlTree> {
  const router = inject(Router);
  const authService = inject(AuthService);
  const authStore = inject(AuthStore);
  const configStore = inject(ConfigStore);
  const latestTermsStore = inject(LatestTermsStore);

  return authService.checkUser().pipe(
    map(() => {
      const termsEnabled = configStore.select(selectIsFeatureEnabled('terms'));
      const latestTerms = latestTermsStore.state;
      const userTerms = authStore.state.userTerms;
      if (!termsEnabled) return true;

      if (state.url === '/terms') {
        return latestTerms.version !== userTerms.termsVersion || router.parseUrl('landing');
      }
      return latestTerms.version === userTerms.termsVersion || router.parseUrl('landing');
    }),
  );
}
