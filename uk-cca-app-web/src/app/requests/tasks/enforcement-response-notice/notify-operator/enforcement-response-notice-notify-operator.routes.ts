import { Routes } from '@angular/router';

export const ENFORCEMENT_RESPONSE_NOTICE_NOTIFY_OPERATOR_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        title: 'Select who should receive the enforcement response notice',
        path: '',
        loadComponent: () => import('./enforcement-response-notice-notify-operator.component'),
        data: { backlink: '../..', breadcrumb: false },
      },
      {
        title: 'Enforcement response notice sent to operator',
        path: 'confirmation',
        loadComponent: () => import('./confirmation/confirmation.component'),
        data: { backlink: false, breadcrumb: false },
      },
    ],
  },
];
