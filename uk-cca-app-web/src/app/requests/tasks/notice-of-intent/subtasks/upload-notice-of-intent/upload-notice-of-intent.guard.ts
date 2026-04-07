import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';

import { noticeOfIntentQuery } from '../../notice-of-intent.selectors';
import { isWizardCompleted } from './completed';

export const uploadNoticeOfIntentRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);

  if (!store.select(requestTaskQuery.selectIsEditable)()) {
    return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  }

  const noticeOfIntent = store.select(noticeOfIntentQuery.selectNoticeOfIntent)();
  if (!isWizardCompleted(noticeOfIntent)) {
    return createUrlTreeFromSnapshot(route, ['upload-notice']);
  }

  return createUrlTreeFromSnapshot(route, ['check-your-answers']);
};
