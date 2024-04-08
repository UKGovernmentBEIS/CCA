import { inject } from '@angular/core';
import { Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { first, map, Observable, switchMap } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { AuthStore } from '@core/store/auth';

export function TermsAndConditionsGuard(_, state: RouterStateSnapshot): Observable<true | UrlTree> {
  const router = inject(Router);
  const authService = inject(AuthService);
  const authStore = inject(AuthStore);
  return authService.checkUser().pipe(
    switchMap(() => authStore),
    map(({ terms, user }) => {
      if (state.url === '/terms') {
        return terms.version !== user.termsVersion || router.parseUrl('landing');
      }

      return terms.version === user.termsVersion || router.parseUrl('landing');
    }),
    first(),
  );
}
