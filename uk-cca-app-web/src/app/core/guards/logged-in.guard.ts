import { inject } from '@angular/core';
import { Router } from '@angular/router';

import { first, map } from 'rxjs';

import { AuthStore, selectIsLoggedIn } from '@core/store/auth';

export function LoggedInGuard() {
  const router = inject(Router);
  const store = inject(AuthStore);
  return store.pipe(
    selectIsLoggedIn,
    map((isLoggedIn) => {
      if (!isLoggedIn) {
        return router.parseUrl('landing');
      }
      return true;
    }),
    first(),
  );
}
