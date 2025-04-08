import {
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  TaskItemStatus,
  UNAApplicationRequestTaskPayload,
  UNARequestTaskPayload,
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

    initializeTP5Payload(payload);
    initializeTP6Payload(payload);
  });
}

export function initializeTP5Payload(payload: UNARequestTaskPayload) {
  if (!payload.underlyingAgreement.targetPeriod5Details) {
    payload.underlyingAgreement.targetPeriod5Details = {
      exist: null,
      details: null,
    };
  }
}
export function initializeTP6Payload(payload: UNAApplicationRequestTaskPayload) {
  if (!payload.underlyingAgreement.targetPeriod6Details) {
    payload.underlyingAgreement.targetPeriod6Details = {
      targetComposition: null,
      baselineData: null,
      targets: null,
    };
    payload.underlyingAgreement.targetPeriod6Details.targetComposition = {
      calculatorFile: null,
      measurementType: null,
      agreementCompositionType: null,
      isTargetUnitThroughputMeasured: null,
      throughputUnit: null,
      conversionFactor: null,
      conversionEvidences: [],
    };
    payload.underlyingAgreement.targetPeriod6Details.baselineData = {
      isTwelveMonths: null,
      baselineDate: null,
      explanation: null,
      greenfieldEvidences: [],
      energy: null,
      usedReportingMechanism: null,
      throughput: null,
      performance: null,
      energyCarbonFactor: null,
    };

    payload.underlyingAgreement.targetPeriod6Details.targets = {
      improvement: null,
      target: null,
    };
  }
}
