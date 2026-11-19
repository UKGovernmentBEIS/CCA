import { Routes } from '@angular/router';

export const UNDERLYING_AGREEMENT_ACTIVATION_NOTIFY_OPERATOR_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        title: 'Select who should receive the active Underlying Agreement notice',
        path: '',
        loadComponent: () => import('./underlying-agreement-activation-notify-operator.component'),
        data: { backlink: '../..', breadcrumb: false },
      },
      {
        title: 'Underlying agreement activated and sent to operator',
        path: 'confirmation',
        loadComponent: () =>
          import('./confirmation/underlying-agreement-activation-notify-operator-confirmation.component'),
        data: { breadcrumb: false },
      },
    ],
  },
];
