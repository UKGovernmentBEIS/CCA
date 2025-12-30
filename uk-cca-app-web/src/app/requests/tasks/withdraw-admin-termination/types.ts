import {
  AdminTerminationWithdrawSaveRequestTaskActionPayload,
  CcaNotifyOperatorForDecisionRequestTaskActionPayload,
  RequestTaskActionProcessDTO,
} from 'cca-api';

export const REASON_FOR_WITHDRAW_ADMIN_TERMINATION_SUBTASK = 'adminTerminationWithdrawReasonDetails';

export type WithdrawAdminTerminationRequestTaskActionProcessDTO = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: AdminTerminationWithdrawSaveRequestTaskActionPayload;
};

export type WithdrawAdminTerminationNotifyDto = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: CcaNotifyOperatorForDecisionRequestTaskActionPayload;
};
