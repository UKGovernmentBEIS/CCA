import { inject } from '@angular/core';
import { CanActivateFn, Router, UrlTree } from '@angular/router';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';

import { Cca3TransactionDetailsStore } from '../cca3-transaction-details.store';

export const canActivateCca3MarkAllFacilities: CanActivateFn = (route): boolean | UrlTree => {
  const { transactionId } = inject(Cca3TransactionDetailsStore).state;
  const isRegulator = inject(AuthStore).select(selectUserRoleType)() === 'REGULATOR';
  const routeTransactionId = route.paramMap.get('transactionId');

  if (isRegulator && transactionId === +routeTransactionId) return true;

  return inject(Router).createUrlTree(['/buyout-surplus', 'cca3-transactions', routeTransactionId]);
};
