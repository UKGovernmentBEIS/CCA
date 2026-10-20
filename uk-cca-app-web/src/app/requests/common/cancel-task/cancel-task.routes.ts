import { Routes } from '@angular/router';

export const CANCEL_TASK_ROUTES: Routes = [
  {
    path: '',
    data: { backlink: '../', breadcrumb: false },
    loadComponent: () => import('./cancel-task.component').then((c) => c.CancelTaskComponent),
  },
  {
    path: 'confirmation',
    loadComponent: () => import('./cancel-confirmation.component').then((c) => c.CancelTaskConfirmationComponent),
  },
];
