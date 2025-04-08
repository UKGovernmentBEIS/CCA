import { inject } from '@angular/core';
import { Router } from '@angular/router';

import { AuthStore, selectUserRoleType, selectUserState } from '@netz/common/auth';
import { hasNoAuthority, loginEnabled } from '@shared/utils';

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
