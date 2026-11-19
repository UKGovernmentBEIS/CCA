import { Routes } from '@angular/router';

export const CCA3_MIGRATION_ACCOUNT_ACTIVATION_ROUTES: Routes = [
  {
    title: 'Upload target unit assent',
    path: '',
    children: [
      {
        path: 'provide-evidence',
        loadChildren: () =>
          import('./subtasks/provide-evidence/provide-evidence.routes').then((r) => r.PROVIDE_EVIDENCE_ROUTES),
      },
      {
        title: 'Notify operator of decision',
        path: 'notify-operator',
        loadChildren: () =>
          import('./notify-operator/cca3-migration-account-activation-notify-operator.routes').then(
            (r) => r.CCA3_MIGRATION_ACCOUNT_ACTIVATION_NOTIFY_OPERATOR_ROUTES,
          ),
      },
    ],
  },
];
