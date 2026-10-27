import { Routes } from '@angular/router';

export const NON_COMPLIANCE_CONCLUSION_NOTIFY_OPERATOR_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        title: 'Select who should receive the withdrawal notice',
        path: '',
        loadComponent: () => import('./notify-operator.component').then((c) => c.ConclusionNotifyOperatorComponent),
        data: { backlink: '../..', breadcrumb: false },
      },
      {
        title: 'Withdrawal sent to operator',
        path: 'confirmation',
        loadComponent: () =>
          import('./confirmation/confirmation.component').then((c) => c.ConclusionNotifyOperatorConfirmationComponent),
        data: { backlink: false, breadcrumb: false },
      },
    ],
  },
];
