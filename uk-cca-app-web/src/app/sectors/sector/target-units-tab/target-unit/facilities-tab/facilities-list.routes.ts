import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';
import { resetCurrentFacility, setCurrentFacility } from '@requests/common';
import { WORKFLOW_DETAILS_ROUTES } from '@shared/components';
import { PendingRequestGuard } from '@shared/guards';

import { FacilityAuditStore } from './facility-audit/facility-audit.store';
import { FacilityAvailableReportingPeriodsResolver } from './facility-available-reporting-periods.resolver';
import { FacilityDetailsResolver } from './facility-details.resolver';
import { FacilityTargetPeriodReportStore } from './facility-target-period-report.store';
import { FACILITY_REPORTS_TAB_ROUTES } from './reports-tab/facility-reports-tab.routes';

export const FACILITIES_LIST_ROUTES: Routes = [
  {
    path: ':facilityId',
    providers: [FacilityTargetPeriodReportStore],
    canActivate: [setCurrentFacility],
    canDeactivate: [resetCurrentFacility, () => inject(FacilityTargetPeriodReportStore).reset()],
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
      {
        path: 'workflow-details',
        children: WORKFLOW_DETAILS_ROUTES,
      },
      {
        path: 'reports',
        data: { backlink: '../../', breadcrumb: false },
        children: FACILITY_REPORTS_TAB_ROUTES,
      },
      {
        path: 'process-actions',
        title: 'Start a facility task',
        data: { backlink: '../', breadcrumb: false },
        canDeactivate: [PendingRequestGuard],
        loadComponent: () => import('@shared/components').then((c) => c.StartNewTaskComponent),
      },
      {
        path: 'tp-reporting',
        data: { backlink: '../process-actions', breadcrumb: false },
        resolve: { availablePeriods: FacilityAvailableReportingPeriodsResolver },
        loadComponent: () => import('./tp-reporting/tp-reporting.component').then((c) => c.TpReportingComponent),
      },
    ],
  },
];
