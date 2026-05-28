import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus } from '@requests/common';

import { noticeOfIntentQuery } from '../../notice-of-intent.selectors';
import { UPLOAD_NOTICE_OF_INTENT_SUBTASK } from '../../notice-of-intent.types';
import { isWizardCompleted } from './completed';

export const uploadNoticeOfIntentRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);

  if (!store.select(requestTaskQuery.selectIsEditable)()) return createUrlTreeFromSnapshot(route, ['summary']);

  const sectionsCompleted = store.select(noticeOfIntentQuery.selectSectionsCompleted)() ?? {};
  const sectionStatus = sectionsCompleted[UPLOAD_NOTICE_OF_INTENT_SUBTASK];

  if (sectionStatus === TaskItemStatus.COMPLETED) return createUrlTreeFromSnapshot(route, ['summary']);

  const noticeOfIntent = store.select(noticeOfIntentQuery.selectNoticeOfIntent)();
  if (!isWizardCompleted(noticeOfIntent)) return createUrlTreeFromSnapshot(route, ['upload-notice']);

  return createUrlTreeFromSnapshot(route, ['check-your-answers']);
};

export const uploadNoticeOfIntentEditGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);

  if (store.select(requestTaskQuery.selectIsEditable)()) return true;

  return createUrlTreeFromSnapshot(route, ['../summary']);
};
