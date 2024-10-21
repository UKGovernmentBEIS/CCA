import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';

import { SectorAssociationSchemeDTO, SectorAssociationSchemeService } from 'cca-api';

export const CreateTargetUnitSubSectorResolver: ResolveFn<SectorAssociationSchemeDTO> = (route) => {
  const sectorAssociationSchemeService = inject(SectorAssociationSchemeService);
  const sectorId = +route.paramMap.get('sectorId');
  if (!sectorId) throw new Error('sector id param not found');
  return sectorAssociationSchemeService.getSectorAssociationSchemeBySectorAssociationId(sectorId);
};
