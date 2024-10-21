import { Routes } from '@angular/router';

export const WITHDRAW_ADMIN_TERMINATION_NOTIFY_OPERATOR_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        data: { backlink: '../..', breadcrumb: false },
        loadComponent: () => import('./withdraw-admin-termination-notify-operator.component'),
      },
      {
        path: 'confirmation',
        data: { breadcrumb: false },
        loadComponent: () => import('./confirmation/confirmation.component'),
      },
    ],
  },
];
