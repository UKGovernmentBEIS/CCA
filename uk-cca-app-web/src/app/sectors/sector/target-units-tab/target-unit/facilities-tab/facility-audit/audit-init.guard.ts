import { inject } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';

import { map } from 'rxjs';

import { FacilityAuditStore } from './facility-audit.store';

export const initFacilityAuditGuard = (route: ActivatedRouteSnapshot) =>
  inject(FacilityAuditStore)
    .init(route.params.facilityId)
    .pipe(map(() => true));
