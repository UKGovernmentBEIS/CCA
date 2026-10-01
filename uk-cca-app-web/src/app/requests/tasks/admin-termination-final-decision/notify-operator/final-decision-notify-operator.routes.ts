import { Routes } from '@angular/router';

export const FINAL_DECISION_NOTIFY_OPERATOR_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        title: 'Notify operator of decision',
        data: { backlink: '../..', breadcrumb: false },
        loadComponent: () => import('./final-decision-notify-operator.component'),
      },
      {
        path: 'confirmation',
        title: 'Admin termination final decision notice sent to operator',
        data: { breadcrumb: false },
        loadComponent: () => import('./confirmation/confirmation.component'),
      },
    ],
  },
];
