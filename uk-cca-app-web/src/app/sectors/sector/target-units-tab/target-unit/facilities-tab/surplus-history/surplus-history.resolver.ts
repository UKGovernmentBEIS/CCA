import { createUrlTreeFromSnapshot, RedirectCommand, ResolveFn } from '@angular/router';

import { map } from 'rxjs';

import { FacilityInfoDTO } from 'cca-api';

import { FacilityDetailsResolver } from '../facility-details.resolver';

export const FacilitySurplusHistoryResolver: ResolveFn<FacilityInfoDTO> = (route) =>
  FacilityDetailsResolver(route).pipe(
    map((details) =>
      details.surplusDetails?.schemeVersion === 'CCA_3' && details.surplusDetails.hasHistory
        ? details
        : new RedirectCommand(createUrlTreeFromSnapshot(route, ['../'], {}, 'details'), { replaceUrl: true }),
    ),
  );
