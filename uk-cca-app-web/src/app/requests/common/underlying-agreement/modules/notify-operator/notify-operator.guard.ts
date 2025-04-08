import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot, UrlTree } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { underlyingAgreementReviewQuery } from '@requests/common';

export const canActivateNotifyOperator: CanActivateFn = (route: ActivatedRouteSnapshot): boolean | UrlTree => {
  const requestTaskStore = inject(RequestTaskStore);
  const determinationSubmitted = requestTaskStore.select(underlyingAgreementReviewQuery.selectDeterminationSubmitted)();
  if (!determinationSubmitted) {
    return createUrlTreeFromSnapshot(route, ['../../']);
  }
  return true;
};
