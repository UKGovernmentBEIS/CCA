import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';

export const TRACK_CORRECTIVE_ACTIONS_COMPLETE_TASK_ROUTES: Routes = [
  {
    title: 'Complete task',
    path: '',
    loadComponent: () => import('./complete-task.component').then((c) => c.CompleteTaskComponent),
    canActivate: [() => inject(RequestTaskStore).select(requestTaskQuery.selectIsEditable)()],
    data: { breadcrumb: false, backlink: '../..' },
  },
  {
    title: 'Track corrective actions complete',
    path: 'confirmation',
    loadComponent: () =>
      import('./confirmation/confirmation.component').then((c) => c.TrackCorrectiveActionsConfirmationComponent),
    canActivate: [() => inject(RequestTaskStore).select(requestTaskQuery.selectIsEditable)()],
    data: { breadcrumb: false },
  },
];
