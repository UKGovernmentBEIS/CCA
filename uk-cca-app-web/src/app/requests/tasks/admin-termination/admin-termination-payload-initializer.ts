import produce from 'immer';

import { ADTRequestTaskPayload } from './admin-termination.types';

export function initializeAdminTerminationPayload(payload: ADTRequestTaskPayload): ADTRequestTaskPayload {
  return produce(payload, (p) => {
    if (!p.adminTerminationReasonDetails)
      p.adminTerminationReasonDetails = { explanation: '', reason: null, relevantFiles: [] };
  });
}
