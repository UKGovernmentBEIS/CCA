import { Routes } from '@angular/router';

export const UNDERLYING_AGREEMENT_ACTIVATION_NOTIFY_OPERATOR_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        data: { backlink: '../..', breadcrumb: false },
        loadComponent: () => import('./underlying-agreement-activation-notify-operator.component'),
      },
      {
        path: 'confirmation',
        data: { breadcrumb: false },
        loadComponent: () =>
          import('./confirmation/underlying-agreement-activation-notify-operator-confirmation.component'),
      },
    ],
  },
];
