import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';

export const PRE_AUDIT_REVIEW_COMPLETE_TASK_ROUTES: Routes = [
  {
    title: 'Complete task',
    path: '',
    loadComponent: () => import('./complete-task.component').then((c) => c.CompleteTaskComponent),
    canActivate: [() => inject(RequestTaskStore).select(requestTaskQuery.selectIsEditable)()],
    data: { breadcrumb: false, backlink: '../..' },
  },
  {
    title: 'Pre-audit review complete',
    path: 'confirmation',
    loadComponent: () =>
      import('./confirmation/confirmation.component').then((c) => c.PreAuditReviewConfirmationComponent),
    canActivate: [() => inject(RequestTaskStore).select(requestTaskQuery.selectIsEditable)()],
    data: { breadcrumb: false },
  },
];
