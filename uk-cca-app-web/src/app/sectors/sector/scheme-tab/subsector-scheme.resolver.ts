import { inject } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';

import { Observable } from 'rxjs';

import { SubsectorAssociationInfoViewService, SubsectorAssociationSchemesDTO } from 'cca-api';

export function SubsectorAssociationSchemeResolver(
  route: ActivatedRouteSnapshot,
): Observable<SubsectorAssociationSchemesDTO> {
  const subsectorAssociationInfoViewService = inject(SubsectorAssociationInfoViewService);
  const sectorId = +route.paramMap.get('sectorId');
  const subSectorId = +route.paramMap.get('subId');

  return subsectorAssociationInfoViewService.getSubsectorAssociationSchemeBySubsectorAssociationId(
    sectorId,
    subSectorId,
  );
}
