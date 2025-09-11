import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, createUrlTreeFromSnapshot } from '@angular/router';

import { BuyoutAndSurplusTabStore } from '../buyout-and-surplus-tab.store';

export function editSurplusGuard(route: ActivatedRouteSnapshot) {
  const buyoutAndSurplusTabStore = inject(BuyoutAndSurplusTabStore);

  const targetPeriod = route.paramMap.get('targetPeriodType');
  const list = buyoutAndSurplusTabStore.state?.surplusInfo?.surplusGainedDTOList;
  if (!list || !targetPeriod) return createUrlTreeFromSnapshot(route, ['../../../'], {}, 'buyout-surplus');

  const matchingEntry = list.find((entry) => entry.targetPeriod === targetPeriod);
  if (!matchingEntry) return createUrlTreeFromSnapshot(route, ['../../../', 'buyout-surplus'], null, 'buyout-surplus');
  if (!matchingEntry?.surplusGained)
    return createUrlTreeFromSnapshot(route, ['../../../', 'buyout-surplus'], null, 'buyout-surplus');

  return true;
}
