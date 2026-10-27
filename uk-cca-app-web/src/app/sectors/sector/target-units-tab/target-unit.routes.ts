import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { NOTES_ROUTES, WORKFLOW_DETAILS_ROUTES } from '@shared/components';
import { PendingRequestGuard } from '@shared/guards';

import { ActiveTargetUnitStore } from './active-target-unit.store';
import { CREATE_TARGET_UNIT_ROUTES } from './create-target-unit/create-target-unit.routes';
import { TargetUnitGuard } from './target-unit.guard';
import { BUYOUT_AND_SURPLUS_ROUTES } from './target-unit/buyout-and-surplus-tab/buyout-and-surplus.routes';
import { BuyoutAndSurplusTabStore } from './target-unit/buyout-and-surplus-tab/buyout-and-surplus-tab.store';
import { EDIT_TARGET_UNIT_ROUTES } from './target-unit/edit-target-unit/edit-target-unit.routes';
import { FACILITIES_LIST_ROUTES } from './target-unit/facilities-tab/facilities-list.routes';
import { PatReportStore } from './target-unit/pat-report-store';
import { PerformanceReportStore } from './target-unit/performance-report-store';
import { REPORTS_TAB_ROUTES } from './target-unit/reports-tab/reports-tab.routes';
import { USERS_AND_CONTACTS_ROUTES } from './target-unit/users-and-contacts-tab/users-and-contacts-tab.routes';
import { targetUnitBreadcrumb, targetUnitTabBreadcrumb } from './target-unit-breadcrumb';

export const ACTIVE_TARGET_UNIT_ROUTES: Routes = [
  {
    title: 'Target unit details',
    path: ':targetUnitId',
    canActivate: [TargetUnitGuard],
    canDeactivate: [
      () => inject(PerformanceReportStore).reset(),
      () => inject(PatReportStore).reset(),
      () => inject(BuyoutAndSurplusTabStore).reset(),
    ],
    data: { breadcrumb: targetUnitBreadcrumb },
    resolve: { targetUnit: () => inject(ActiveTargetUnitStore).state },
    children: [
      {
        title: 'Target unit details',
        path: '',
        loadComponent: () => import('./target-unit/target-unit.component').then((c) => c.TargetUnitComponent),
      },
      {
        title: 'Start a new task',
        path: 'process-actions',
        loadComponent: () => import('@shared/components').then((c) => c.StartNewTaskComponent),
        canDeactivate: [PendingRequestGuard],
      },
      {
        title: 'Your download has started',
        path: ':unaId/file-download/:fileType/:uuid',
        loadComponent: () => import('@shared/components').then((c) => c.FileDownloadComponent),
      },
      {
        path: 'facilities',
        children: FACILITIES_LIST_ROUTES,
      },
      ...EDIT_TARGET_UNIT_ROUTES,
      ...USERS_AND_CONTACTS_ROUTES,
      ...REPORTS_TAB_ROUTES,
      ...BUYOUT_AND_SURPLUS_ROUTES,
      {
        path: 'workflow-details',
        data: { breadcrumb: targetUnitTabBreadcrumb('workflow-history') },
        children: WORKFLOW_DETAILS_ROUTES,
      },
      ...NOTES_ROUTES,
    ],
    providers: [ActiveTargetUnitStore, PerformanceReportStore, PatReportStore, BuyoutAndSurplusTabStore],
  },
];

export const TARGET_UNIT_ROUTES: Routes = [...CREATE_TARGET_UNIT_ROUTES, ...ACTIVE_TARGET_UNIT_ROUTES];
