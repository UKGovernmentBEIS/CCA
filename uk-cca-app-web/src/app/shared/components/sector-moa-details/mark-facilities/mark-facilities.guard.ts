import { inject } from '@angular/core';
import { CanActivateFn, createUrlTreeFromSnapshot, UrlTree } from '@angular/router';

import { SectorMoaDetailsStore } from '../sector-moa-details.store';

export const canActivateMarkFacilitiesGuard: CanActivateFn = (route): boolean | UrlTree => {
  const sectorMoaDetailsStore = inject(SectorMoaDetailsStore);

  if (sectorMoaDetailsStore.state.sectorMoaDetails) return true;
  return createUrlTreeFromSnapshot(route, ['../']);
};
