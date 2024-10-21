import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router } from '@angular/router';

import { map } from 'rxjs';

import { SectorAssociationAuthoritiesService } from 'cca-api';

import { RoleCode, RoleCodes } from './sector/types';

export const CanAddSectorUserGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const sectorAssociationService = inject(SectorAssociationAuthoritiesService);
  const router = inject(Router);
  const sectorId = route.paramMap?.get('sectorId');
  const role = route.queryParamMap?.get('role') as RoleCode | null;

  if (!sectorId || !role) return router.createUrlTree(['../']);

  return sectorAssociationService
    .getSectorUserAuthoritiesBySectorAssociationId(+sectorId)
    .pipe(map(({ editable }) => (editable && RoleCodes.includes(role) ? true : router.createUrlTree(['../']))));
};
