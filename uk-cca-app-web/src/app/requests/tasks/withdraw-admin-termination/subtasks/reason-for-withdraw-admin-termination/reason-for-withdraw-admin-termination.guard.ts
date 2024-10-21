import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot, UrlTree } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus } from '@requests/common';

import { AdminTerminationWithdrawQuery } from '../../+state/withdraw-admin-termination.selectors';
import {
  REASON_FOR_WITHDRAW_ADMIN_TERMINATION_SUBTASK,
  ReasonForWithdrawAdminTerminationWizardStep,
} from '../../withdraw-admin-termination.types';
import { isWizardCompleted } from './reason-for-withdraw-admin-termination.wizard';

export const CanActivateReasonForWithdrawAdminTerminationDetails: CanActivateFn = (
  route: ActivatedRouteSnapshot,
): boolean | UrlTree => {
  const requestTaskStore = inject(RequestTaskStore);

  const change = route.queryParamMap.get('change') === 'true';

  const withdrawAdminTerminationReasonDetails = requestTaskStore.select(
    AdminTerminationWithdrawQuery.selectWithdrawAdminTerminationReasonDetails,
  )();

  const withdrawAdminTerminationSectionsCompleted = requestTaskStore.select(
    AdminTerminationWithdrawQuery.selectWithdrawAdminTerminationSectionsCompleted,
  )();

  const isEditable = requestTaskStore.select(requestTaskQuery.selectIsEditable)();

  if (!change && !isWizardCompleted(withdrawAdminTerminationReasonDetails)) return true;
  if (change && isWizardCompleted(withdrawAdminTerminationReasonDetails)) return true;

  if (!isEditable)
    return createUrlTreeFromSnapshot(route, ['../', ReasonForWithdrawAdminTerminationWizardStep.SUMMARY]);

  if (!change && isWizardCompleted(withdrawAdminTerminationReasonDetails)) {
    if (
      withdrawAdminTerminationSectionsCompleted[REASON_FOR_WITHDRAW_ADMIN_TERMINATION_SUBTASK] ===
      TaskItemStatus.COMPLETED
    ) {
      return createUrlTreeFromSnapshot(route, ['../', ReasonForWithdrawAdminTerminationWizardStep.SUMMARY]);
    }

    if (
      withdrawAdminTerminationSectionsCompleted[REASON_FOR_WITHDRAW_ADMIN_TERMINATION_SUBTASK] ===
      TaskItemStatus.IN_PROGRESS
    ) {
      return createUrlTreeFromSnapshot(route, ['../', ReasonForWithdrawAdminTerminationWizardStep.CHECK_YOUR_ANSWERS]);
    }
  }

  return true;
};

export const CanActivateReasonForWithdrawAdminTerminationCheckYourAnswers: CanActivateFn = (
  route: ActivatedRouteSnapshot,
): boolean | UrlTree => {
  const requestTaskStore = inject(RequestTaskStore);

  const withdrawAdminTerminationReasonDetails = requestTaskStore.select(
    AdminTerminationWithdrawQuery.selectWithdrawAdminTerminationReasonDetails,
  )();

  const withdrawAdminTerminationSectionsCompleted = requestTaskStore.select(
    AdminTerminationWithdrawQuery.selectWithdrawAdminTerminationSectionsCompleted,
  )();

  const isEditable = requestTaskStore.select(requestTaskQuery.selectIsEditable)();

  if (!isEditable)
    return createUrlTreeFromSnapshot(route, ['../', ReasonForWithdrawAdminTerminationWizardStep.SUMMARY]);

  if (!isWizardCompleted(withdrawAdminTerminationReasonDetails)) {
    return createUrlTreeFromSnapshot(route, ['../', ReasonForWithdrawAdminTerminationWizardStep.REASON_DETAILS]);
  }

  if (
    withdrawAdminTerminationSectionsCompleted[REASON_FOR_WITHDRAW_ADMIN_TERMINATION_SUBTASK] ===
    TaskItemStatus.COMPLETED
  ) {
    return createUrlTreeFromSnapshot(route, ['../', ReasonForWithdrawAdminTerminationWizardStep.SUMMARY]);
  }

  return true;
};

export const CanActivateReasonForWithdrawAdminTerminationSummary: CanActivateFn = (
  route: ActivatedRouteSnapshot,
): boolean | UrlTree => {
  const requestTaskStore = inject(RequestTaskStore);

  const withdrawAdminTerminationReasonDetails = requestTaskStore.select(
    AdminTerminationWithdrawQuery.selectWithdrawAdminTerminationReasonDetails,
  )();

  const withdrawAdminTerminationSectionsCompleted = requestTaskStore.select(
    AdminTerminationWithdrawQuery.selectWithdrawAdminTerminationSectionsCompleted,
  )();

  const isEditable = requestTaskStore.select(requestTaskQuery.selectIsEditable)();

  if (!isEditable) return true;

  if (!isWizardCompleted(withdrawAdminTerminationReasonDetails)) {
    return createUrlTreeFromSnapshot(route, [ReasonForWithdrawAdminTerminationWizardStep.REASON_DETAILS]);
  }

  if (
    withdrawAdminTerminationSectionsCompleted[REASON_FOR_WITHDRAW_ADMIN_TERMINATION_SUBTASK] ===
    TaskItemStatus.IN_PROGRESS
  ) {
    return createUrlTreeFromSnapshot(route, [ReasonForWithdrawAdminTerminationWizardStep.CHECK_YOUR_ANSWERS]);
  }

  return true;
};
