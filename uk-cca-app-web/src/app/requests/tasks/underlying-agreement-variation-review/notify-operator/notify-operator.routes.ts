import { Routes } from '@angular/router';

import { canActivateNotifyOperator } from '@requests/common';

export const UNDERLYING_AGREEMENT_REVIEW_NOTIFY_OPERATOR_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateNotifyOperator],
    data: { backlink: '../..', breadcrumb: false },
    loadComponent: () =>
      import('./notify-operator-variation.component').then((c) => c.NotifyOperatorVariationComponent),
  },
  {
    path: 'confirmation',
    canActivate: [canActivateNotifyOperator],
    data: { breadcrumb: false },
    loadComponent: () =>
      import('../confirmation/confirmation.component').then((c) => c.NotifyOperatorVariationConfirmationComponent),
  },
];
