import { Routes } from '@angular/router';

export const FINAL_DECISION_NOTIFY_OPERATOR_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        data: { backlink: '../..', breadcrumb: false },
        loadComponent: () => import('./final-decision-notify-operator.component'),
      },
      {
        path: 'confirmation',
        data: { breadcrumb: false },
        loadComponent: () => import('./confirmation/confirmation.component'),
      },
    ],
  },
];
