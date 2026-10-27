import { Routes } from '@angular/router';

export const ADMIN_TERMINATION_NOTIFY_OPERATOR_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        title: 'Select who should receive the termination notification',
        path: '',
        loadComponent: () => import('./admin-termination-notify-operator.component'),
        data: { backlink: '../..', breadcrumb: false },
      },
      {
        title: 'Admin termination notice sent to operator',
        path: 'confirmation',
        loadComponent: () => import('./confirmation/confirmation.component'),
        data: { breadcrumb: false },
      },
    ],
  },
];
