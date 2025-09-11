import { inject } from '@angular/core';
import { CanActivateFn, createUrlTreeFromSnapshot, UrlTree } from '@angular/router';

import { SectorMoaTUDetailsStore } from '../sector-moa-tu-details.store';

export const canActivateMarkFacilitiesGuard: CanActivateFn = (route): boolean | UrlTree => {
  const sectorMoaTUDetailsStore = inject(SectorMoaTUDetailsStore);

  if (sectorMoaTUDetailsStore.state.moaTUDetails) return true;
  return createUrlTreeFromSnapshot(route, ['../']);
};
