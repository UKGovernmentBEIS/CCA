import { Observable, of } from 'rxjs';

import produce from 'immer';

import { UnderlyingAgreementVariationDetails } from 'cca-api';

import { TaskItemStatus } from '../../../task-item-status';
import {
  UNAVariationRequestTaskPayload,
  UNAVariationReviewRequestTaskPayload,
  VariationDetailsWizardStep,
} from '../../underlying-agreement.types';

export function variationDetailsNextStepPath(currentStep: string): Observable<string> {
  switch (currentStep) {
    case VariationDetailsWizardStep.DETAILS:
      return of('../' + VariationDetailsWizardStep.CHECK_YOUR_ANSWERS);
  }
}

export const facilityChangesTypes: UnderlyingAgreementVariationDetails['modifications'] = [
  'AMEND_OPERATOR_OR_ORGANISATION_NAME',
  'AMEND_OPERATOR_OR_ORGANISATION_TARGET_UNIT_ADDRESS',
  'AMEND_RESPONSIBLE_PERSON_PERSONAL_DETAILS',
  'AMEND_ONE_OR_MORE_FACILITIES_NAME',
  'ADD_ONE_OR_MORE_FACILITIES_TO_AGREEMENT',
  'REMOVE_ONE_OR_MORE_FACILITIES_FROM_AGREEMENT',
  'AMEND_70_PERCENT_RULE_EVALUATION',
];

export const baselineChangesTypes: UnderlyingAgreementVariationDetails['modifications'] = [
  'STRUCTURAL_CHANGE',
  'REVIEW_OF_70_PERCENT_RULE',
  'ERROR_DISCOVERY',
  'ADDITION_OR_REMOVAL_ONE_OR_MORE_FACILITIES_FROM_AGREEMENT',
  'REPLACING_ESTIMATED_WITH_ACTUAL_VALUES',
  'UNEXPECTED_POWER_SUPPLY_DISRUPTION_DURING_TARGET_PERIOD',
  'THROUGHPUT_DROPPING_MORE_THAN_10_PERCENT_DURING_TARGET_PERIOD',
];

export const baselineChangesTypesOption: { value: string; hint?: string }[] = baselineChangesTypes.map((c) => {
  return [
    'UNEXPECTED_POWER_SUPPLY_DISRUPTION_DURING_TARGET_PERIOD',
    'THROUGHPUT_DROPPING_MORE_THAN_10_PERCENT_DURING_TARGET_PERIOD',
  ].includes(c)
    ? {
        value: c,
        hint: 'You must notify us of this variation by 31 January in the year after the end of the target period',
      }
    : {
        value: c,
      };
});

export const targetCurrencyChangesTypes: UnderlyingAgreementVariationDetails['modifications'] = [
  'CHANGE_BETWEEN_RELATIVE_AND_NOVEM_TARGET_TYPES',
  'CHANGE_THROUGHPUT_UNIT',
];

export const otherChangesTypes: UnderlyingAgreementVariationDetails['modifications'] = ['ANY_CHANGES_NOT_COVERED'];

export function applyVariationDetails(
  currentPayload: UNAVariationRequestTaskPayload | UNAVariationReviewRequestTaskPayload,
  subtask: string,
  userInput: UnderlyingAgreementVariationDetails,
): Observable<UNAVariationRequestTaskPayload | UNAVariationReviewRequestTaskPayload> {
  return of(
    produce(currentPayload, (payload) => {
      payload.underlyingAgreement[subtask] = userInput;
      payload.sectionsCompleted[subtask] = TaskItemStatus.IN_PROGRESS;
    }),
  );
}
