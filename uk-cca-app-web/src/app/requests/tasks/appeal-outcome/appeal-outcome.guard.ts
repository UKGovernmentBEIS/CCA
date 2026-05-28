import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';

import { appealOutcomeQuery } from './appeal-outcome.selectors';
import { AppealOutcome } from './types';

export const appealOutcomeRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);
  const appealOutcome = store.select(appealOutcomeQuery.selectAppealOutcome)();

  if (isAppealOutcomeCompleted(appealOutcome)) {
    return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  }

  return createUrlTreeFromSnapshot(route, ['provide-details']);
};

export function isAppealOutcomeCompleted(appealOutcome: AppealOutcome): boolean {
  return !!appealOutcome?.tribunalDecision && !!appealOutcome?.appealOutcomeDate;
}
