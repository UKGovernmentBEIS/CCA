import { inject } from '@angular/core';
import { Router } from '@angular/router';

import { first, map } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { AuthStore, selectIsLoggedIn } from '@netz/common/auth';

export function NonAuthGuard() {
  const router = inject(Router);
  const authService = inject(AuthService);
  const authStore = inject(AuthStore);

  return authService.checkUser().pipe(
    first(),
    map(() => !authStore.select(selectIsLoggedIn)() || router.parseUrl('landing')),
  );
}
