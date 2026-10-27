import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';

export const NON_COMPLIANCE_CONCLUSION_COMPLETE_TASK_ROUTES: Routes = [
  {
    title: 'Complete task',
    path: '',
    loadComponent: () => import('./complete-task.component').then((c) => c.ConclusionCompleteTaskComponent),
    canActivate: [() => inject(RequestTaskStore).select(requestTaskQuery.selectIsEditable)()],
    data: { breadcrumb: false, backlink: '../..' },
  },
  {
    title: 'Conclusion of non-compliance complete',
    path: 'confirmation',
    loadComponent: () => import('./confirmation/confirmation.component').then((c) => c.ConclusionConfirmationComponent),
    canActivate: [() => inject(RequestTaskStore).select(requestTaskQuery.selectIsEditable)()],
    data: { breadcrumb: false, backlink: false },
  },
];
