import { Routes } from '@angular/router';

import { SectorUserAuthorityDetailsDTO } from 'cca-api';

import { CanEditSectorUserGuard } from './can-edit-sector-user.guard';

export const SECTOR_USER_DETAILS_ROUTES: Routes = [
  {
    path: '',
    title: 'Sector user details',
    data: {
      breadcrumb: ({ sectorUserDetails }: { sectorUserDetails: SectorUserAuthorityDetailsDTO }) =>
        `${sectorUserDetails.firstName} ${sectorUserDetails.lastName}`,
    },
    canActivate: [CanEditSectorUserGuard],
    loadComponent: () => import('./sector-user-details.component').then((c) => c.SectorUserDetailsComponent),
  },
  {
    path: 'edit',
    title: 'Change user details',
    canActivate: [CanEditSectorUserGuard],
    data: { backlink: '../', breadcrumb: false },
    loadComponent: () =>
      import('./edit/edit-sector-user-details.component').then((c) => c.EditSectorUserDetailsComponent),
  },
  {
    path: 'delete',
    title: 'Confirm that this sector user will be deleted',
    data: {
      breadcrumb: false,
      backlink: '../../../',
    },
    loadComponent: () => import('./delete/delete-sector-user.component').then((c) => c.DeleteSectorUserComponent),
  },
];
