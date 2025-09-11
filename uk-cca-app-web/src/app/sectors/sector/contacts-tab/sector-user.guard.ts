import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';

import { combineLatest, map, tap } from 'rxjs';

import { AuthStore, selectUserId } from '@netz/common/auth';

import { SectorAssociationAuthoritiesService, SectorUsersService } from 'cca-api';

import { ActiveSectorUserStore } from './active-sector-user.store';

export const SectorUserGuard: CanActivateFn = (route) => {
  const sectorUsersService = inject(SectorUsersService);
  const sectorAssociationAuthoritiesService = inject(SectorAssociationAuthoritiesService);
  const store = inject(ActiveSectorUserStore);

  const currentUserId = inject(AuthStore).select(selectUserId);

  const sectorId = +route.paramMap.get('sectorId');
  const sectorUserId = route.paramMap.get('sectorUserId');
  const isCurrentUser = currentUserId() === sectorUserId;

  return combineLatest([
    isCurrentUser
      ? sectorUsersService.getCurrentSectorUser(sectorId)
      : sectorUsersService.getSectorUserById(sectorId, sectorUserId),
    sectorAssociationAuthoritiesService.getSectorUserAuthoritiesBySectorAssociationId(sectorId),
  ]).pipe(
    tap(([sectorUserAuthorityDetailsDTO, sectorUsersAuthoritiesInfoDTO]) => {
      store.setState({
        editable: sectorUsersAuthoritiesInfoDTO.editable,
        details: sectorUserAuthorityDetailsDTO,
      });
    }),
    map(() => true),
  );
};
