import { createUrlTreeFromSnapshot, RedirectCommand, ResolveFn } from '@angular/router';

import { map } from 'rxjs';

import { FacilityInfoDTO } from 'cca-api';

import { FacilityDetailsResolver } from '../facility-details.resolver';

export const EditFacilitySurplusResolver: ResolveFn<FacilityInfoDTO> = (route) =>
  FacilityDetailsResolver(route).pipe(
    map((details) =>
      details.surplusDetails?.schemeVersion === 'CCA_3'
        ? details
        : new RedirectCommand(createUrlTreeFromSnapshot(route.parent, ['../'], {}, 'details'), {
            replaceUrl: true,
          }),
    ),
  );
