import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { tprFormQuery } from '@requests/common';

import { canSubmit } from './target-period-reporting-form-task-content';

export const tprFormCanSubmitGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const payload = inject(RequestTaskStore).select(tprFormQuery.selectPayload)();
  return canSubmit(payload) || createUrlTreeFromSnapshot(route, ['../..']);
};
