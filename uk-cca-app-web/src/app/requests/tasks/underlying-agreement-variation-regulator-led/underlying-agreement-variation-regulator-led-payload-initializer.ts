import {
  nonFacilitySections,
  OPERATOR_ASSENT_DECISION_SUBTASK,
  TaskItemStatus,
  UNAVariationRegulatorLedRequestTaskPayload,
  VARIATION_DETAILS_SUBTASK,
} from '@requests/common';
import { produce } from 'immer';

export function initializeUnderlyingAgreementVariationRegulatorLedSubmitPayload(
  currentPayload: UNAVariationRegulatorLedRequestTaskPayload,
): UNAVariationRegulatorLedRequestTaskPayload {
  return produce(currentPayload, (payload) => {
    [VARIATION_DETAILS_SUBTASK, OPERATOR_ASSENT_DECISION_SUBTASK].forEach((section) => {
      payload.sectionsCompleted[section] = payload.sectionsCompleted[section] ?? TaskItemStatus.NOT_STARTED;
    });

    [...nonFacilitySections].forEach((section) => {
      payload.sectionsCompleted[section] = !payload.sectionsCompleted[section]
        ? TaskItemStatus.UNCHANGED
        : payload.sectionsCompleted[section];
    });

    payload.originalUnderlyingAgreementContainer.underlyingAgreement.facilities.forEach((f) => {
      payload.sectionsCompleted[f.facilityId] = !payload.sectionsCompleted[f.facilityId]
        ? TaskItemStatus.UNCHANGED
        : payload.sectionsCompleted[f.facilityId];
    });
  });
}
