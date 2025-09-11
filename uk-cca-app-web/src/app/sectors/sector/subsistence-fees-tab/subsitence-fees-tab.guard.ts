import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';

export const IsAllowedUserGuard: CanActivateFn = () => {
  const authStore = inject(AuthStore);
  return ['REGULATOR', 'SECTOR_USER'].includes(authStore.select(selectUserRoleType)());
};
