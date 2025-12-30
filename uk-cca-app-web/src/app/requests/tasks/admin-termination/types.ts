import {
  AdminTerminationSaveRequestTaskActionPayload,
  CcaNotifyOperatorForDecisionRequestTaskActionPayload,
  RequestTaskActionProcessDTO,
} from 'cca-api';

export const REASON_FOR_ADMIN_TERMINATION_SUBTASK = 'adminTerminationReasonDetails';

export type AdminTerminationRequestTaskActionProcessDTO = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: AdminTerminationSaveRequestTaskActionPayload;
};

export type AdminTerminationNotifyDto = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: CcaNotifyOperatorForDecisionRequestTaskActionPayload;
};
