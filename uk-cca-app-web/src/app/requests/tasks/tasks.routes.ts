import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { TASK_RELATED_ACTIONS_MAP } from '@netz/common/components';
import { ItemNamePipe, TASK_STATUS_TAG_MAP } from '@netz/common/pipes';
import {
  getRequestTaskPageCanDeactivateGuard,
  getRequestTaskPageDefaultCanActivateGuard,
  REQUEST_TASK_IS_EDITABLE_RESOLVER,
  REQUEST_TASK_PAGE_CONTENT,
} from '@netz/common/request-task';
import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { taskStatusTagMap } from '@requests/common';

import { RequestTaskDTO } from 'cca-api';

import { CANCEL_TASK_ROUTES } from '../common/cancel-task/cancel-task.routes';
import { initializePayloadGuard } from './initialize-payload.guard';
import { createIsEditableResolver, taskRelatedActionsMap } from './tasks.providers';
import { tasksContent } from './tasks-content';

const taskTypeToReturnText = (type: RequestTaskDTO['type']): string => {
  return new ItemNamePipe().transform(type as any) ?? 'Dashboard';
};

export const TASKS_ROUTES: Routes = [
  {
    path: ':taskId',
    resolve: { type: () => inject(RequestTaskStore).state?.requestTaskItem?.requestTask?.type },
    canActivate: [getRequestTaskPageDefaultCanActivateGuard(), initializePayloadGuard],
    canDeactivate: [getRequestTaskPageCanDeactivateGuard()],
    providers: [
      { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      { provide: REQUEST_TASK_IS_EDITABLE_RESOLVER, useFactory: createIsEditableResolver },
      { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: taskTypeToReturnText },
    ],
    children: [
      {
        path: '',
        providers: [
          { provide: REQUEST_TASK_PAGE_CONTENT, useValue: tasksContent },
          { provide: TASK_STATUS_TAG_MAP, useValue: taskStatusTagMap },
          { provide: TASK_RELATED_ACTIONS_MAP, useValue: taskRelatedActionsMap },
        ],
        loadChildren: () => import('@netz/common/request-task').then((r) => r.REQUEST_TASK_ROUTES),
      },
      { path: 'cancel', children: CANCEL_TASK_ROUTES },
      {
        path: 'timeline',
        loadChildren: () => import('../timeline/timeline.routes').then((r) => r.TIMELINE_ROUTES),
      },
      {
        path: 'underlying-agreement-application',
        loadChildren: () =>
          import('./underlying-agreement-application/underlying-agreement-application.routes').then(
            (r) => r.UNDERLYING_AGREEMENT_APPLICATION_ROUTES,
          ),
      },
      {
        path: 'underlying-agreement-review',
        loadChildren: () =>
          import('./underlying-agreement-review/underlying-agreement-review.routes').then(
            (r) => r.UNDERLYING_AGREEMENT_REVIEW_ROUTES,
          ),
      },
      {
        path: 'underlying-agreement-activation',
        loadChildren: () =>
          import('./underlying-agreement-activation/underlying-agreement-activation.routes').then(
            (r) => r.UNDERLYING_AGREEMENT_ACTIVATION_ROUTES,
          ),
      },
      {
        path: 'underlying-agreement-variation-activation',
        loadChildren: () =>
          import('./underlying-agreement-variation-activation/underlying-agreement-variation-activation.routes').then(
            (r) => r.UNDERLYING_AGREEMENT_VARIATION_ACTIVATION_ROUTES,
          ),
      },
      {
        path: 'admin-termination',
        loadChildren: () =>
          import('./admin-termination/admin-termination.routes').then((r) => r.ADMIN_TERMINATION_ROUTES),
      },
      {
        path: 'withdraw-admin-termination',
        loadChildren: () =>
          import('./withdraw-admin-termination/withdraw-admin-termination.routes').then(
            (r) => r.WITHDRAW_ADMIN_TERMINATION_ROUTES,
          ),
      },
      {
        path: 'admin-termination-final-decision',
        loadChildren: () =>
          import('./admin-termination-final-decision/admin-termination-final-decision.routes').then(
            (r) => r.ADMIN_TERMINATION_FINAL_DECISION_ROUTES,
          ),
      },
      {
        path: 'admin-termination-peer-review',
        loadChildren: () =>
          import('./admin-termination-peer-review/admin-termination-peer-review.routes').then(
            (r) => r.ADMIN_TERMINATION_PEER_REVIEW_ROUTES,
          ),
      },
      {
        path: 'admin-termination-await-peer-review',
        loadChildren: () =>
          import('./admin-termination-wait-for-peer-review/admin-termination-wait-for-peer-review.routes').then(
            (r) => r.ADMIN_TERMINATION_WAIT_FOR_PEER_REVIEW_ROUTES,
          ),
      },
      {
        path: 'underlying-agreement-peer-review',
        loadChildren: () =>
          import('./underlying-agreement-peer-review/underlying-agreement-peer-review.routes').then(
            (r) => r.UNDERLYING_AGREEMENT_PEER_REVIEW_ROUTES,
          ),
      },
      {
        path: 'underlying-agreement-await-peer-review',
        loadChildren: () =>
          import('./underlying-agreement-wait-for-peer-review/underlying-agreement-wait-for-peer-review.routes').then(
            (r) => r.UNDERLYING_AGREEMENT_WAIT_FOR_PEER_REVIEW_ROUTES,
          ),
      },
      {
        path: 'underlying-agreement-variation',
        loadChildren: () =>
          import('./underlying-agreement-variation/underlying-agreement-variation.routes').then(
            (r) => r.UNDERLYING_AGREEMENT_VARIATION_ROUTES,
          ),
      },
      {
        path: 'underlying-agreement-variation-regulator-led',
        loadChildren: () =>
          import('./underlying-agreement-variation-regulator-led/underlying-agreement-variation-regulator-led.routes').then(
            (r) => r.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_ROUTES,
          ),
      },
      {
        path: 'underlying-agreement-variation-review',
        loadChildren: () =>
          import('./underlying-agreement-variation-review/underlying-agreement-variation-review.routes').then(
            (r) => r.UNDERLYING_AGREEMENT_VARIATION_REVIEW_ROUTES,
          ),
      },
      {
        path: 'underlying-agreement-variation-peer-review',
        loadChildren: () =>
          import('./underlying-agreement-variation-peer-review/underlying-agreement-variation-peer-review.routes').then(
            (r) => r.UNDERLYING_AGREEMENT_VARIATION_PEER_REVIEW_ROUTES,
          ),
      },
      {
        path: 'underlying-agreement-variation-await-peer-review',
        loadChildren: () =>
          import('./underlying-agreement-variation-wait-for-peer-review/underlying-agreement-variation-wait-for-peer-review.routes').then(
            (r) => r.UNDERLYING_AGREEMENT_VARIATION_WAIT_FOR_PEER_REVIEW_ROUTES,
          ),
      },
      {
        path: 'underlying-agreement-variation-regulator-led-await-peer-review',
        loadChildren: () =>
          import('./underlying-agreement-variation-regulator-led-wait-for-peer-review/underlying-agreement-variation-regulator-led-wait-for-peer-review.routes').then(
            (r) => r.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_WAIT_FOR_PEER_REVIEW_ROUTES,
          ),
      },
      {
        path: 'underlying-agreement-variation-regulator-led-peer-review',
        loadChildren: () =>
          import('./underlying-agreement-variation-regulator-led-peer-review/underlying-agreement-variation-regulator-led-peer-review.routes').then(
            (r) => r.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_PEER_REVIEW_ROUTES,
          ),
      },
      {
        path: 'performance-data-download',
        loadChildren: () =>
          import('./performance-data-download/performance-data-download.routes').then((r) => r.PERFORMANCE_DATA_ROUTES),
      },
      {
        path: 'performance-data-upload',
        loadChildren: () =>
          import('./performance-data-upload/performance-data-upload.routes').then(
            (r) => r.PERFORMANCE_DATA_UPLOAD_ROUTES,
          ),
      },
      {
        path: 'performance-account-template-upload',
        loadChildren: () =>
          import('./performance-account-template-upload/pat-upload.routes').then((r) => r.PAT_UPLOAD_ROUTES),
      },
      {
        path: 'cca3-migration-account-activation',
        loadChildren: () =>
          import('./cca3-migration-account-activation/cca3-migration-account-activation.routes').then(
            (r) => r.CCA3_MIGRATION_ACCOUNT_ACTIVATION_ROUTES,
          ),
      },
      {
        path: 'pre-audit-review',
        loadChildren: () => import('./pre-audit-review/pre-audit-review.routes').then((r) => r.PRE_AUDIT_REVIEW_ROUTES),
      },
      {
        path: 'audit-details-corrective-actions',
        loadChildren: () =>
          import('./audit-details-corrective-actions/audit-details-corrective-actions.routes').then(
            (r) => r.AUDIT_DETAILS_CORRECTIVE_ACTIONS_ROUTES,
          ),
      },
      {
        path: 'track-corrective-actions',
        loadChildren: () =>
          import('./track-corrective-actions/track-corrective-actions.routes').then(
            (r) => r.TRACK_CORRECTIVE_ACTIONS_ROUTES,
          ),
      },
      {
        path: 'non-compliance',
        loadChildren: () =>
          import('./non-compliance-details/non-compliance-details.routes').then((r) => r.NON_COMPLIANCE_DETAILS_ROUTES),
      },
      {
        path: 'file-download/:uuid',
        loadComponent: () => import('@shared/components').then((m) => m.FileDownloadComponent),
      },
      {
        path: 'file-download/:fileType/:uuid',
        loadComponent: () => import('@shared/components').then((m) => m.FileDownloadComponent),
      },
    ],
  },
  {
    path: '',
    pathMatch: 'full',
    redirectTo: '/dashboard',
  },
];
