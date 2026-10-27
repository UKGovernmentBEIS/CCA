import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';
import { resetCurrentFacility, setCurrentFacility } from '@requests/common';
import { WORKFLOW_DETAILS_ROUTES } from '@shared/components';
import { PendingRequestGuard } from '@shared/guards';

import { FacilityInfoDTO } from 'cca-api';

import { targetUnitTabBreadcrumb } from '../../target-unit-breadcrumb';
import { EditFacilitySurplusResolver } from './edit-surplus/edit-surplus.resolver';
import { FacilityAuditStore } from './facility-audit/facility-audit.store';
import { FacilityAvailableReportingPeriodsResolver } from './facility-available-reporting-periods.resolver';
import { FacilityCertificationResolver, FacilityDetailsResolver } from './facility-details.resolver';
import { FacilityPATReportStore } from './facility-pat-report.store';
import { FacilityTargetPeriodReportStore } from './facility-target-period-report.store';
import { FACILITY_REPORTS_TAB_ROUTES } from './reports-tab/facility-reports-tab.routes';
import { FacilitySurplusHistoryResolver } from './surplus-history/surplus-history.resolver';

export const FACILITIES_LIST_ROUTES: Routes = [
  {
    path: ':facilityId',
    canActivate: [setCurrentFacility],
    canDeactivate: [
      resetCurrentFacility,
      () => inject(FacilityTargetPeriodReportStore).reset(),
      () => inject(FacilityPATReportStore).reset(),
    ],
    data: { breadcrumb: targetUnitTabBreadcrumb('facilities') },
    children: [
      {
        title: 'Facility details',
        path: '',
        loadComponent: () =>
          import('./facility-details/facility-details.component').then((c) => c.FacilityDetailsComponent),
        data: { backlink: false },
        resolve: { facilityDetails: FacilityDetailsResolver },
      },
      {
        title: 'Edit facility scheme exit date',
        path: 'edit',
        loadComponent: () => import('./edit-facility-details/edit-facility-details.component'),
        canActivate: [() => inject(AuthStore).select(selectUserRoleType)() === 'REGULATOR'],
        data: { backlink: { link: '../', fragment: 'details' }, breadcrumb: false },
        resolve: { facilityDetails: FacilityDetailsResolver },
      },
      {
        title: 'Surplus gained change history',
        path: 'surplus-history',
        loadComponent: () => import('./surplus-history/surplus-history.component'),
        data: {
          breadcrumb: ({ facilityDetails }: { facilityDetails: FacilityInfoDTO }) => ({
            text: `${facilityDetails.facilityBusinessId}`,
            from: ':facilityId',
            fragment: 'details',
          }),
        },
        resolve: { facilityDetails: FacilitySurplusHistoryResolver },
      },
      {
        path: 'edit-surplus',
        canActivate: [() => inject(AuthStore).select(selectUserRoleType)() === 'REGULATOR'],
        data: { breadcrumb: false },
        children: [
          {
            title: 'Surplus gained',
            path: '',
            loadComponent: () => import('./edit-surplus/edit-surplus.component'),
            data: { backlink: '../' },
            resolve: { facilityDetails: EditFacilitySurplusResolver },
          },
          {
            title: 'Surplus gained updated',
            path: 'confirmation',
            loadComponent: () => import('./edit-surplus/confirmation/confirmation.component'),
            data: { backlink: '../../' },
            resolve: { facilityDetails: EditFacilitySurplusResolver },
          },
        ],
      },
      {
        title: 'Certification status',
        path: ':certificationPeriod/change-certification-status',
        loadComponent: () =>
          import('./change-certification-status/change-certification-status.component').then(
            (c) => c.ChangeCertificationStatusComponent,
          ),
        canActivate: [() => inject(AuthStore).select(selectUserRoleType)() === 'REGULATOR'],
        data: { backlink: { link: '../../', fragment: 'certification-status' }, breadcrumb: false },
        resolve: { facilityDetails: FacilityDetailsResolver, certificationDetails: FacilityCertificationResolver },
      },
      {
        path: 'audit',
        loadChildren: () => import('./facility-audit/audit.routes').then((r) => r.FACILITY_AUDIT_ROUTES),
        providers: [FacilityAuditStore],
      },
      {
        path: 'workflow-details',
        data: {
          breadcrumb: ({ facilityDetails }: { facilityDetails: FacilityInfoDTO }) => ({
            text: `${facilityDetails.facilityBusinessId}`,
            from: ':facilityId',
            fragment: 'workflow-history',
          }),
        },
        resolve: { facilityDetails: FacilityDetailsResolver },
        children: WORKFLOW_DETAILS_ROUTES,
      },
      {
        path: 'reports',
        children: FACILITY_REPORTS_TAB_ROUTES,
      },
      {
        title: 'Start a facility task',
        path: 'process-actions',
        loadComponent: () => import('@shared/components').then((c) => c.StartNewTaskComponent),
        canDeactivate: [PendingRequestGuard],
        data: { backlink: '../', breadcrumb: false },
      },
      {
        title: 'Select the target period you are reporting for',
        path: 'tp-reporting',
        loadComponent: () => import('./tp-reporting/tp-reporting.component').then((c) => c.TpReportingComponent),
        data: { backlink: '../process-actions', breadcrumb: false },
        resolve: { availablePeriods: FacilityAvailableReportingPeriodsResolver },
      },
    ],
    providers: [FacilityTargetPeriodReportStore, FacilityPATReportStore],
  },
];
