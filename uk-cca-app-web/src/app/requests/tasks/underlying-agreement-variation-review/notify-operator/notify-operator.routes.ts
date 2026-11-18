import { Routes } from '@angular/router';

export const UNDERLYING_AGREEMENT_REVIEW_NOTIFY_OPERATOR_ROUTES: Routes = [
  {
    path: '',
    data: { backlink: '../..', breadcrumb: false },
    title: 'Notify operator of decision',
    loadComponent: () =>
      import('./notify-operator-variation.component').then((c) => c.NotifyOperatorVariationComponent),
  },
  {
    path: 'confirmation',
    data: { breadcrumb: false },
    title: 'Variation complete',
    loadComponent: () =>
      import('../confirmation/confirmation.component').then((c) => c.NotifyOperatorVariationConfirmationComponent),
  },
];
