import { Routes } from '@angular/router';

import { SectorUserAuthorityDetailsDTO } from 'cca-api';

import { CanEditSectorUserGuard } from './can-edit-sector-user.guard';

export const SECTOR_USER_DETAILS_ROUTES: Routes = [
  {
    title: 'Sector user details',
    path: '',
    loadComponent: () => import('./sector-user-details.component').then((c) => c.SectorUserDetailsComponent),
    canActivate: [CanEditSectorUserGuard],
    data: {
      breadcrumb: ({ sectorUserDetails }: { sectorUserDetails: SectorUserAuthorityDetailsDTO }) =>
        `${sectorUserDetails.firstName} ${sectorUserDetails.lastName}`,
    },
  },
  {
    title: 'Change user details',
    path: 'edit',
    loadComponent: () =>
      import('./edit/edit-sector-user-details.component').then((c) => c.EditSectorUserDetailsComponent),
    canActivate: [CanEditSectorUserGuard],
    data: { backlink: '../', breadcrumb: false },
  },
  {
    title: 'Confirm that this sector user will be deleted',
    path: 'delete',
    loadComponent: () => import('./delete/delete-sector-user.component').then((c) => c.DeleteSectorUserComponent),
    data: {
      breadcrumb: false,
      backlink: { link: '../../../', fragment: 'contacts' },
    },
  },
];
