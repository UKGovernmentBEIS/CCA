import { Routes } from '@angular/router';

import { CanEditSectorUserGuard } from './can-edit-sector-user.guard';

export const SECTOR_USER_DETAILS_ROUTES: Routes = [
  {
    path: '',
    data: {
      pageTitle: 'Sector user details',
      breadcrumb: {
        resolveText: ({ sectorUserDetails }) => `${sectorUserDetails.firstName} ${sectorUserDetails.lastName}`,
      },
    },
    canActivate: [CanEditSectorUserGuard],
    loadComponent: () => import('./sector-user-details.component').then((c) => c.SectorUserDetailsComponent),
  },
  {
    path: 'edit',
    canActivate: [CanEditSectorUserGuard],
    data: { backlink: '../', breadcrumb: false },
    loadComponent: () =>
      import('./edit/edit-sector-user-details.component').then((c) => c.EditSectorUserDetailsComponent),
  },
  {
    path: 'delete',
    data: {
      pageTitle: 'Confirm that this sector user will be deleted',
      breadcrumb: false,
      backlink: '../../../',
    },
    loadComponent: () => import('./delete/delete-sector-user.component').then((c) => c.DeleteSectorUserComponent),
  },
];
