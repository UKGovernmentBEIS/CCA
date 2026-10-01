import { Routes } from '@angular/router';

export const WITHDRAW_ADMIN_TERMINATION_NOTIFY_OPERATOR_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        title: 'Select who should receive the admin termination withdrawal notice',
        data: { backlink: '../..', breadcrumb: false },
        loadComponent: () => import('./withdraw-admin-termination-notify-operator.component'),
      },
      {
        path: 'confirmation',
        title: 'Admin termination withdrawal notice sent to operator',
        data: { breadcrumb: false },
        loadComponent: () => import('./confirmation/confirmation.component'),
      },
    ],
  },
];
