import { Routes } from '@angular/router';

export const NOTICE_OF_INTENT_NOTIFY_OPERATOR_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        title: 'Select who should receive the notice of intent notification',
        data: { backlink: '../..', breadcrumb: false },
        loadComponent: () => import('./notice-of-intent-notify-operator.component'),
      },
      {
        path: 'confirmation',
        title: 'Notice of intent sent to operator',
        data: { backlink: false, breadcrumb: false },
        loadComponent: () => import('./confirmation/confirmation.component'),
      },
    ],
  },
];
