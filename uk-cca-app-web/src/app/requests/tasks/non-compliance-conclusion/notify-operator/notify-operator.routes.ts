import { Routes } from '@angular/router';

export const NON_COMPLIANCE_CONCLUSION_NOTIFY_OPERATOR_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        title: 'Select who should receive the withdrawal notice',
        data: { backlink: '../..', breadcrumb: false },
        loadComponent: () => import('./notify-operator.component').then((c) => c.ConclusionNotifyOperatorComponent),
      },
      {
        path: 'confirmation',
        title: 'Withdrawal sent to operator',
        data: { backlink: false, breadcrumb: false },
        loadComponent: () =>
          import('./confirmation/confirmation.component').then((c) => c.ConclusionNotifyOperatorConfirmationComponent),
      },
    ],
  },
];
