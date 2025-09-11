import { Routes } from '@angular/router';

import { canActivateMarkFacilitiesGuard } from './mark-facilities/mark-facilities.guard';

export const SECTOR_MOA_TU_DETAILS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./sector-moa-tu-details.component').then((c) => c.SectorMoaTuDetailsComponent),
  },
  {
    path: 'mark-facilities',
    canActivate: [canActivateMarkFacilitiesGuard],
    loadChildren: () => import('./mark-facilities/mark-facilities.routes').then((r) => r.MARK_FACILITIES_ROUTES),
  },
];
