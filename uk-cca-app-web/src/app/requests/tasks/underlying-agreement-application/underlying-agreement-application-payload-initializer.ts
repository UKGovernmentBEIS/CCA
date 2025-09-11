import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  TaskItemStatus,
  UNAApplicationRequestTaskPayload,
} from '@requests/common';
import { produce } from 'immer';

export function initializeUnderlyingAgreementSubmitPayload(
  currentPayload: UNAApplicationRequestTaskPayload,
): UNAApplicationRequestTaskPayload {
  return produce(currentPayload, (payload) => {
    if (!payload.sectionsCompleted[REVIEW_TARGET_UNIT_DETAILS_SUBTASK]) {
      payload.sectionsCompleted[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] = TaskItemStatus.COMPLETED;
    }

    if (!payload.underlyingAgreement.authorisationAndAdditionalEvidence) {
      payload.underlyingAgreement.authorisationAndAdditionalEvidence = {
        authorisationAttachmentIds: [],
        additionalEvidenceAttachmentIds: [],
      };
    }

    if (!payload.underlyingAgreement.facilities) {
      payload.underlyingAgreement.facilities = [];
    }

    payload.sectionsCompleted = {
      [AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK]: TaskItemStatus.NOT_STARTED,
      ...payload.sectionsCompleted,
    };
  });
}
