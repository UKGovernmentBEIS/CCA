import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';

export const NON_COMPLIANCE_COMPLETE_TASK_ROUTES: Routes = [
  {
    path: '',
    title: 'Complete task',
    canActivate: [() => inject(RequestTaskStore).select(requestTaskQuery.selectIsEditable)()],
    data: { breadcrumb: false, backlink: '../..' },
    loadComponent: () => import('./complete-task.component').then((c) => c.NonComplianceCompleteTaskComponent),
  },
  {
    path: 'confirmation',
    title: 'Non-compliance details completed',
    canActivate: [() => inject(RequestTaskStore).select(requestTaskQuery.selectIsEditable)()],
    data: { breadcrumb: false },
    loadComponent: () =>
      import('./confirmation/confirmation.component').then((c) => c.NonComplianceConfirmationComponent),
  },
];
