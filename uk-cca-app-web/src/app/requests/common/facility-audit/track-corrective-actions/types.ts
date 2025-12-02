import { AuditTrackCorrectiveActionsSaveRequestTaskActionPayload, RequestTaskActionProcessDTO } from 'cca-api';

export const TRACK_CORRECTIVE_ACTION_SUBTASK = 'correctiveAction';

export const TRACK_CORRECTIVE_ACTIONS_UPLOAD_SECTION_ATTACHMENT_TYPE = 'FACILITY_AUDIT_UPLOAD_ATTACHMENT';

export type TrackCorreciveActionsRequestTaskActionProcessDTO = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: AuditTrackCorrectiveActionsSaveRequestTaskActionPayload;
};

export type AuditDetailsAndCorrectiveActionsSubmitDTO = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: {
    payloadType: 'EMPTY_PAYLOAD';
  };
};
