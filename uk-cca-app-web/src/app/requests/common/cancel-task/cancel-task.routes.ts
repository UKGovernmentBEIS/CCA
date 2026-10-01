import { Routes } from '@angular/router';

export const CANCEL_TASK_ROUTES: Routes = [
  {
    path: '',
    title: 'Are you sure you want to cancel this task?',
    data: { backlink: '../', breadcrumb: false },
    loadComponent: () => import('./cancel-task.component').then((c) => c.CancelTaskComponent),
  },
  {
    path: 'confirmation',
    title: 'Task cancelled',
    loadComponent: () => import('./cancel-confirmation.component').then((c) => c.CancelTaskConfirmationComponent),
  },
];
