import { Routes } from '@angular/router';

import {
  facilityCalculationResolver,
  facilityDetailsResolver,
  facilityDetailsTitleResolver,
} from './facility-details/facility-details.resolver';
export const CCA3_BUYOUT_SURPLUS_ROUTES: Routes = [
  {
    title: facilityDetailsTitleResolver,
    path: 'facility/:facilityId/:targetPeriod',
    loadComponent: () =>
      import('./facility-details/facility-details.component').then((c) => c.FacilityDetailsComponent),
    data: { backlink: '../../../../', breadcrumb: false },
    resolve: { facilityDetails: facilityDetailsResolver, facilityCalculation: facilityCalculationResolver },
  },
];
