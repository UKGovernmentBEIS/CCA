import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Routes } from '@angular/router';

import { ItemActionTypePipe, TASK_STATUS_TAG_MAP } from '@netz/common/pipes';
import {
  getRequestActionPageCanActivateGuard,
  getRequestActionPageCanDeactivateGuard,
  REQUEST_ACTION_PAGE_CONTENT,
} from '@netz/common/request-action';
import {
  ITEM_TYPE_TO_RETURN_TEXT_MAPPER,
  requestActionQuery,
  RequestActionStore,
  TYPE_AWARE_STORE,
} from '@netz/common/store';
import { taskStatusTagMap } from '@requests/common';
import { ActionTypeToBreadcrumbPipe } from '@shared/pipes';

import { RequestActionDTO } from 'cca-api';

import { timelineContent } from './timeline-content';

const actionTypeToReturnText = (type: RequestActionDTO['type']): string => {
  return new ItemActionTypePipe().transform(type as any) ?? 'Dashboard';
};

export const TIMELINE_ROUTES: Routes = [
  {
    path: ':actionId',
    data: {
      breadcrumb: ({ requestAction, isTaskTimeline }) =>
        isTaskTimeline ? false : new ActionTypeToBreadcrumbPipe().transform(requestAction),
      backlink: ({ isTaskTimeline }) => (isTaskTimeline ? '../..' : false),
    },
    title: () =>
      new ActionTypeToBreadcrumbPipe().transform(inject(RequestActionStore).select(requestActionQuery.selectAction)()),
    resolve: {
      requestAction: () => inject(RequestActionStore).select(requestActionQuery.selectAction)(),
      isTaskTimeline: (route: ActivatedRouteSnapshot) => !!route.params.taskId,
    },
    providers: [
      { provide: REQUEST_ACTION_PAGE_CONTENT, useValue: timelineContent },
      { provide: TASK_STATUS_TAG_MAP, useValue: taskStatusTagMap },
      { provide: TYPE_AWARE_STORE, useExisting: RequestActionStore },
      { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: actionTypeToReturnText },
    ],
    canActivate: [getRequestActionPageCanActivateGuard()],
    canDeactivate: [getRequestActionPageCanDeactivateGuard()],
    children: [
      {
        path: '',
        loadChildren: () => import('@netz/common/request-action').then((r) => r.ROUTES),
      },
      {
        path: 'underlying-agreement-submitted',
        loadChildren: () =>
          import('./underlying-agreement-submitted/underlying-agreement-submitted.routes').then(
            (r) => r.UNDERLYING_AGREEMENT_SUBMITTED_ROUTES,
          ),
      },
      {
        path: 'underlying-agreement-reviewed',
        loadChildren: () =>
          import('./underlying-agreement-reviewed/underlying-agreement-reviewed.routes').then(
            (r) => r.UNDERLYING_AGREEMENT_REVIEWED_TIMELINE_ROUTES,
          ),
      },
      {
        path: 'underlying-agreement-variation-submitted',
        loadChildren: () =>
          import('./underlying-agreement-variation-submitted/underlying-agreement-variation-submitted.routes').then(
            (r) => r.UNDERLYING_AGREEMENT_VARIATION_SUBMITTED_ROUTES,
          ),
      },
      {
        path: 'underlying-agreement-variation-reviewed',
        loadChildren: () =>
          import('./underlying-agreement-variation-reviewed/underlying-agreement-variation-reviewed.routes').then(
            (r) => r.UNDERLYING_AGREEMENT_VARIATION_REVIEWED_TIMELINE_ROUTES,
          ),
      },
      {
        path: 'cca3-migration',
        loadChildren: () => import('./cca3-migration/cca3-migration.routes').then((r) => r.CCA3_MIGRATION_ROUTES),
      },
      {
        path: 'pre-audit-review',
        loadChildren: () =>
          import('./facility-audit/pre-audit-review-completed/pre-audit-review-completed.routes').then(
            (r) => r.FACILITY_AUDIT_PRE_AUDIT_REVIEW_COMPLETED_ROUTES,
          ),
      },
      {
        path: 'audit-details-corrective-actions',
        loadChildren: () =>
          import(
            './facility-audit/details-corrective-actions-completed/details-corrective-actions-completed.routes'
          ).then((r) => r.FACILITY_AUDIT_DETAILS_CORRECTIVE_ACTIONS_COMPLETED_ROUTES),
      },
      {
        path: 'track-corrective-actions',
        loadChildren: () =>
          import('./facility-audit/track-corrective-actions-completed/track-corrective-actions-completed.routes').then(
            (r) => r.FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_COMPLETED_ROUTES,
          ),
      },
      {
        path: 'file-download/:uuid',
        loadComponent: () => import('@shared/components').then((c) => c.FileDownloadComponent),
      },
      {
        path: 'file-download/:fileType/:uuid',
        loadComponent: () => import('@shared/components').then((c) => c.FileDownloadComponent),
      },
    ],
  },
];
