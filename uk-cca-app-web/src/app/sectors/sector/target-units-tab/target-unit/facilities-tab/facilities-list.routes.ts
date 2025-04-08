import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';
import { resetCurrentFacility, setCurrentFacility } from '@requests/common';

import { FacilityDetailsResolver } from './facility-details.resolver';

export const FACILITIES_LIST_ROUTES: Routes = [
  {
    path: ':facilityId',
    canActivate: [setCurrentFacility],
    canDeactivate: [resetCurrentFacility],
    children: [
      {
        path: 'details',
        title: 'Facility details',
        data: { backlink: false },
        resolve: { facilityDetails: FacilityDetailsResolver },
        loadComponent: () => import('./facility-details/facility-details.component'),
      },
      {
        path: 'edit',
        title: 'Edit facility scheme exit date',
        data: { backlink: '../details', breadcrumb: false },
        resolve: { facilityDetails: FacilityDetailsResolver },
        canActivate: [() => inject(AuthStore).select(selectUserRoleType)() === 'REGULATOR'],
        loadComponent: () => import('./edit-facility-details/edit-facility-details.component'),
      },
    ],
  },
];
