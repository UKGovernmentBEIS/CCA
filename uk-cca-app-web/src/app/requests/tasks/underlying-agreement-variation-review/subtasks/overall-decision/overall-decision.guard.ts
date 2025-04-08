import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';

import { reviewSectionsCompleted } from '../../utils';

export const canActivateVariationOverallDecision: CanActivateFn = (): boolean => {
  const requestTaskStore = inject(RequestTaskStore);
  const payload = requestTaskStore.select(requestTaskQuery.selectRequestTaskPayload)();
  return reviewSectionsCompleted(payload);
};
