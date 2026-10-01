import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { CanAddSectorUserGuard } from '../../can-add-sector-user.guard';
import { ActiveSectorUserStore } from './active-sector-user.store';
import { SectorUserGuard } from './sector-user.guard';
import { SECTOR_USER_DETAILS_ROUTES } from './sector-user-details/sector-user-details.routes';

export const CONTACTS_ROUTES: Routes = [
  {
    path: 'sector-user',
    children: [
      {
        path: 'add',
        title: 'Add new user',
        canActivate: [CanAddSectorUserGuard],
        loadComponent: () =>
          import('./add-sector-user/add-sector-user.component').then((c) => c.AddSectorUserComponent),
      },
      {
        path: 'add-confirmation',
        title: 'Account confirmation email sent',
        loadComponent: () =>
          import('./add-confirmation/confirmation.component').then((c) => c.AddSectorConfirmationComponent),
      },
      {
        path: ':sectorUserId',
        providers: [ActiveSectorUserStore],
        canActivate: [SectorUserGuard],
        resolve: { sectorUserDetails: () => inject(ActiveSectorUserStore).state.details },
        children: SECTOR_USER_DETAILS_ROUTES,
      },
    ],
  },
];
