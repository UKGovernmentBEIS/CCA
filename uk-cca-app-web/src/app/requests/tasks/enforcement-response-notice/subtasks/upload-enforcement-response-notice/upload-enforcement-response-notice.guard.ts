import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot, Router } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus } from '@requests/common';

import { enforcementResponseNoticeQuery } from '../../enforcement-response-notice.selectors';
import { UPLOAD_ENFORCEMENT_RESPONSE_NOTICE_SUBTASK } from '../../enforcement-response-notice.types';

export const uploadEnforcementResponseNoticeRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);

  const isEditable = store.select(requestTaskQuery.selectIsEditable)();
  if (!isEditable) return createUrlTreeFromSnapshot(route, ['summary']);

  const enforcementResponseNotice = store.select(enforcementResponseNoticeQuery.selectEnforcementResponseNotice)();
  const isPenaltyReissue = store.select(enforcementResponseNoticeQuery.selectIsPenaltyReissue)();
  const sectionsCompleted = store.select(enforcementResponseNoticeQuery.selectSectionsCompleted)() ?? {};
  const sectionStatus = sectionsCompleted[UPLOAD_ENFORCEMENT_RESPONSE_NOTICE_SUBTASK];

  if (!isPenaltyReissue && !enforcementResponseNotice?.type) {
    return createUrlTreeFromSnapshot(route, ['enforcement-type']);
  }
  if (!enforcementResponseNotice?.file) {
    return createUrlTreeFromSnapshot(route, ['upload-notice']);
  }
  if (sectionStatus === TaskItemStatus.IN_PROGRESS) return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  if (sectionStatus === TaskItemStatus.COMPLETED) return createUrlTreeFromSnapshot(route, ['summary']);

  return false;
};

export const enforcementTypeGuard: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const router = inject(Router);

  const isPenaltyReissue = store.select(enforcementResponseNoticeQuery.selectIsPenaltyReissue)();
  if (isPenaltyReissue) {
    const taskId = store.select(requestTaskQuery.selectRequestTaskId)();
    return router.parseUrl(`/tasks/${taskId}`);
  }
  return true;
};
