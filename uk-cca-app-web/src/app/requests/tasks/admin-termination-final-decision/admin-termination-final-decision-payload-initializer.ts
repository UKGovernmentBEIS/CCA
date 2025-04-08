import { produce } from 'immer';

import { ADTFDRequestTaskPayload } from './admin-termination-final-decision.types';

export function initializeAdminTerminationFinalDecisionPayload(
  payload: ADTFDRequestTaskPayload,
): ADTFDRequestTaskPayload {
  return produce(payload, (p) => {
    if (!p.adminTerminationFinalDecisionReasonDetails) {
      p.adminTerminationFinalDecisionReasonDetails = { explanation: '', finalDecisionType: null, relevantFiles: [] };
    }
  });
}
