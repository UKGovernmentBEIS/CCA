import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';

import { SectorAssociationSchemesDTO, SectorAssociationSchemeService } from 'cca-api';

export const CreateTargetUnitSubSectorResolver: ResolveFn<SectorAssociationSchemesDTO> = (route) => {
  const sectorAssociationSchemeService = inject(SectorAssociationSchemeService);
  const sectorId = +route.paramMap.get('sectorId');
  if (!sectorId) throw new Error('sector id param not found');
  return sectorAssociationSchemeService.getSectorAssociationSchemeBySectorAssociationId(sectorId);
};
