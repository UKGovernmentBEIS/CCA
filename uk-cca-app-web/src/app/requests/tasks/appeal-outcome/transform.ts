import { RequestTaskActionPayload, RequestTaskActionProcessDTO } from 'cca-api';

import { AppealOutcome, AppealOutcomeSaveRequestTaskActionPayload } from './types';

export function createSaveActionDTO(requestTaskId: number, appealOutcome: AppealOutcome): RequestTaskActionProcessDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'NON_COMPLIANCE_APPEAL_OUTCOME_SAVE_APPLICATION',
    requestTaskActionPayload: {
      payloadType: 'NON_COMPLIANCE_APPEAL_OUTCOME_SAVE_PAYLOAD',
      appealOutcome,
    } as AppealOutcomeSaveRequestTaskActionPayload,
  };
}

export function createCompleteActionDTO(requestTaskId: number): RequestTaskActionProcessDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'NON_COMPLIANCE_APPEAL_OUTCOME_COMPLETE_APPLICATION',
    requestTaskActionPayload: {
      payloadType: 'EMPTY_PAYLOAD',
    } as RequestTaskActionPayload,
  };
}
