import { produce } from 'immer';

import { AdminTerminationSubmitRequestTaskPayload } from 'cca-api';

export function initializeAdminTerminationPayload(
  payload: AdminTerminationSubmitRequestTaskPayload,
): AdminTerminationSubmitRequestTaskPayload {
  return produce(payload, (p) => {
    if (!p.adminTerminationReasonDetails)
      p.adminTerminationReasonDetails = { explanation: '', reason: null, relevantFiles: [] };
  });
}
