import { Routes } from '@angular/router';

export const UNDERLYING_AGREEMENT_VARIATION_ACTIVATION_NOTIFY_OPERATOR_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        title: 'Select who should be notified of the decision',
        path: '',
        loadComponent: () => import('./underlying-agreement-variation-activation-notify-operator.component'),
        data: { backlink: '../..', breadcrumb: false },
      },
      {
        title: 'Underlying agreement variation activated and sent to operator',
        path: 'confirmation',
        loadComponent: () =>
          import('./confirmation/underlying-agreement-variation-activation-notify-operator-confirmation.component'),
        data: { breadcrumb: false },
      },
    ],
  },
];
