import { AuditDetailsCorrectiveActionsSubmitSaveRequestTaskActionPayload, RequestTaskActionProcessDTO } from 'cca-api';

export const AUDIT_DETAILS_SUBTASK = 'auditDetails';
export const CORRECTIVE_ACTIONS_SUBTASK = 'correctiveActions';

export const AUDIT_DETAILS_CORRECTIVE_ACTIONS_UPLOAD_SECTION_ATTACHMENT_TYPE = {
  AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMIT: 'FACILITY_AUDIT_UPLOAD_ATTACHMENT',
};

export type AuditDetailsAndCorreciveActionsRequestTaskActionProcessDTO = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: AuditDetailsCorrectiveActionsSubmitSaveRequestTaskActionPayload;
};

export type AuditDetailsAndCorrectiveActionsSubmitDTO = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: {
    payloadType: 'EMPTY_PAYLOAD';
  };
};
