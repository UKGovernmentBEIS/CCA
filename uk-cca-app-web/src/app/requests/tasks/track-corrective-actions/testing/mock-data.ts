import { RequestTaskState } from '@netz/common/store';

import { AuditTrackCorrectiveActions, AuditTrackCorrectiveActionsRequestTaskPayload } from 'cca-api';

export const mockCorrectiveActions: AuditTrackCorrectiveActions = {
  correctiveActionResponses: {
    '1': {
      title: '1',
      details: 'dsfdsfddsfsd',
      deadline: '2025-01-01',
      isActionCarriedOut: true,
      actionCarriedOutDate: '2025-01-01',
      comments: 'dsoiuhv osihsd vsdvsdvh soi',
      evidenceFiles: ['36001ec3-ab79-496b-aa79-ad0b17310da6'],
    },
    '2': {
      title: '2',
      details: 'sdkjfhdskjfhds',
      deadline: '2025-02-02',
      isActionCarriedOut: false,
      comments: 'as adfsdf dsfdsf',
      evidenceFiles: [],
    },
  },
};

export const mockPayload: AuditTrackCorrectiveActionsRequestTaskPayload = {
  payloadType: 'AUDIT_TRACK_CORRECTIVE_ACTIONS_PAYLOAD',
  sendEmailNotification: true,
  auditTrackCorrectiveActions: mockCorrectiveActions,
  respondedActions: [],
  sectionsCompleted: {
    correctiveAction1: 'COMPLETED',
    correctiveAction2: 'COMPLETED',
  },
  facilityAuditAttachments: {
    '36001ec3-ab79-496b-aa79-ad0b17310da6': 'sample_profile1.png',
  },
};

export const mockTrackCorrectiveActionsState = {
  requestTask: {
    id: 139,
    type: 'AUDIT_TRACK_CORRECTIVE_ACTIONS',
    payload: mockPayload,
    assignable: true,
    assigneeUserId: 'adf25241-0457-404d-8413-2383e0923ee6',
    assigneeFullName: 'Regulator England',
    daysRemaining: -324,
    startDate: '2025-11-18T11:50:05.926896Z',
  },
  allowedRequestTaskActions: [
    'FACILITY_AUDIT_CANCEL_APPLICATION',
    'FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_COMPLETE_APPLICATION',
    'FACILITY_AUDIT_UPLOAD_ATTACHMENT',
    'FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_SAVE_RESPONSE',
    'FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_SUBMIT_RESPONSE',
  ],
  userAssignCapable: true,
  requestInfo: {
    id: 'ADS_4-F00004-AUDT-1',
    type: 'FACILITY_AUDIT',
    resourceType: 'ACCOUNT',
    resources: { ACCOUNT: '17' },
    creationDate: '2025-11-18T11:45:59.57719Z',
  },
} as unknown as RequestTaskState;
