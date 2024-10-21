import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot, UrlTree } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus } from '@requests/common';

import { AdminTerminationFinalDecisionQuery } from '../../+state/admin-termination-final-decision.selectors';
import {
  ADMIN_TERMINATION_FINAL_DECISION_SUBTASK,
  AdminTerminationFinalDecisionTerminateAgreementWizardStep,
} from '../../admin-termination-final-decision.helper';
import { isWizardCompleted } from './final-decision-reason.wizard';

export const CanActivateFinalDecisionReasonStep: CanActivateFn = (route: ActivatedRouteSnapshot): boolean | UrlTree => {
  const requestTaskStore = inject(RequestTaskStore);

  const change = route.queryParamMap.get('change') === 'true';

  const finalDecisionReasonDetails = requestTaskStore.select(
    AdminTerminationFinalDecisionQuery.selectAdminTerminationFinalDecisionReasonDetails,
  )();

  const finalDecisionSectionsCompleted = requestTaskStore.select(
    AdminTerminationFinalDecisionQuery.selectAdminTerminationFinalDecisionSectionsCompleted,
  )();

  const isEditable = requestTaskStore.select(requestTaskQuery.selectIsEditable)();

  if (!change && !isWizardCompleted(finalDecisionReasonDetails)) return true;
  if (change && isWizardCompleted(finalDecisionReasonDetails)) return true;

  if (!isEditable)
    return createUrlTreeFromSnapshot(route, ['../', AdminTerminationFinalDecisionTerminateAgreementWizardStep.SUMMARY]);

  if (!change && isWizardCompleted(finalDecisionReasonDetails)) {
    if (finalDecisionSectionsCompleted[ADMIN_TERMINATION_FINAL_DECISION_SUBTASK] === TaskItemStatus.COMPLETED)
      return createUrlTreeFromSnapshot(route, [
        '../',
        AdminTerminationFinalDecisionTerminateAgreementWizardStep.SUMMARY,
      ]);

    if (finalDecisionSectionsCompleted[ADMIN_TERMINATION_FINAL_DECISION_SUBTASK] === TaskItemStatus.IN_PROGRESS)
      return createUrlTreeFromSnapshot(route, [
        '../',
        AdminTerminationFinalDecisionTerminateAgreementWizardStep.CHECK_YOUR_ANSWERS,
      ]);
  }

  return true;
};

export const CanActivateFinalDecisionReasonCheckYourAnswers: CanActivateFn = (
  route: ActivatedRouteSnapshot,
): boolean | UrlTree => {
  const requestTaskStore = inject(RequestTaskStore);

  const finalDecisionReasonDetails = requestTaskStore.select(
    AdminTerminationFinalDecisionQuery.selectAdminTerminationFinalDecisionReasonDetails,
  )();

  const finalDecisionSectionsCompleted = requestTaskStore.select(
    AdminTerminationFinalDecisionQuery.selectAdminTerminationFinalDecisionSectionsCompleted,
  )();

  const isEditable = requestTaskStore.select(requestTaskQuery.selectIsEditable)();

  if (!isEditable)
    return createUrlTreeFromSnapshot(route, ['../', AdminTerminationFinalDecisionTerminateAgreementWizardStep.SUMMARY]);

  if (!isWizardCompleted(finalDecisionReasonDetails)) {
    return createUrlTreeFromSnapshot(route, [
      '../',
      AdminTerminationFinalDecisionTerminateAgreementWizardStep.REASON_DETAILS,
    ]);
  }

  if (finalDecisionSectionsCompleted[ADMIN_TERMINATION_FINAL_DECISION_SUBTASK] === TaskItemStatus.COMPLETED) {
    return createUrlTreeFromSnapshot(route, ['../', AdminTerminationFinalDecisionTerminateAgreementWizardStep.SUMMARY]);
  }

  return true;
};

export const CanActivateFinalDecisionReasonSummary: CanActivateFn = (
  route: ActivatedRouteSnapshot,
): boolean | UrlTree => {
  const requestTaskStore = inject(RequestTaskStore);

  const finalDecisionReasonDetails = requestTaskStore.select(
    AdminTerminationFinalDecisionQuery.selectAdminTerminationFinalDecisionReasonDetails,
  )();

  const finalDecisionSectionsCompleted = requestTaskStore.select(
    AdminTerminationFinalDecisionQuery.selectAdminTerminationFinalDecisionSectionsCompleted,
  )();

  const isEditable = requestTaskStore.select(requestTaskQuery.selectIsEditable)();

  if (!isEditable) return true;

  if (!isWizardCompleted(finalDecisionReasonDetails)) {
    return createUrlTreeFromSnapshot(route, [AdminTerminationFinalDecisionTerminateAgreementWizardStep.REASON_DETAILS]);
  }

  if (finalDecisionSectionsCompleted[ADMIN_TERMINATION_FINAL_DECISION_SUBTASK] === TaskItemStatus.IN_PROGRESS) {
    return createUrlTreeFromSnapshot(route, [
      AdminTerminationFinalDecisionTerminateAgreementWizardStep.CHECK_YOUR_ANSWERS,
    ]);
  }

  return true;
};
