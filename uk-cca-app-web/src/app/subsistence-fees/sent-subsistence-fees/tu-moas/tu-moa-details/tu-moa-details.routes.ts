import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { canActivateMarkFacilitiesGuard } from './mark-facilities/mark-facilities.guard';
import { ReceivedAmountStore } from './received-amount/received-amount.store';

export const TARGET_UNIT_MOA_DETAILS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./tu-moa-details.component').then((c) => c.TuMoaDetailsComponent),
  },
  {
    path: 'received-amount',
    providers: [ReceivedAmountStore],
    canDeactivate: [
      () => {
        inject(ReceivedAmountStore).reset();
        return true;
      },
    ],
    loadChildren: () => import('./received-amount/received-amount.routes').then((r) => r.RECEIVED_AMOUNT_ROUTES),
  },
  {
    path: 'mark-facilities',
    canActivate: [canActivateMarkFacilitiesGuard],
    loadChildren: () => import('./mark-facilities/mark-facilities.routes').then((r) => r.MARK_FACILITIES_ROUTES),
  },
];
