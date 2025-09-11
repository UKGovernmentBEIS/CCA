import { ActivatedRouteSnapshot, createUrlTreeFromSnapshot, UrlTree } from '@angular/router';

export function paymentStatusRedirectGuard(route: ActivatedRouteSnapshot): boolean | UrlTree {
  const paymentStatus = route.queryParamMap.get('buyOutSurplusPaymentStatus');
  if (!paymentStatus) return createUrlTreeFromSnapshot(route, [], { buyOutSurplusPaymentStatus: 'AWAITING_PAYMENT' });
  return true;
}
