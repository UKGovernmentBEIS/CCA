import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot, UrlTree } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  TaskItemStatus,
  underlyingAgreementQuery,
  underlyingAgreementVariationQuery,
  VARIATION_DETAILS_SUBTASK,
  VariationDetailsWizardStep,
} from '@requests/common';

import { isWizardCompleted } from './variation-details.wizard';

export const CanActivateVariationDetails: CanActivateFn = (route: ActivatedRouteSnapshot): boolean | UrlTree => {
  const change = route.queryParamMap.get('change') === 'true';
  const store = inject(RequestTaskStore);
  const isEditable = store.select(requestTaskQuery.selectIsEditable)();
  const sectionsCompleted = store.select(underlyingAgreementQuery.selectSectionsCompleted)();

  const variationDetails = store.select(underlyingAgreementVariationQuery.selectVariationDetails)();

  if (!change && !isWizardCompleted(variationDetails)) return true;
  if (change && isWizardCompleted(variationDetails)) return true;

  if (!isEditable) return createUrlTreeFromSnapshot(route, ['../', VariationDetailsWizardStep.SUMMARY]);

  if (!change && isWizardCompleted(variationDetails)) {
    if (sectionsCompleted[VARIATION_DETAILS_SUBTASK] === TaskItemStatus.COMPLETED) {
      return createUrlTreeFromSnapshot(route, ['../', VariationDetailsWizardStep.SUMMARY]);
    } else if (sectionsCompleted[VARIATION_DETAILS_SUBTASK] === TaskItemStatus.IN_PROGRESS) {
      return createUrlTreeFromSnapshot(route, ['../', VariationDetailsWizardStep.CHECK_YOUR_ANSWERS]);
    }
    return true;
  }

  return true;
};

export const CanActivateVariationDetailsCheckYourAnswers: CanActivateFn = (
  route: ActivatedRouteSnapshot,
): boolean | UrlTree => {
  const store = inject(RequestTaskStore);
  const isEditable = store.select(requestTaskQuery.selectIsEditable)();
  const sectionsCompleted = store.select(underlyingAgreementQuery.selectSectionsCompleted)();

  const variationDetails = store.select(underlyingAgreementVariationQuery.selectVariationDetails)();

  if (!isEditable) return createUrlTreeFromSnapshot(route, ['../', VariationDetailsWizardStep.SUMMARY]);
  if (!isWizardCompleted(variationDetails)) {
    return createUrlTreeFromSnapshot(route, ['../', VariationDetailsWizardStep.DETAILS]);
  }

  if (sectionsCompleted[VARIATION_DETAILS_SUBTASK] === TaskItemStatus.COMPLETED) {
    return createUrlTreeFromSnapshot(route, ['../', VariationDetailsWizardStep.SUMMARY]);
  }
  return true;
};

export const CanActivateVariationDetailsSummary: CanActivateFn = (route: ActivatedRouteSnapshot): boolean | UrlTree => {
  const store = inject(RequestTaskStore);
  const isEditable = store.select(requestTaskQuery.selectIsEditable)();
  const sectionsCompleted = store.select(underlyingAgreementQuery.selectSectionsCompleted)();

  const variationDetails = store.select(underlyingAgreementVariationQuery.selectVariationDetails)();

  if (!isEditable) return true;
  if (!isWizardCompleted(variationDetails)) {
    return createUrlTreeFromSnapshot(route, ['../', VariationDetailsWizardStep.DETAILS]);
  }
  if (sectionsCompleted[VARIATION_DETAILS_SUBTASK] === TaskItemStatus.IN_PROGRESS) {
    return createUrlTreeFromSnapshot(route, ['../', VariationDetailsWizardStep.CHECK_YOUR_ANSWERS]);
  }
  return true;
};
