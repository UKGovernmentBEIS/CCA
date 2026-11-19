import { Routes } from '@angular/router';

export const WITHDRAW_ADMIN_TERMINATION_NOTIFY_OPERATOR_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        title: 'Select who should receive the admin termination withdrawal notice',
        path: '',
        loadComponent: () => import('./withdraw-admin-termination-notify-operator.component'),
        data: { backlink: '../..', breadcrumb: false },
      },
      {
        title: 'Admin termination withdrawal notice sent to operator',
        path: 'confirmation',
        loadComponent: () => import('./confirmation/confirmation.component'),
        data: { breadcrumb: false },
      },
    ],
  },
];
