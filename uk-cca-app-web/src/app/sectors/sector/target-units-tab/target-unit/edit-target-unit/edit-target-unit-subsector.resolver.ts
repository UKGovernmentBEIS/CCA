import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';

import { SectorAssociationSchemesDTO, SectorAssociationSchemeService } from 'cca-api';

import { ActiveTargetUnitStore } from '../../active-target-unit.store';

export const EditTargetUnitSubSectorResolver: ResolveFn<SectorAssociationSchemesDTO> = () => {
  const store = inject(ActiveTargetUnitStore);
  const sectorAssociationSchemeService = inject(SectorAssociationSchemeService);

  const sectorAssociationId = store.state.targetUnitAccountDetails?.sectorAssociationId;
  if (!sectorAssociationId) throw new Error('sectorAssociationId not found');

  return sectorAssociationSchemeService.getSectorAssociationSchemeBySectorAssociationId(sectorAssociationId);
};
