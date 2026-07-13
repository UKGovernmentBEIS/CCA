import { inject } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';

import { Observable } from 'rxjs';

import { SectorAssociationSchemesDTO, SectorAssociationSchemeService } from 'cca-api';

export function SectorAssociationSchemeResolver(
  route: ActivatedRouteSnapshot,
): Observable<SectorAssociationSchemesDTO> {
  const sectorAssociationSchemeService = inject(SectorAssociationSchemeService);
  const sectorId = +route.paramMap.get('sectorId');

  return sectorAssociationSchemeService.getSectorAssociationSchemeBySectorAssociationId(sectorId);
}
