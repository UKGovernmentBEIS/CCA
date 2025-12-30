import {
  AdminTerminationFinalDecisionSaveRequestTaskActionPayload,
  CcaNotifyOperatorForDecisionRequestTaskActionPayload,
  RequestTaskActionProcessDTO,
} from 'cca-api';

export const ADMIN_TERMINATION_FINAL_DECISION_SUBTASK = 'adminTerminationFinalDecisionReasonDetails';

export type AdminTerminationFinalDecisionRequestTaskActionProcessDTO = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: AdminTerminationFinalDecisionSaveRequestTaskActionPayload;
};

export type AdminTerminationFinalDecisionNotifyDto = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: CcaNotifyOperatorForDecisionRequestTaskActionPayload;
};
