import { inject } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';

import { Observable } from 'rxjs';

import { SubsectorAssociationSchemeDTO, SubsectorAssociationSchemeService } from 'cca-api';

export function SubsectorAssociationSchemeResolver(
  route: ActivatedRouteSnapshot,
): Observable<SubsectorAssociationSchemeDTO> {
  const subsectorAssociationSchemeService = inject(SubsectorAssociationSchemeService);
  const sectorId = +route.paramMap.get('sectorId');
  const subSectorId = +route.paramMap.get('subId');

  return subsectorAssociationSchemeService.getSubsectorAssociationSchemeBySubsectorAssociationSchemeId(
    sectorId,
    subSectorId,
  );
}
