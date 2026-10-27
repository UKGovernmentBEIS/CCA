import { Routes } from '@angular/router';

export const NOTICE_OF_INTENT_NOTIFY_OPERATOR_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        title: 'Select who should receive the notice of intent notification',
        path: '',
        loadComponent: () => import('./notice-of-intent-notify-operator.component'),
        data: { backlink: '../..', breadcrumb: false },
      },
      {
        title: 'Notice of intent sent to operator',
        path: 'confirmation',
        loadComponent: () => import('./confirmation/confirmation.component'),
        data: { backlink: false, breadcrumb: false },
      },
    ],
  },
];
