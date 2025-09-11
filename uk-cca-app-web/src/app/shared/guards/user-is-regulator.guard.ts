import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, createUrlTreeFromSnapshot } from '@angular/router';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';

export function userIsRegulatorGuard(route: ActivatedRouteSnapshot) {
  const authStore = inject(AuthStore);
  const roleType = authStore.select(selectUserRoleType);
  return roleType() === 'REGULATOR' ? true : createUrlTreeFromSnapshot(route, ['../../']);
}
