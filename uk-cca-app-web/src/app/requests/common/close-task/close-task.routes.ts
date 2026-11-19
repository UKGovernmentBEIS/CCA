import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';

export const CLOSE_TASK_ROUTES: Routes = [
  {
    title: 'Close task',
    path: 'close-task',
    loadComponent: () => import('./close-task.component').then((c) => c.CloseTaskComponent),
    canActivate: [() => inject(RequestTaskStore).select(requestTaskQuery.selectIsEditable)()],
    data: { breadcrumb: false, backlink: '../..' },
  },
  {
    title: 'Task closed',
    path: 'close-task-confirmation',
    loadComponent: () => import('./confirmation/confirmation.component').then((c) => c.CloseTaskConfirmationComponent),
    data: { breadcrumb: false, backlink: false },
  },
];
