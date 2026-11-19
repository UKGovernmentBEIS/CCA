import { Routes } from '@angular/router';

export const FINAL_DECISION_NOTIFY_OPERATOR_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        title: 'Notify operator of decision',
        path: '',
        loadComponent: () => import('./final-decision-notify-operator.component'),
        data: { backlink: '../..', breadcrumb: false },
      },
      {
        title: 'Admin termination final decision notice sent to operator',
        path: 'confirmation',
        loadComponent: () => import('./confirmation/confirmation.component'),
        data: { breadcrumb: false },
      },
    ],
  },
];
