import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn } from '@angular/router';

import { AuthStore, selectUserId } from '@netz/common/auth';

import { ActiveSectorUserStore } from '../active-sector-user.store';

export const CanEditSectorUserGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(ActiveSectorUserStore);
  const sectorUserId = route.paramMap.get('sectorUserId');
  const currentUserId = inject(AuthStore).select(selectUserId);
  const isCurrentUser = currentUserId() === sectorUserId;
  return isCurrentUser || store.state.editable;
};
