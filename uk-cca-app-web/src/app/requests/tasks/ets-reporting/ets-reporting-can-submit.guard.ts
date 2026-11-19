import { inject } from '@angular/core';
import { CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';

import { etsReportingQuery } from './ets-reporting.selectors';
import { canSubmitEtsReporting } from './ets-reporting.validation';

export const etsReportingCanSubmitGuard: CanActivateFn = (route) => {
  const payload = inject(RequestTaskStore).select(etsReportingQuery.selectPayload)();
  return canSubmitEtsReporting(payload) || createUrlTreeFromSnapshot(route, ['../..']);
};

export const etsReportingSummaryRedirectGuard: CanActivateFn = (route, state) => {
  const isEditable = inject(RequestTaskStore).select(requestTaskQuery.selectIsEditable)();
  const path = state.url.split(/[?#]/)[0];
  return isEditable || path.endsWith('/summary') || createUrlTreeFromSnapshot(route, ['summary']);
};
