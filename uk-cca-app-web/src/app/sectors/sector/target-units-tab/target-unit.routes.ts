import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';

import { ActiveTargetUnitStore } from './active-target-unit.store';
import { CREATE_TARGET_UNIT_ROUTES } from './create-target-unit/create-target-unit.routes';
import { TargetUnitGuard } from './target-unit.guard';
import { EDIT_TARGET_UNIT_ROUTES } from './target-unit/edit-target-unit/edit-target-unit.routes';
import { USERS_AND_CONTACTS_ROUTES } from './target-unit/users-and-contacts-tab/users-and-contacts-tab.routes';
import { WORKFLOW_DETAILS_ROUTES } from './target-unit/workflow-history-tab/workflow-details/workflow-details.routes';

export const ACTIVE_TARGET_UNIT_ROUTES: Routes = [
  {
    path: ':targetUnitId',
    providers: [ActiveTargetUnitStore],
    canActivate: [TargetUnitGuard],
    resolve: { targetUnit: () => inject(ActiveTargetUnitStore).state },
    data: {
      pageTitle: 'Target unit details',
      breadcrumb: {
        resolveText: ({ targetUnit }) => `${targetUnit.targetUnitAccountDetails.businessId}`,
      },
    },
    children: [
      {
        path: '',
        loadComponent: () => import('./target-unit/target-unit.component').then((c) => c.TargetUnitComponent),
      },
      {
        path: 'process-actions',
        title: 'Start a new task',
        data: {
          breadcrumb: {
            resolveText: ({ targetUnit }) => `${targetUnit.targetUnitAccountDetails.name}`,
          },
        },
        canDeactivate: [PendingRequestGuard],
        loadComponent: () => import('./start-new-task/start-new-task.component').then((c) => c.StartNewTaskComponent),
      },
      {
        path: ':unaId/file-download/:fileType/:uuid',
        loadComponent: () => import('@shared/components/file-download/file-download.component'),
      },
      ...EDIT_TARGET_UNIT_ROUTES,
      ...WORKFLOW_DETAILS_ROUTES,
      ...USERS_AND_CONTACTS_ROUTES,
    ],
  },
];

export const TARGET_UNIT_ROUTES: Routes = [...CREATE_TARGET_UNIT_ROUTES, ...ACTIVE_TARGET_UNIT_ROUTES];
