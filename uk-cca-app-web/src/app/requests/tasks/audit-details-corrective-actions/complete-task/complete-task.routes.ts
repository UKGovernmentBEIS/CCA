import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';

export const AUDIT_DETAILS_CORRECTIVE_ACTIONS_COMPLETE_TASK_ROUTES: Routes = [
  {
    title: 'Complete task',
    path: '',
    loadComponent: () => import('./complete-task.component').then((c) => c.CompleteTaskComponent),
    canActivate: [() => inject(RequestTaskStore).select(requestTaskQuery.selectIsEditable)()],
    data: { breadcrumb: false, backlink: '../..' },
  },
  {
    title: 'Audit details and corrective actions complete',
    path: 'confirmation',
    loadComponent: () =>
      import('./confirmation/confirmation.component').then(
        (c) => c.AuditDetailsAndCorrectiveActionsConfirmationComponent,
      ),
    canActivate: [() => inject(RequestTaskStore).select(requestTaskQuery.selectIsEditable)()],
    data: { breadcrumb: false },
  },
];
