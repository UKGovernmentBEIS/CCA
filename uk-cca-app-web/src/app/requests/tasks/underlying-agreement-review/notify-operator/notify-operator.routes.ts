import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, createUrlTreeFromSnapshot, Routes, UrlTree } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { underlyingAgreementReviewQuery } from '@requests/common';

export const UNDERLYING_AGREEMENT_REVIEW_NOTIFY_OPERATOR_ROUTES: Routes = [
  {
    title: 'Select who should receive the determination notification',
    path: '',
    loadComponent: () => import('./notify-operator.component').then((c) => c.NotifyOperatorComponent),
    canActivate: [canActivateNotifyOperator],
    data: { backlink: '../..', breadcrumb: false },
  },
  {
    title: 'Confirmation',
    path: 'confirmation',
    loadComponent: () =>
      import('../confirmation/confirmation.component').then((c) => c.NotifyOperatorConfirmationComponent),
    canActivate: [canActivateNotifyOperator],
    data: { breadcrumb: false },
  },
];

function canActivateNotifyOperator(route: ActivatedRouteSnapshot): boolean | UrlTree {
  const requestTaskStore = inject(RequestTaskStore);
  const determinationSubmitted = requestTaskStore.select(underlyingAgreementReviewQuery.selectDeterminationSubmitted)();
  if (!determinationSubmitted) return createUrlTreeFromSnapshot(route, ['../../']);
  return true;
}
