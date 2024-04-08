import { inject } from '@angular/core';
import { Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { AuthStore } from '@core/store/auth';
import { loginDisabled } from '@core/util/user-status-util';

export function AuthGuard() {
  const router = inject(Router);
  const authService = inject(AuthService);
  const store = inject(AuthStore);
  return authService.checkUser().pipe(
    switchMap(() => store),
    map(({ isLoggedIn, userState }) => {
      if (isLoggedIn && !loginDisabled(userState)) {
        return true;
      } else {
        return router.parseUrl('landing');
      }
    }),
    first(),
  );
}
