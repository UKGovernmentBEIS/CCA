import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot, UrlTree } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';

import { TaskItemStatus } from '../../../task-item-status';
import { underlyingAgreementQuery } from '../../+state';
import {
  BASELINE_AND_TARGETS_SUBTASK,
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsStep,
} from '../../underlying-agreement.types';
import { isTargetPeriodWizardCompleted } from './baseline-and-targets-wizard';

export const CanActivateTargetPeriod: CanActivateFn = (route: ActivatedRouteSnapshot): boolean | UrlTree => {
  const baselineTargetPeriod = inject(BASELINE_AND_TARGETS_SUBTASK);
  const isTargetPeriod5 = baselineTargetPeriod === BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS;
  const requestTaskStore = inject(RequestTaskStore);
  const change = route.queryParamMap.get('change') === 'true';
  const baselineExists = requestTaskStore.select(underlyingAgreementQuery.selectTargetPeriodExists)();
  const sectionsCompleted = requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)();
  const isEditable = requestTaskStore.select(requestTaskQuery.selectIsEditable)();

  const targetPeriodCompleted = isTargetPeriod5
    ? sectionsCompleted[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS]
    : sectionsCompleted[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS];

  const targetPeriodDetails = requestTaskStore.select(
    underlyingAgreementQuery.selectTargetPeriodDetails(isTargetPeriod5),
  )();
  if (!change && !isTargetPeriodWizardCompleted(isTargetPeriod5, baselineExists, targetPeriodDetails)) return true;
  if (change && isTargetPeriodWizardCompleted(isTargetPeriod5, baselineExists, targetPeriodDetails)) return true;

  if (!isEditable) return createUrlTreeFromSnapshot(route, ['../', BaseLineAndTargetsStep.SUMMARY]);

  if (!change && isTargetPeriodWizardCompleted(isTargetPeriod5, baselineExists, targetPeriodDetails)) {
    if (targetPeriodCompleted === TaskItemStatus.COMPLETED) {
      return createUrlTreeFromSnapshot(route, ['../', BaseLineAndTargetsStep.SUMMARY]);
    } else if (targetPeriodCompleted === TaskItemStatus.IN_PROGRESS) {
      return createUrlTreeFromSnapshot(route, ['../', BaseLineAndTargetsStep.CHECK_YOUR_ANSWERS]);
    }
  }
};

export const CanActivateTargetPeriodCheckYourAnswers: CanActivateFn = (
  route: ActivatedRouteSnapshot,
): boolean | UrlTree => {
  const baselineTargetPeriod = inject(BASELINE_AND_TARGETS_SUBTASK);
  const isTargetPeriod5 = baselineTargetPeriod === BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS;
  const requestTaskStore = inject(RequestTaskStore);
  const sectionsCompleted = requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)();
  const isEditable = requestTaskStore.select(requestTaskQuery.selectIsEditable)();
  const baselineExists = requestTaskStore.select(underlyingAgreementQuery.selectTargetPeriodExists)();

  const targetPeriodCompleted = isTargetPeriod5
    ? sectionsCompleted[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS]
    : sectionsCompleted[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS];

  const targetPeriodDetails = requestTaskStore.select(
    underlyingAgreementQuery.selectTargetPeriodDetails(isTargetPeriod5),
  )();

  if (!isEditable) return createUrlTreeFromSnapshot(route, ['../', BaseLineAndTargetsStep.SUMMARY]);

  if (!isTargetPeriodWizardCompleted(isTargetPeriod5, baselineExists, targetPeriodDetails)) {
    return isTargetPeriod5
      ? createUrlTreeFromSnapshot(route, ['../', BaseLineAndTargetsStep.BASELINE_EXISTS])
      : createUrlTreeFromSnapshot(route, ['../', BaseLineAndTargetsStep.TARGET_COMPOSITION]);
  }

  if (targetPeriodCompleted === TaskItemStatus.COMPLETED) {
    return createUrlTreeFromSnapshot(route, ['../', BaseLineAndTargetsStep.SUMMARY]);
  }

  return true;
};

export const CanActivateTargetPeriodSummary: CanActivateFn = (route: ActivatedRouteSnapshot): boolean | UrlTree => {
  const requestTaskStore = inject(RequestTaskStore);
  const baselineTargetPeriod = inject(BASELINE_AND_TARGETS_SUBTASK);
  const isTargetPeriod5 = baselineTargetPeriod === BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS;
  const isEditable = requestTaskStore.select(requestTaskQuery.selectIsEditable)();
  const sectionsCompleted = requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)();
  const baselineExists = requestTaskStore.select(underlyingAgreementQuery.selectTargetPeriodExists)();

  const targetPeriodCompleted = isTargetPeriod5
    ? sectionsCompleted[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS]
    : sectionsCompleted[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS];

  const targetPeriodDetails = requestTaskStore.select(
    underlyingAgreementQuery.selectTargetPeriodDetails(isTargetPeriod5),
  )();

  if (!isEditable) return true;

  if (!isTargetPeriodWizardCompleted(isTargetPeriod5, baselineExists, targetPeriodDetails)) {
    return isTargetPeriod5
      ? createUrlTreeFromSnapshot(route, ['../', BaseLineAndTargetsStep.BASELINE_EXISTS])
      : createUrlTreeFromSnapshot(route, ['../', BaseLineAndTargetsStep.TARGET_COMPOSITION]);
  }

  if (targetPeriodCompleted === TaskItemStatus.IN_PROGRESS) {
    return createUrlTreeFromSnapshot(route, ['../', BaseLineAndTargetsStep.CHECK_YOUR_ANSWERS]);
  }

  return true;
};
