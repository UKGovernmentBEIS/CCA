import { inject } from '@angular/core';
import { Router } from '@angular/router';

import { AuthStore, selectIsLoggedIn } from '@netz/common/auth';

export function LoggedInGuard() {
  const router = inject(Router);
  const authStore = inject(AuthStore);

  if (!authStore.select(selectIsLoggedIn)()) {
    return router.parseUrl('landing');
  }

  return true;
}
