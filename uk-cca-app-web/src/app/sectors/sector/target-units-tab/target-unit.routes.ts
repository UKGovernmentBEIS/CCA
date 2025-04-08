import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { PendingRequestGuard } from '@shared/guards';

import { ActiveTargetUnitStore } from './active-target-unit.store';
import { CREATE_TARGET_UNIT_ROUTES } from './create-target-unit/create-target-unit.routes';
import { TargetUnitGuard } from './target-unit.guard';
import { EDIT_TARGET_UNIT_ROUTES } from './target-unit/edit-target-unit/edit-target-unit.routes';
import { FACILITIES_LIST_ROUTES } from './target-unit/facilities-tab/facilities-list.routes';
import { PerformanceReportStore } from './target-unit/performance-report-store';
import { reportSubmittedGuard } from './target-unit/reports-tab/report-submitted/report-submitted.guard';
import { toggleLockGuard } from './target-unit/reports-tab/toggle-lock/toggle-lock.guard';
import { USERS_AND_CONTACTS_ROUTES } from './target-unit/users-and-contacts-tab/users-and-contacts-tab.routes';
import { WORKFLOW_DETAILS_ROUTES } from './target-unit/workflow-history-tab/workflow-details/workflow-details.routes';

export const ACTIVE_TARGET_UNIT_ROUTES: Routes = [
  {
    path: ':targetUnitId',
    providers: [ActiveTargetUnitStore, PerformanceReportStore],
    canActivate: [TargetUnitGuard],
    canDeactivate: [() => inject(PerformanceReportStore).reset()],
    resolve: { targetUnit: () => inject(ActiveTargetUnitStore).state },
    data: {
      pageTitle: 'Target unit details',
      breadcrumb: ({ targetUnit }) => `${targetUnit.targetUnitAccountDetails.name}`,
    },
    children: [
      {
        path: '',
        loadComponent: () => import('./target-unit/target-unit.component').then((c) => c.TargetUnitComponent),
      },
      {
        canActivate: [toggleLockGuard],
        path: ':targetPeriodType/toggle-lock',
        data: { breadcrumb: false, backlink: '../../' },
        loadComponent: () =>
          import('./target-unit/reports-tab/toggle-lock/toggle-lock.component').then((c) => c.ToggleLockComponent),
      },
      {
        canActivate: [reportSubmittedGuard],
        path: ':targetPeriodType/submitted-report',
        loadComponent: () =>
          import('./target-unit/reports-tab/report-submitted/report-submitted.component').then(
            (c) => c.ReportSubmittedComponent,
          ),
      },
      {
        path: ':targetPeriodType/file-download/:uuid',
        loadComponent: () => import('@shared/components').then((c) => c.FileDownloadComponent),
      },
      {
        path: 'process-actions',
        title: 'Start a new task',

        canDeactivate: [PendingRequestGuard],
        loadComponent: () => import('@shared/components').then((c) => c.StartNewTaskComponent),
      },
      {
        path: ':unaId/file-download/:fileType/:uuid',
        loadComponent: () => import('@shared/components').then((m) => m.FileDownloadComponent),
      },
      ...EDIT_TARGET_UNIT_ROUTES,
      ...WORKFLOW_DETAILS_ROUTES,
      ...USERS_AND_CONTACTS_ROUTES,
      ...FACILITIES_LIST_ROUTES,
    ],
  },
];

export const TARGET_UNIT_ROUTES: Routes = [...CREATE_TARGET_UNIT_ROUTES, ...ACTIVE_TARGET_UNIT_ROUTES];
