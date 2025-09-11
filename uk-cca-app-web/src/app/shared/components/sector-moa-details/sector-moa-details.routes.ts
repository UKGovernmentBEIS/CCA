import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { SectorMoasReceivedAmountStore } from '@shared/components';

import { canActivateMarkFacilitiesGuard } from './mark-facilities/mark-facilities.guard';
import { SectorMoaTUDetailsStore } from './sector-moa-tu-details/sector-moa-tu-details.store';

export const SECTOR_MOA_DETAILS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./sector-moa-details.component').then((c) => c.SectorMoaDetailsComponent),
  },
  {
    path: 'tu-details',
    providers: [SectorMoaTUDetailsStore],
    canActivate: [
      () => {
        inject(SectorMoaTUDetailsStore).initialize();
        return true;
      },
    ],
    canDeactivate: [
      () => {
        inject(SectorMoaTUDetailsStore).reset();
        return true;
      },
    ],
    children: [
      {
        path: ':moaTUId',
        loadChildren: () =>
          import('./sector-moa-tu-details/sector-moa-tu-details.routes').then((r) => r.SECTOR_MOA_TU_DETAILS_ROUTES),
      },
    ],
  },
  {
    path: 'received-amount',
    providers: [SectorMoasReceivedAmountStore],
    canDeactivate: [
      () => {
        inject(SectorMoasReceivedAmountStore).reset();
        return true;
      },
    ],
    loadChildren: () => import('@shared/components').then((r) => r.SECTOR_MOAS_RECEIVED_AMOUNT_ROUTES),
  },
  {
    path: 'mark-facilities',
    canActivate: [canActivateMarkFacilitiesGuard],
    loadChildren: () => import('./mark-facilities/mark-facilities.routes').then((r) => r.MARK_FACILITIES_ROUTES),
  },
];
