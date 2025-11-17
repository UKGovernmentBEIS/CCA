import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';
import { resetCurrentFacility, setCurrentFacility } from '@requests/common';

import { FacilityAuditStore } from './facility-audit/facility-audit.store';
import { FacilityDetailsResolver } from './facility-details.resolver';

export const FACILITIES_LIST_ROUTES: Routes = [
  {
    path: ':facilityId',
    canActivate: [setCurrentFacility],
    canDeactivate: [resetCurrentFacility],
    data: {
      breadcrumb: ({ targetUnit }) => ({
        text: `${targetUnit.targetUnitAccountDetails.name}`,
        fragment: 'facilities',
      }),
    },
    children: [
      {
        path: '',
        title: 'Facility details',
        data: { backlink: false },
        resolve: { facilityDetails: FacilityDetailsResolver },
        loadComponent: () =>
          import('./facility-details/facility-details.component').then((c) => c.FacilityDetailsComponent),
      },
      {
        path: 'edit',
        title: 'Edit facility scheme exit date',
        data: { backlink: '../', breadcrumb: false },
        resolve: { facilityDetails: FacilityDetailsResolver },
        canActivate: [() => inject(AuthStore).select(selectUserRoleType)() === 'REGULATOR'],
        loadComponent: () => import('./edit-facility-details/edit-facility-details.component'),
      },
      {
        path: ':certificationPeriod/change-certification-status',
        data: { backlink: '../../', breadcrumb: false },
        resolve: { facilityDetails: FacilityDetailsResolver },
        canActivate: [() => inject(AuthStore).select(selectUserRoleType)() === 'REGULATOR'],
        loadComponent: () =>
          import('./change-certification-status/change-certification-status.component').then(
            (c) => c.ChangeCertificationStatusComponent,
          ),
      },
      {
        path: 'audit',
        providers: [FacilityAuditStore],
        loadChildren: () => import('./facility-audit/audit.routes').then((r) => r.FACILITY_AUDIT_ROUTES),
      },
    ],
  },
];
