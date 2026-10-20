import { Routes } from '@angular/router';

export const ENFORCEMENT_RESPONSE_NOTICE_NOTIFY_OPERATOR_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        data: { backlink: '../..', breadcrumb: false },
        loadComponent: () => import('./enforcement-response-notice-notify-operator.component'),
      },
      {
        path: 'confirmation',
        data: { backlink: false, breadcrumb: false },
        loadComponent: () => import('./confirmation/confirmation.component'),
      },
    ],
  },
];
