import { produce } from 'immer';

import { AdminTerminationFinalDecisionRequestTaskPayload } from 'cca-api';

export function initializeAdminTerminationFinalDecisionPayload(
  payload: AdminTerminationFinalDecisionRequestTaskPayload,
): AdminTerminationFinalDecisionRequestTaskPayload {
  return produce(payload, (p) => {
    if (!p.adminTerminationFinalDecisionReasonDetails) {
      p.adminTerminationFinalDecisionReasonDetails = { explanation: '', finalDecisionType: null, relevantFiles: [] };
    }
  });
}
