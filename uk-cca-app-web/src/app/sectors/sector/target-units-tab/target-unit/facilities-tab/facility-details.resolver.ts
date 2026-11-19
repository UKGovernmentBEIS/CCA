import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, createUrlTreeFromSnapshot, RedirectCommand, ResolveFn } from '@angular/router';

import { map, Observable } from 'rxjs';

import { FacilityCertificationStatusDTO, FacilityInfoDTO, FacilityInfoViewService } from 'cca-api';

export const FacilityDetailsResolver = (route: ActivatedRouteSnapshot): Observable<FacilityInfoDTO> => {
  const facilityId = +route.paramMap.get('facilityId');

  const facilityInfoViewService = inject(FacilityInfoViewService);
  return facilityInfoViewService.getFacilityDetailsById(facilityId);
};

// Resolves the certification period requested in the url, or redirects back to the certification status tab
// when the api does not return it (e.g. a future or older period).
export const FacilityCertificationResolver: ResolveFn<FacilityCertificationStatusDTO | RedirectCommand> = (route) => {
  const facilityId = +route.paramMap.get('facilityId');
  const certificationPeriod = route.paramMap.get('certificationPeriod');

  const facilityInfoViewService = inject(FacilityInfoViewService);
  return facilityInfoViewService
    .getFacilityCertifications(facilityId)
    .pipe(
      map(
        (certifications) =>
          certifications.find((entry) => entry.certificationPeriod === certificationPeriod) ??
          new RedirectCommand(createUrlTreeFromSnapshot(route, ['../..'], null, 'certification-status')),
      ),
    );
};
