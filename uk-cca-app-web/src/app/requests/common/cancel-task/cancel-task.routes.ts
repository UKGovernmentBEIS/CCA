import { Routes } from '@angular/router';

export const CANCEL_TASK_ROUTES: Routes = [
  {
    title: 'Are you sure you want to cancel this task?',
    path: '',
    loadComponent: () => import('./cancel-task.component').then((c) => c.CancelTaskComponent),
    data: { backlink: '../', breadcrumb: false },
  },
  {
    title: 'Task cancelled',
    path: 'confirmation',
    loadComponent: () => import('./cancel-confirmation.component').then((c) => c.CancelTaskConfirmationComponent),
  },
];
