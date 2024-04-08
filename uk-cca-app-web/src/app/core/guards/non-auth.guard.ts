import { inject } from '@angular/core';
import { Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { AuthStore, selectIsLoggedIn } from '@core/store/auth';

export function NonAuthGuard() {
  const router = inject(Router);
  const authService = inject(AuthService);
  const authStore = inject(AuthStore);
  return authService.checkUser().pipe(
    switchMap(() => authStore.pipe(selectIsLoggedIn)),
    map((isLoggedIn) => !isLoggedIn || router.parseUrl('landing')),
    first(),
  );
}
