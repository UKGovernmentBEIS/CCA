import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot, UrlTree } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';

import { TaskItemStatus } from '../../../task-item-status';
import { underlyingAgreementQuery } from '../../+state';
import { FacilityWizardStep } from '../../underlying-agreement.types';
import { isFacilityWizardCompleted } from './facility.wizard';

export const CanActivateFacility: CanActivateFn = (route: ActivatedRouteSnapshot): boolean | UrlTree => {
  const requestTaskStore = inject(RequestTaskStore);

  const change = route.queryParamMap.get('change') === 'true';
  const facilityId = route.paramMap.get('facilityId');

  const facilityDetails = requestTaskStore.select(underlyingAgreementQuery.selectFacility(facilityId))();
  const sectionCompleted = requestTaskStore.select(underlyingAgreementQuery.selectStatusForSubtask(facilityId))();
  const isEditable = requestTaskStore.select(requestTaskQuery.selectIsEditable)();

  if (!change && !isFacilityWizardCompleted(facilityDetails)) return true;
  if (change && isFacilityWizardCompleted(facilityDetails)) return true;

  if (!isEditable) return createUrlTreeFromSnapshot(route, ['../', FacilityWizardStep.SUMMARY]);

  if (!change && isFacilityWizardCompleted(facilityDetails)) {
    if (sectionCompleted === TaskItemStatus.COMPLETED) {
      return createUrlTreeFromSnapshot(route, ['../', FacilityWizardStep.SUMMARY]);
    } else if (sectionCompleted === TaskItemStatus.IN_PROGRESS) {
      return createUrlTreeFromSnapshot(route, ['../', FacilityWizardStep.CHECK_YOUR_ANSWERS]);
    }
  }

  return true;
};

export const CanActivateFacilityCheckYourAnswers: CanActivateFn = (
  route: ActivatedRouteSnapshot,
): boolean | UrlTree => {
  const requestTaskStore = inject(RequestTaskStore);

  const facilityId = route.paramMap.get('facilityId');

  const facilityDetails = requestTaskStore.select(underlyingAgreementQuery.selectFacility(facilityId))();
  const sectionCompleted = requestTaskStore.select(underlyingAgreementQuery.selectStatusForSubtask(facilityId))();
  const isEditable = requestTaskStore.select(requestTaskQuery.selectIsEditable)();

  if (!isEditable) return createUrlTreeFromSnapshot(route, ['../', FacilityWizardStep.SUMMARY]);

  if (!isFacilityWizardCompleted(facilityDetails)) {
    return createUrlTreeFromSnapshot(route, ['../', FacilityWizardStep.DETAILS]);
  }

  if (sectionCompleted === TaskItemStatus.COMPLETED) {
    return createUrlTreeFromSnapshot(route, ['../', FacilityWizardStep.SUMMARY]);
  }

  return true;
};

export const CanActivateFacilitySummary: CanActivateFn = (route) => {
  const requestTaskStore = inject(RequestTaskStore);

  const facilityId = route.paramMap.get('facilityId');

  const isEditable = requestTaskStore.select(requestTaskQuery.selectIsEditable)();
  const facilityDetails = requestTaskStore.select(underlyingAgreementQuery.selectFacility(facilityId))();
  const sectionCompleted = requestTaskStore.select(underlyingAgreementQuery.selectStatusForSubtask(facilityId))();

  if (!isEditable) return true;

  if (!isFacilityWizardCompleted(facilityDetails)) {
    return createUrlTreeFromSnapshot(route, ['../', FacilityWizardStep.DETAILS]);
  }

  if (sectionCompleted === TaskItemStatus.IN_PROGRESS) {
    return createUrlTreeFromSnapshot(route, ['../', FacilityWizardStep.CHECK_YOUR_ANSWERS]);
  }

  return true;
};
