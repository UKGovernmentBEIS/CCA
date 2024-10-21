import produce from 'immer';

import { WADTRequestTaskPayload } from './withdraw-admin-termination.types';

export function initializeWithdrawAdminTerminationPayload(payload: WADTRequestTaskPayload): WADTRequestTaskPayload {
  return produce(payload, (p) => {
    if (!p.adminTerminationWithdrawReasonDetails)
      p.adminTerminationWithdrawReasonDetails = { explanation: '', relevantFiles: [] };
  });
}
