import { Routes } from '@angular/router';

export const UNDERLYING_AGREEMENT_ACTIVATION_NOTIFY_OPERATOR_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        title: 'Select who should receive the active Underlying Agreement notice',
        data: { backlink: '../..', breadcrumb: false },
        loadComponent: () => import('./underlying-agreement-activation-notify-operator.component'),
      },
      {
        path: 'confirmation',
        title: 'Underlying agreement activated and sent to operator',
        data: { breadcrumb: false },
        loadComponent: () =>
          import('./confirmation/underlying-agreement-activation-notify-operator-confirmation.component'),
      },
    ],
  },
];
