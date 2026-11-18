import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';

export const CLOSE_TASK_ROUTES: Routes = [
  {
    path: 'close-task',
    title: 'Close task',
    canActivate: [() => inject(RequestTaskStore).select(requestTaskQuery.selectIsEditable)()],
    data: { breadcrumb: false, backlink: '../..' },
    loadComponent: () => import('./close-task.component').then((c) => c.CloseTaskComponent),
  },
  {
    path: 'close-task-confirmation',
    title: 'Task closed',
    data: { breadcrumb: false, backlink: false },
    loadComponent: () => import('./confirmation/confirmation.component').then((c) => c.CloseTaskConfirmationComponent),
  },
];
