import { Routes } from '@angular/router';

export const CCA3_MIGRATION_ACCOUNT_ACTIVATION_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: 'provide-evidence',
        loadChildren: () =>
          import('./subtasks/provide-evidence/provide-evidence.routes').then((r) => r.PROVIDE_EVIDENCE_ROUTES),
      },
      {
        path: 'notify-operator',
        title: 'Notify operator of decision',
        loadChildren: () =>
          import('./notify-operator/cca3-migration-account-activation-notify-operator.routes').then(
            (r) => r.CCA3_MIGRATION_ACCOUNT_ACTIVATION_NOTIFY_OPERATOR_ROUTES,
          ),
      },
    ],
  },
];
