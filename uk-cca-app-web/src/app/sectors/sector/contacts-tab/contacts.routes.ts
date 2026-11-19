import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { CanAddSectorUserGuard } from '../../can-add-sector-user.guard';
import { sectorTabBreadcrumb } from '../sector-breadcrumb';
import { ActiveSectorUserStore } from './active-sector-user.store';
import { SectorUserGuard } from './sector-user.guard';
import { SECTOR_USER_DETAILS_ROUTES } from './sector-user-details/sector-user-details.routes';

export const CONTACTS_ROUTES: Routes = [
  {
    path: 'sector-user',
    data: { breadcrumb: sectorTabBreadcrumb('contacts') },
    children: [
      {
        title: 'Add new user',
        path: 'add',
        loadComponent: () =>
          import('./add-sector-user/add-sector-user.component').then((c) => c.AddSectorUserComponent),
        canActivate: [CanAddSectorUserGuard],
      },
      {
        title: 'Account confirmation email sent',
        path: 'add-confirmation',
        loadComponent: () =>
          import('./add-confirmation/confirmation.component').then((c) => c.AddSectorConfirmationComponent),
      },
      {
        path: ':sectorUserId',
        canActivate: [SectorUserGuard],
        resolve: { sectorUserDetails: () => inject(ActiveSectorUserStore).state.details },
        children: SECTOR_USER_DETAILS_ROUTES,
        providers: [ActiveSectorUserStore],
      },
    ],
  },
];
