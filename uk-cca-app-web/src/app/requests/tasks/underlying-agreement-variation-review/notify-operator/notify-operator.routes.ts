import { Routes } from '@angular/router';

export const UNDERLYING_AGREEMENT_REVIEW_NOTIFY_OPERATOR_ROUTES: Routes = [
  {
    title: 'Notify operator of decision',
    path: '',
    loadComponent: () =>
      import('./notify-operator-variation.component').then((c) => c.NotifyOperatorVariationComponent),
    data: { backlink: '../..', breadcrumb: false },
  },
  {
    title: 'Variation complete',
    path: 'confirmation',
    loadComponent: () =>
      import('../confirmation/confirmation.component').then((c) => c.NotifyOperatorVariationConfirmationComponent),
    data: { breadcrumb: false },
  },
];
