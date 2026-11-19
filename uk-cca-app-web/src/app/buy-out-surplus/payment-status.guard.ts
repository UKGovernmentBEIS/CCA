import { ActivatedRouteSnapshot, createUrlTreeFromSnapshot, UrlTree } from '@angular/router';

export function paymentStatusRedirectGuard(route: ActivatedRouteSnapshot): boolean | UrlTree {
  const paymentStatus = route.queryParamMap.get('paymentStatus');
  if (!paymentStatus)
    return createUrlTreeFromSnapshot(route, [], { paymentStatus: 'AWAITING_PAYMENT' }, route.fragment);
  return true;
}
