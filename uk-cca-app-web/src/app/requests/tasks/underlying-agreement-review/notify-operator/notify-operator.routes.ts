import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, createUrlTreeFromSnapshot, Routes, UrlTree } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { underlyingAgreementReviewQuery } from '@requests/common';

export const UNDERLYING_AGREEMENT_REVIEW_NOTIFY_OPERATOR_ROUTES: Routes = [
  {
    path: '',
    canActivate: [canActivateNotifyOperator],
    data: { backlink: '../..', breadcrumb: false },
    loadComponent: () => import('./notify-operator.component').then((c) => c.NotifyOperatorComponent),
  },
  {
    path: 'confirmation',
    canActivate: [canActivateNotifyOperator],
    data: { breadcrumb: false },
    loadComponent: () =>
      import('../confirmation/confirmation.component').then((c) => c.NotifyOperatorConfirmationComponent),
  },
];

function canActivateNotifyOperator(route: ActivatedRouteSnapshot): boolean | UrlTree {
  const requestTaskStore = inject(RequestTaskStore);
  const determinationSubmitted = requestTaskStore.select(underlyingAgreementReviewQuery.selectDeterminationSubmitted)();
  if (!determinationSubmitted) return createUrlTreeFromSnapshot(route, ['../../']);
  return true;
}
