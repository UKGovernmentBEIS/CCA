import { inject } from '@angular/core';

import { Observable } from 'rxjs';

import { CurrentFacilityId } from '@requests/common';

import { FacilityDataDetailsDTO, FacilityInfoViewService } from 'cca-api';

export const FacilityDetailsResolver = (): Observable<FacilityDataDetailsDTO> => {
  const currentFacility = inject(CurrentFacilityId);
  if (!currentFacility) throw new Error('no currentFacilityId');

  const facilityInfoViewService = inject(FacilityInfoViewService);
  return facilityInfoViewService.getFacilityDetailsById(currentFacility());
};
