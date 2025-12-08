import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { isFeatureEnabled } from '@shared/config';
import { PendingRequestGuard } from '@shared/guards';

import { ActiveTargetUnitStore } from './active-target-unit.store';
import { CREATE_TARGET_UNIT_ROUTES } from './create-target-unit/create-target-unit.routes';
import { TargetUnitGuard } from './target-unit.guard';
import { BUYOUT_AND_SURPLUS_ROUTES } from './target-unit/buyout-and-surplus-tab/buyout-and-surplus.routes';
import { BuyoutAndSurplusTabStore } from './target-unit/buyout-and-surplus-tab/buyout-and-surplus-tab.store';
import { EDIT_TARGET_UNIT_ROUTES } from './target-unit/edit-target-unit/edit-target-unit.routes';
import { FACILITIES_LIST_ROUTES } from './target-unit/facilities-tab/facilities-list.routes';
import { NOTES_ROUTES } from './target-unit/notes-tab/notes.routes';
import { PatReportStore } from './target-unit/pat-report-store';
import { PerformanceReportStore } from './target-unit/performance-report-store';
import { REPORTS_TAB_ROUTES } from './target-unit/reports-tab/reports-tab.routes';
import { USERS_AND_CONTACTS_ROUTES } from './target-unit/users-and-contacts-tab/users-and-contacts-tab.routes';
import { WORKFLOW_DETAILS_ROUTES } from './target-unit/workflow-history-tab/workflow-details/workflow-details.routes';

export const ACTIVE_TARGET_UNIT_ROUTES: Routes = [
  {
    path: ':targetUnitId',
    providers: [ActiveTargetUnitStore, PerformanceReportStore, PatReportStore, BuyoutAndSurplusTabStore],
    canActivate: [TargetUnitGuard],
    canDeactivate: [
      () => inject(PerformanceReportStore).reset(),
      () => inject(PatReportStore).reset(),
      () => inject(BuyoutAndSurplusTabStore).reset(),
    ],
    resolve: { targetUnit: () => inject(ActiveTargetUnitStore).state },
    data: {
      breadcrumb: ({ targetUnit }) => `${targetUnit.targetUnitAccountDetails.name}`,
      pageTitle: 'Target unit details',
    },
    children: [
      {
        path: '',
        loadComponent: () => import('./target-unit/target-unit.component').then((c) => c.TargetUnitComponent),
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
      {
        path: 'facilities',
        children: FACILITIES_LIST_ROUTES,
      },
      ...EDIT_TARGET_UNIT_ROUTES,
      ...WORKFLOW_DETAILS_ROUTES,
      ...USERS_AND_CONTACTS_ROUTES,
      ...REPORTS_TAB_ROUTES,
      ...BUYOUT_AND_SURPLUS_ROUTES,
      {
        path: 'notes',
        canActivate: [() => !isFeatureEnabled('hideNotes')()],
        children: NOTES_ROUTES,
      },
    ],
  },
];

export const TARGET_UNIT_ROUTES: Routes = [...CREATE_TARGET_UNIT_ROUTES, ...ACTIVE_TARGET_UNIT_ROUTES];
