import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, createUrlTreeFromSnapshot } from '@angular/router';

import { BuyoutAndSurplusTabStore } from '../buyout-and-surplus-tab.store';

export function surplusHistoryGuard(route: ActivatedRouteSnapshot) {
  const buyoutAndSurplusTabStore = inject(BuyoutAndSurplusTabStore);

  const details = buyoutAndSurplusTabStore.state?.surplusInfo?.surplusDetails;
  if (!details.hasHistory) return createUrlTreeFromSnapshot(route, ['../../../'], {}, 'buyout-surplus');

  return true;
}
