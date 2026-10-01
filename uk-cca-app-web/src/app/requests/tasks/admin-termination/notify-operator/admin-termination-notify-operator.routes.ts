import { Routes } from '@angular/router';

export const ADMIN_TERMINATION_NOTIFY_OPERATOR_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        title: 'Select who should receive the termination notification',
        data: { backlink: '../..', breadcrumb: false },
        loadComponent: () => import('./admin-termination-notify-operator.component'),
      },
      {
        path: 'confirmation',
        title: 'Admin termination notice sent to operator',
        data: { breadcrumb: false },
        loadComponent: () => import('./confirmation/confirmation.component'),
      },
    ],
  },
];
