import { Observable, of } from 'rxjs';

import BigNumber from 'bignumber.js';
import { produce } from 'immer';

import { Facility } from 'cca-api';

import { TaskItemStatus } from '../../../task-item-status';
import { FacilityWizardReviewStep, FacilityWizardStep, UNARequestTaskPayload } from '../../underlying-agreement.types';

export function calculateEnergyConsumedEligible(energyConsumed: number, energyConsumedProvision: number): number {
  const energyConsumedBig = new BigNumber(energyConsumed);
  const energyConsumedProvisionBig = new BigNumber(energyConsumedProvision);

  return energyConsumedBig
    .multipliedBy(energyConsumedProvisionBig)
    .div(100)
    .plus(energyConsumedBig)
    .decimalPlaces(7, BigNumber.ROUND_HALF_UP)
    .toNumber();
}

export function facilityNextStepPath(currentStep: string): Observable<string> {
  switch (currentStep) {
    case FacilityWizardStep.DETAILS:
      return of('../' + FacilityWizardStep.CONTACT_DETAILS);

    case FacilityWizardStep.CONTACT_DETAILS:
      return of('../' + FacilityWizardStep.ELIGIBILITY_DETAILS);

    case FacilityWizardStep.ELIGIBILITY_DETAILS:
      return of('../' + FacilityWizardStep.EXTENT);

    case FacilityWizardStep.EXTENT:
      return of('../' + FacilityWizardStep.APPLY_RULE);

    case FacilityWizardStep.APPLY_RULE:
      return of('../' + FacilityWizardStep.CHECK_YOUR_ANSWERS);

    case FacilityWizardStep.CHECK_YOUR_ANSWERS:
      return of('../' + FacilityWizardStep.SUMMARY);
  }
}

export function facilityReviewNextStepPath(currentStep: string): Observable<string> {
  switch (currentStep) {
    case FacilityWizardReviewStep.DETAILS:
      return of('../' + FacilityWizardReviewStep.CONTACT_DETAILS);

    case FacilityWizardReviewStep.CONTACT_DETAILS:
      return of('../' + FacilityWizardReviewStep.ELIGIBILITY_DETAILS);

    case FacilityWizardReviewStep.ELIGIBILITY_DETAILS:
      return of('../' + FacilityWizardReviewStep.EXTENT);

    case FacilityWizardReviewStep.EXTENT:
      return of('../' + FacilityWizardReviewStep.APPLY_RULE);

    case FacilityWizardReviewStep.APPLY_RULE:
      return of(FacilityWizardReviewStep.DECISION);

    case FacilityWizardReviewStep.DECISION:
      return of('../' + FacilityWizardReviewStep.CHECK_YOUR_ANSWERS);

    case FacilityWizardReviewStep.CHECK_YOUR_ANSWERS:
      return of('../' + FacilityWizardReviewStep.SUMMARY);
  }
}

export function applyFacility(
  currentPayload: UNARequestTaskPayload,
  { facility, attachments }: { facility: Facility; attachments?: { [key: string]: string } },
): Observable<UNARequestTaskPayload> {
  return of(
    produce(currentPayload, (payload) => {
      const facilityId = facility.facilityId;
      const facilityIdx = payload.underlyingAgreement.facilities.findIndex((f) => f.facilityId === facilityId);

      if (facilityIdx < 0) {
        payload.underlyingAgreement.facilities.push(facility);
      } else {
        payload.underlyingAgreement.facilities[facilityIdx] = {
          ...payload.underlyingAgreement.facilities[facilityIdx],
          ...facility,
        };
      }

      if (attachments) {
        payload.underlyingAgreementAttachments = {
          ...payload.underlyingAgreementAttachments,
          ...attachments,
        };
      }

      payload.currentFacilityId = facilityId;

      payload.sectionsCompleted[facilityId] = TaskItemStatus.IN_PROGRESS;
    }),
  );
}
