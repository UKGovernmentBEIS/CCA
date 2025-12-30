import { produce } from 'immer';

import {
  AdminTerminationWithdrawRequestTaskPayload,
  AdminTerminationWithdrawSaveRequestTaskActionPayload,
} from 'cca-api';

export function initializeWithdrawAdminTerminationPayload(
  payload: AdminTerminationWithdrawRequestTaskPayload,
): AdminTerminationWithdrawSaveRequestTaskActionPayload {
  return produce(payload, (p) => {
    if (!p.adminTerminationWithdrawReasonDetails)
      p.adminTerminationWithdrawReasonDetails = { explanation: '', relevantFiles: [] };
  });
}
