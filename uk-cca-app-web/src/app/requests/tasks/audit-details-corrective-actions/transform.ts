import { AuditDetailsCorrectiveActionsSubmitRequestTaskPayload } from 'cca-api';

import {
  AuditDetailsAndCorreciveActionsRequestTaskActionProcessDTO,
  AuditDetailsAndCorrectiveActionsSubmitDTO,
} from './types';

export function createRequestTaskActionProcessDTO(
  requestTaskId: number,
  payload: AuditDetailsCorrectiveActionsSubmitRequestTaskPayload,
  sectionsCompleted: Record<string, string>,
): AuditDetailsAndCorreciveActionsRequestTaskActionProcessDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'FACILITY_AUDIT_AUDIT_DETAILS_CORRECTIVE_ACTIONS_SAVE_APPLICATION',
    requestTaskActionPayload: {
      payloadType: 'FACILITY_AUDIT_AUDIT_DETAILS_CORRECTIVE_ACTIONS_SAVE_PAYLOAD',
      auditDetailsAndCorrectiveActions: payload?.auditDetailsAndCorrectiveActions,
      sectionsCompleted,
    },
  };
}

export function createSubmitActionDTO(requestTaskId: number): AuditDetailsAndCorrectiveActionsSubmitDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'FACILITY_AUDIT_AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMIT_APPLICATION',
    requestTaskActionPayload: {
      payloadType: 'EMPTY_PAYLOAD',
    },
  };
}
