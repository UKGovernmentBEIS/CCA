import { inject } from '@angular/core';
import { CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';

import { TaskItemStatus } from '../../../task-item-status';
import { underlyingAgreementQuery } from '../../+state';
import {
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  ReviewTargetUnitDetailsWizardStep,
} from '../../underlying-agreement.types';

export const canActivateTargetUnitDetails: CanActivateFn = (route) => {
  const change = route.queryParamMap.get('change') === 'true';
  const store = inject(RequestTaskStore);
  const sectionsCompleted = store.select(underlyingAgreementQuery.selectSectionsCompleted)();

  if (!change && sectionsCompleted[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] === TaskItemStatus.IN_PROGRESS) return true;
  if (change && sectionsCompleted[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] === TaskItemStatus.COMPLETED) return true;

  if (sectionsCompleted[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] === TaskItemStatus.COMPLETED) {
    return createUrlTreeFromSnapshot(route, ['../', ReviewTargetUnitDetailsWizardStep.SUMMARY]);
  }

  return true;
};

export const canActivateTargetUnitDetailsCheckYourAnswers: CanActivateFn = (route) => {
  const store = inject(RequestTaskStore);
  const sectionsCompleted = store.select(underlyingAgreementQuery.selectSectionsCompleted)();

  if (sectionsCompleted[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] === TaskItemStatus.COMPLETED) {
    return createUrlTreeFromSnapshot(route, ['../', ReviewTargetUnitDetailsWizardStep.SUMMARY]);
  }
  return true;
};

export const canActivateTargetUnitDetailsSummary: CanActivateFn = (route) => {
  const store = inject(RequestTaskStore);
  const sectionsCompleted = store.select(underlyingAgreementQuery.selectSectionsCompleted)();

  if (sectionsCompleted[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] === TaskItemStatus.IN_PROGRESS) {
    return createUrlTreeFromSnapshot(route, ['../', ReviewTargetUnitDetailsWizardStep.CHECK_YOUR_ANSWERS]);
  }
  return true;
};
