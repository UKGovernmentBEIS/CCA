import { inject } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';

import { Observable } from 'rxjs';

import { FacilityInfoDTO, FacilityInfoViewService } from 'cca-api';

export const FacilityDetailsResolver = (route: ActivatedRouteSnapshot): Observable<FacilityInfoDTO> => {
  const facilityId = +route.paramMap.get('facilityId');

  const facilityInfoViewService = inject(FacilityInfoViewService);
  return facilityInfoViewService.getFacilityDetailsById(facilityId);
};
