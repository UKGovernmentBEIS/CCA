import { RequestTaskState } from '@netz/common/store';

import { AuditDetails, AuditDetailsAndCorrectiveActions, CorrectiveAction, CorrectiveActions } from 'cca-api';

export const mockAuditDetails: AuditDetails = {
  auditTechnique: 'DESK_BASED_INTERVIEW',
  auditDate: '2025-01-01',
  comments: 'dslkjfkjdsn',
  finalAuditReportDate: '2025-01-01',
  auditDocuments: ['feecfb95-f4c4-47a1-b1b8-4acc68329eca'],
};

export const mockActions: CorrectiveAction[] = [
  {
    title: '1',
    details: 'ca1',
    deadline: '2025-01-01',
  },
  {
    title: '2',
    details: 'ca2',
    deadline: '2025-02-02',
  },
];

export const mockCorrectiveActions: CorrectiveActions = {
  hasActions: true,
  actions: mockActions,
};

export const mockAuditDetailsAndCorrectiveActions: AuditDetailsAndCorrectiveActions = {
  auditDetails: mockAuditDetails,
  correctiveActions: mockCorrectiveActions,
};

export const mockAuditDetailsAndCorrectiveActionsState = {
  isEditable: true,
  requestTaskItem: {
    requestTask: {
      id: 184,
      type: 'AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMIT',
      payload: {
        payloadType: 'AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMIT_PAYLOAD',
        sendEmailNotification: true,
        auditDetailsAndCorrectiveActions: mockAuditDetailsAndCorrectiveActions,
        sectionsCompleted: {
          auditDetails: 'COMPLETED',
          correctiveActions: 'COMPLETED',
        },
        facilityAuditAttachments: {
          'e3d8cda6-8382-42df-91d5-8fb804e47a11': 'sample_profile1.png',
          'feecfb95-f4c4-47a1-b1b8-4acc68329eca': 'sample_profile1.png',
        },
      },
      assignable: true,
      assigneeUserId: '38ddb238-97e7-4dd1-9799-57d5a43a6ce2',
      assigneeFullName: 'Regulator England',
      startDate: '2025-11-12T12:06:00.058091Z',
    },
    allowedRequestTaskActions: [
      'FACILITY_AUDIT_CANCEL_APPLICATION',
      'FACILITY_AUDIT_UPLOAD_ATTACHMENT',
      'FACILITY_AUDIT_AUDIT_DETAILS_CORRECTIVE_ACTIONS_SAVE_APPLICATION',
      'FACILITY_AUDIT_AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMIT_APPLICATION',
    ],
    userAssignCapable: true,
    requestInfo: {
      id: 'ADS_1-F00001-AUDT-1',
      type: 'FACILITY_AUDIT',
      competentAuthority: 'ENGLAND',
      accountId: 1,
      creationDate: '2025-11-07T09:36:39.851121Z',
    },
  },
} as unknown as RequestTaskState;
