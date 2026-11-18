import { Routes } from '@angular/router';

export const CCA3_MIGRATION_ACCOUNT_ACTIVATION_NOTIFY_OPERATOR_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        data: { backlink: '../..', breadcrumb: false },
        loadComponent: () => import('./cca3-migration-account-activation-notify-operator.component'),
      },
      {
        path: 'confirmation',
        data: { breadcrumb: false },
        loadComponent: () =>
          import('./confirmation/cca3-migration-account-activation-notify-operator-confirmation.component'),
      },
    ],
  },
];
