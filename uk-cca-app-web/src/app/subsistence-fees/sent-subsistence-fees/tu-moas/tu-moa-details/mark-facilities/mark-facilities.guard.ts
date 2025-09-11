import { inject } from '@angular/core';
import { CanActivateFn, createUrlTreeFromSnapshot, UrlTree } from '@angular/router';

import { TuMoaDetailsStore } from '../tu-moa-details.store';

export const canActivateMarkFacilitiesGuard: CanActivateFn = (route): boolean | UrlTree => {
  const tuMoaDetailsStore = inject(TuMoaDetailsStore);

  if (tuMoaDetailsStore.state.moaTUDetails) return true;
  return createUrlTreeFromSnapshot(route, ['../']);
};
