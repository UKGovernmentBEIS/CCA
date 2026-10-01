import { Routes } from '@angular/router';

export const ENFORCEMENT_RESPONSE_NOTICE_NOTIFY_OPERATOR_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        title: 'Select who should receive the enforcement response notice',
        data: { backlink: '../..', breadcrumb: false },
        loadComponent: () => import('./enforcement-response-notice-notify-operator.component'),
      },
      {
        path: 'confirmation',
        title: 'Enforcement response notice sent to operator',
        data: { backlink: false, breadcrumb: false },
        loadComponent: () => import('./confirmation/confirmation.component'),
      },
    ],
  },
];
