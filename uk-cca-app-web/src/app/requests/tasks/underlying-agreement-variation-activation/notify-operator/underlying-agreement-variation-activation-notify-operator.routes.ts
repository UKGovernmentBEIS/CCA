import { Routes } from '@angular/router';

export const UNDERLYING_AGREEMENT_VARIATION_ACTIVATION_NOTIFY_OPERATOR_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        title: 'Select who should be notified of the decision',
        data: { backlink: '../..', breadcrumb: false },
        loadComponent: () => import('./underlying-agreement-variation-activation-notify-operator.component'),
      },
      {
        path: 'confirmation',
        title: 'Underlying agreement variation activated and sent to operator',
        data: { breadcrumb: false },
        loadComponent: () =>
          import('./confirmation/underlying-agreement-variation-activation-notify-operator-confirmation.component'),
      },
    ],
  },
];
