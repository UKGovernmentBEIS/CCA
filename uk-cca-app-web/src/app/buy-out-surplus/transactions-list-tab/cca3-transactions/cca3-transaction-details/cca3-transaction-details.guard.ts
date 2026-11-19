import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanDeactivateFn, RouterStateSnapshot } from '@angular/router';

import { Cca3TransactionDetailsStore } from './cca3-transaction-details.store';

/**
 * The facilities selection persists across the nested screens of the same transaction and is cleared when navigating
 * away from it. The store is provided on the transaction route, which Angular never destroys, so it is reset here.
 */
export const canDeactivateCca3TransactionDetails: CanDeactivateFn<unknown> = (
  _component,
  currentRoute,
  _currentState,
  nextState,
): boolean => {
  if (!isSameTransaction(currentRoute, nextState)) inject(Cca3TransactionDetailsStore).reset();
  return true;
};

function isSameTransaction(currentRoute: ActivatedRouteSnapshot, nextState: RouterStateSnapshot): boolean {
  for (let route = nextState.root; route; route = route.firstChild) {
    if (route.routeConfig === currentRoute.routeConfig) {
      return route.paramMap.get('transactionId') === currentRoute.paramMap.get('transactionId');
    }
  }

  return false;
}
