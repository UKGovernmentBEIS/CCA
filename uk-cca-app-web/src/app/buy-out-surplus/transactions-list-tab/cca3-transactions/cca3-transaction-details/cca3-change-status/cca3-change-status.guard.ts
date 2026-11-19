import { inject } from '@angular/core';
import { CanActivateFn, Router, UrlTree } from '@angular/router';

import { Cca3TransactionDetailsStore } from '../cca3-transaction-details.store';

export const canActivateCca3ChangeStatus: CanActivateFn = (route): boolean | UrlTree => {
  const { transactionId, selectedFacilities } = inject(Cca3TransactionDetailsStore).state;
  const routeTransactionId = route.paramMap.get('transactionId');

  // the selection must belong to the transaction being changed, otherwise its facilities would be sent to another one
  if (selectedFacilities.size > 0 && transactionId === +routeTransactionId) return true;

  return inject(Router).createUrlTree(['/buyout-surplus', 'cca3-transactions', routeTransactionId]);
};
