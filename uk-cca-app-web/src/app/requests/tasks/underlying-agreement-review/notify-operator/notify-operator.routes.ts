import { Routes } from '@angular/router';

export const UNDERLYING_AGREEMENT_REVIEW_NOTIFY_OPERATOR_ROUTES: Routes = [
  {
    path: '',
    data: { backlink: '../..', breadcrumb: false },
    loadComponent: () => import('./notify-operator.component').then((c) => c.NotifyOperatorComponent),
  },
  {
    path: 'confirmation',
    data: { breadcrumb: false },
    loadComponent: () =>
      import('./confirmation/confirmation.component').then((c) => c.NotifyOperatorConfirmationComponent),
  },
];
