import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';

export const PRE_AUDIT_REVIEW_COMPLETE_TASK_ROUTES: Routes = [
  {
    path: '',
    canActivate: [() => inject(RequestTaskStore).select(requestTaskQuery.selectIsEditable)()],
    data: { breadcrumb: false, backlink: '../..' },
    loadComponent: () => import('./complete-task.component').then((c) => c.CompleteTaskComponent),
  },
  {
    path: 'confirmation',
    canActivate: [() => inject(RequestTaskStore).select(requestTaskQuery.selectIsEditable)()],
    data: { breadcrumb: false },
    loadComponent: () =>
      import('./confirmation/confirmation.component').then((c) => c.PreAuditReviewConfirmationComponent),
  },
];
