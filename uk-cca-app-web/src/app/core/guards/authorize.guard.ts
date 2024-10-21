import { inject } from '@angular/core';
import { Router } from '@angular/router';

import { hasNoAuthority, loginEnabled } from '@core/util/user-status-util';
import { AuthStore, selectUserRoleType, selectUserState } from '@netz/common/auth';

export function AuthorizeGuard() {
  const router = inject(Router);
  const authStore = inject(AuthStore);

  const userState = authStore.select(selectUserState)();
  const userRoleType = authStore.select(selectUserRoleType)();

  if (loginEnabled(userState) || (hasNoAuthority(userState) && ['REGULATOR', 'SECTOR_USER'].includes(userRoleType))) {
    return true;
  }

  return router.parseUrl('landing');
}
