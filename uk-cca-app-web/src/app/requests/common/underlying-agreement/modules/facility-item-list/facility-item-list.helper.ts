import { Observable, of } from 'rxjs';

import { produce } from 'immer';

import { TaskItemStatus } from '../../../task-item-status';
import {
  FacilityItemViewModel,
  ManageFacilitiesWizardStep,
  UNARequestTaskPayload,
  UNAVariationRequestTaskPayload,
} from '../../underlying-agreement.types';

export function manageFacilitiesNextStepPath(currentStep: string): Observable<string> {
  switch (currentStep) {
    case ManageFacilitiesWizardStep.ADD_FACILITY:
    case ManageFacilitiesWizardStep.EDIT_FACILITY:
    case ManageFacilitiesWizardStep.DELETE_FACILITY:
    case ManageFacilitiesWizardStep.EXCLUDE_FACILITY:
    case ManageFacilitiesWizardStep.UNDO_FACILITY:
      return of('../' + ManageFacilitiesWizardStep.SUMMARY);
  }
}

export function applyDeleteFacility(
  currentPayload: UNARequestTaskPayload,
  subtask: string,
  userInput: FacilityItemViewModel,
): Observable<UNARequestTaskPayload> {
  return of(
    produce(currentPayload, (payload) => {
      payload.sectionsCompleted[subtask] = TaskItemStatus.IN_PROGRESS;
      payload.underlyingAgreement.facilities = payload.underlyingAgreement.facilities.filter(
        (f) => f.facilityId !== userInput.facilityId,
      );
      delete payload.sectionsCompleted[userInput.facilityId];
    }),
  );
}

export function applyEditFacility(
  currentPayload: UNARequestTaskPayload,
  subtask: string,
  userInput: FacilityItemViewModel,
): Observable<UNARequestTaskPayload> {
  return of(
    produce(currentPayload, (payload) => {
      payload.sectionsCompleted[subtask] = TaskItemStatus.IN_PROGRESS;
      payload.underlyingAgreement.facilities = payload.underlyingAgreement.facilities.map((f) =>
        f.facilityId === userInput.facilityId
          ? { ...f, facilityDetails: { ...f.facilityDetails, name: userInput.name } }
          : f,
      );
    }),
  );
}

export function applyEditFacilityVariationReviewStatusChanges(
  currentPayload: UNARequestTaskPayload,
  userInput: FacilityItemViewModel,
) {
  return of(
    produce(currentPayload, (payload) => {
      if (userInput.status !== 'NEW') {
        (payload as UNAVariationRequestTaskPayload).reviewSectionsCompleted[userInput.facilityId] =
          TaskItemStatus.UNDECIDED;

        delete (payload as UNAVariationRequestTaskPayload).facilitiesReviewGroupDecisions[userInput.facilityId];
      }
    }),
  );
}

export function applyAddFacility(
  currentPayload: UNARequestTaskPayload,
  subtask: string,
  userInput: FacilityItemViewModel,
): Observable<UNARequestTaskPayload> {
  return of(
    produce(currentPayload, (payload) => {
      payload.sectionsCompleted[subtask] = TaskItemStatus.IN_PROGRESS;
      if (!payload.underlyingAgreement.facilities) payload.underlyingAgreement.facilities = [];
      payload.underlyingAgreement.facilities.push({
        facilityId: userInput.facilityId,
        status: userInput.status,
        // @ts-expect-error in this step of the form we cannot populate the facility appropriately
        // since it uses the Facility interface which we don't have the info to populate accordingly
        facilityDetails: {
          name: userInput.name,
        },
      });
    }),
  );
}

export function applyExcludeFacility(
  currentPayload: UNARequestTaskPayload,
  subtask: string,
  userInput: FacilityItemViewModel,
): Observable<UNARequestTaskPayload> {
  return of(
    produce(currentPayload, (payload) => {
      payload.sectionsCompleted[subtask] = TaskItemStatus.IN_PROGRESS;
      payload.underlyingAgreement.facilities = payload.underlyingAgreement.facilities.map((f) =>
        f.facilityId === userInput.facilityId
          ? {
              ...f,
              status: 'EXCLUDED',
              excludedDate: userInput.excludedDate,
            }
          : f,
      );
    }),
  );
}

export function applyExcludeFacilityVariationReviewStatusChanges(
  currentPayload: UNARequestTaskPayload,
  userInput: FacilityItemViewModel,
) {
  return of(
    produce(currentPayload, (payload) => {
      (payload as UNAVariationRequestTaskPayload).reviewSectionsCompleted[userInput.facilityId] =
        TaskItemStatus.UNDECIDED;

      delete (payload as UNAVariationRequestTaskPayload).facilitiesReviewGroupDecisions[userInput.facilityId];
    }),
  );
}

export function applyUndoFacility(
  currentPayload: UNARequestTaskPayload,
  subtask: string,
  userInput: FacilityItemViewModel,
): Observable<UNARequestTaskPayload> {
  return of(
    produce(currentPayload, (payload) => {
      payload.sectionsCompleted[subtask] = TaskItemStatus.IN_PROGRESS;
      payload.underlyingAgreement.facilities = payload.underlyingAgreement.facilities.map((f) =>
        f.facilityId === userInput.facilityId
          ? {
              ...f,
              status: 'LIVE',
              excludedDate: null,
            }
          : f,
      );
    }),
  );
}
