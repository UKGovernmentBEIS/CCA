import { inject } from '@angular/core';
import { Router } from '@angular/router';

import { first, map, withLatestFrom } from 'rxjs';

import { AuthStore, selectUserRoleType, selectUserState } from '@core/store/auth';
import { hasNoAuthority, loginEnabled } from '@core/util/user-status-util';

export function InstallationAuthGuard() {
  const router = inject(Router);
  const store = inject(AuthStore);
  return store.pipe(selectUserState).pipe(
    first(),
    withLatestFrom(store.pipe(selectUserRoleType)),
    map(([userState, role]) => {
      if (loginEnabled(userState) || (hasNoAuthority(userState) && ['REGULATOR'].includes(role))) {
        return true;
      }
      return router.parseUrl('landing');
    }),
  );
}
