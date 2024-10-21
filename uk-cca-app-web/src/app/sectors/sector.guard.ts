import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, tap } from 'rxjs';

import { SectorAssociationInfoViewService } from 'cca-api';

import { ActiveSectorStore } from './sector/active-sector.store';

export const SectorGuard: CanActivateFn = (route) => {
  const sectorAssociationInfoViewService = inject(SectorAssociationInfoViewService);
  const store = inject(ActiveSectorStore);
  return sectorAssociationInfoViewService.getSectorAssociationById(+route.paramMap.get('sectorId')).pipe(
    tap((sectorAssociationInfo) => store.setState(sectorAssociationInfo)),
    map(() => true),
  );
};
export const RefreshSector: CanDeactivateFn<unknown> = (_, route) => {
  const sectorAssociationInfoViewService = inject(SectorAssociationInfoViewService);
  const store = inject(ActiveSectorStore);
  return sectorAssociationInfoViewService.getSectorAssociationById(+route.paramMap.get('sectorId')).pipe(
    tap((sectorAssociationInfo) => store.setState(sectorAssociationInfo)),
    map(() => true),
  );
};
