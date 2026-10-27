import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';

export const NON_COMPLIANCE_COMPLETE_TASK_ROUTES: Routes = [
  {
    title: 'Complete task',
    path: '',
    loadComponent: () => import('./complete-task.component').then((c) => c.NonComplianceCompleteTaskComponent),
    canActivate: [() => inject(RequestTaskStore).select(requestTaskQuery.selectIsEditable)()],
    data: { breadcrumb: false, backlink: '../..' },
  },
  {
    title: 'Non-compliance details completed',
    path: 'confirmation',
    loadComponent: () =>
      import('./confirmation/confirmation.component').then((c) => c.NonComplianceConfirmationComponent),
    canActivate: [() => inject(RequestTaskStore).select(requestTaskQuery.selectIsEditable)()],
    data: { breadcrumb: false },
  },
];
