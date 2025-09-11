import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot, UrlTree } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { TaskItemStatus } from '@requests/common';

import { AdminTerminationQuery } from '../../+state/admin-termination.selectors';
import {
  REASON_FOR_ADMIN_TERMINATION_SUBTASK,
  ReasonForAdminTerminationWizardStep,
} from '../../admin-termination.types';
import { isWizardCompleted } from './reason-for-admin-termination.wizard';

export const CanActivateReasonForAdminTerminationDetails: CanActivateFn = (
  route: ActivatedRouteSnapshot,
): boolean | UrlTree => {
  const requestTaskStore = inject(RequestTaskStore);

  const change = route.queryParamMap.get('change') === 'true';

  const adminTerminationReasonDetails = requestTaskStore.select(
    AdminTerminationQuery.selectAdminTerminationReasonDetails,
  )();

  const adminTerminationSectionsCompleted = requestTaskStore.select(
    AdminTerminationQuery.selectAdminTerminationSectionsCompleted,
  )();

  const isEditable = requestTaskStore.select(requestTaskQuery.selectIsEditable)();
  if (!isEditable) return createUrlTreeFromSnapshot(route, ['../', 'summary']);

  if (!change && !isWizardCompleted(adminTerminationReasonDetails)) return true;
  if (change && isWizardCompleted(adminTerminationReasonDetails)) return true;

  if (!change && isWizardCompleted(adminTerminationReasonDetails)) {
    if (adminTerminationSectionsCompleted[REASON_FOR_ADMIN_TERMINATION_SUBTASK] === TaskItemStatus.COMPLETED) {
      return createUrlTreeFromSnapshot(route, ['../', 'summary']);
    } else if (adminTerminationSectionsCompleted[REASON_FOR_ADMIN_TERMINATION_SUBTASK] === TaskItemStatus.IN_PROGRESS) {
      return createUrlTreeFromSnapshot(route, ['../', 'check-your-answers']);
    }
  }

  return true;
};

export const CanActivateReasonForAdminTerminationCheckYourAnswers: CanActivateFn = (
  route: ActivatedRouteSnapshot,
): boolean | UrlTree => {
  const requestTaskStore = inject(RequestTaskStore);

  const adminTerminationReasonDetails = requestTaskStore.select(
    AdminTerminationQuery.selectAdminTerminationReasonDetails,
  )();

  const adminTerminationSectionsCompleted = requestTaskStore.select(
    AdminTerminationQuery.selectAdminTerminationSectionsCompleted,
  )();

  const isEditable = requestTaskStore.select(requestTaskQuery.selectIsEditable)();

  if (!isEditable) return createUrlTreeFromSnapshot(route, ['../', 'summary']);

  if (!isWizardCompleted(adminTerminationReasonDetails)) {
    return createUrlTreeFromSnapshot(route, ['../', ReasonForAdminTerminationWizardStep.REASON_DETAILS]);
  }

  if (adminTerminationSectionsCompleted[REASON_FOR_ADMIN_TERMINATION_SUBTASK] === TaskItemStatus.COMPLETED) {
    return createUrlTreeFromSnapshot(route, ['../', 'summary']);
  }

  return true;
};

export const CanActivateReasonForAdminTerminationSummary: CanActivateFn = (
  route: ActivatedRouteSnapshot,
): boolean | UrlTree => {
  const requestTaskStore = inject(RequestTaskStore);

  const adminTerminationReasonDetails = requestTaskStore.select(
    AdminTerminationQuery.selectAdminTerminationReasonDetails,
  )();

  const adminTerminationSectionsCompleted = requestTaskStore.select(
    AdminTerminationQuery.selectAdminTerminationSectionsCompleted,
  )();

  const isEditable = requestTaskStore.select(requestTaskQuery.selectIsEditable)();

  if (!isEditable) return true;

  if (!isWizardCompleted(adminTerminationReasonDetails)) {
    return createUrlTreeFromSnapshot(route, [ReasonForAdminTerminationWizardStep.REASON_DETAILS]);
  }

  if (adminTerminationSectionsCompleted[REASON_FOR_ADMIN_TERMINATION_SUBTASK] === TaskItemStatus.IN_PROGRESS) {
    return createUrlTreeFromSnapshot(route, ['check-your-answers']);
  }

  return true;
};
