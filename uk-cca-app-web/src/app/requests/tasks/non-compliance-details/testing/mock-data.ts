import { RequestTaskState } from '@netz/common/store';

import { NonComplianceDetails } from 'cca-api';

export const mockNonComplianceDetails: NonComplianceDetails = {
  nonComplianceType: 'FAILURE_TO_PROVIDE_TPR',
  nonCompliantDate: '2025-01-01T00:00:00.000Z',
  compliantDate: '2025-01-15T00:00:00.000Z',
  comment: 'Operator failed to provide the target period report on time.',
  relevantWorkflows: ['WF-001', 'WF-002'],
  relevantFacilities: [
    { facilityBusinessId: 'FAC-001', isHistorical: false },
    { facilityBusinessId: 'HIST-001', isHistorical: true },
  ],
  isEnforcementResponseNoticeRequired: true,
  explanation: null,
};

const basePayload = {
  payloadType: 'NON_COMPLIANCE_DETAILS_SUBMIT_PAYLOAD',
  nonComplianceDetails: mockNonComplianceDetails,
  allRelevantWorkflows: {
    'WF-001': 'Workflow 1',
    'WF-002': 'Workflow 2',
    'WF-003': 'Workflow 3',
  },
  allRelevantFacilities: {
    'FAC-001': 'Facility 1',
    'FAC-002': 'Facility 2',
  },
  sectionsCompleted: {
    'provide-details': 'COMPLETED',
  },
};

const baseState = {
  isEditable: true,
  requestTaskItem: {
    requestTask: {
      id: 100,
      type: 'NON_COMPLIANCE_DETAILS_SUBMIT',
      payload: basePayload,
      assignable: true,
    },
    allowedRequestTaskActions: ['NON_COMPLIANCE_DETAILS_SAVE_APPLICATION', 'NON_COMPLIANCE_DETAILS_SUBMIT_APPLICATION'],
    requestInfo: {
      id: 'ADS_1-F00001-NON-COMP-1',
      type: 'NON_COMPLIANCE_DETAILS',
      accountId: 1,
      competentAuthority: 'ENGLAND',
      creationDate: '2025-01-01T00:00:00.000Z',
    },
    userAssignCapable: true,
  },
} as unknown as RequestTaskState;

export const mockNonComplianceDetailsState = baseState;

export const mockNonComplianceDetailsNoEnforcementState = {
  ...baseState,
  requestTaskItem: {
    ...baseState.requestTaskItem,
    requestTask: {
      ...baseState.requestTaskItem.requestTask,
      payload: {
        ...basePayload,
        nonComplianceDetails: {
          ...mockNonComplianceDetails,
          isEnforcementResponseNoticeRequired: false,
          explanation: 'Some reason',
        },
      },
    },
  },
} as unknown as RequestTaskState;

export const mockNonComplianceDetailsEmptyState = {
  ...baseState,
  requestTaskItem: {
    ...baseState.requestTaskItem,
    requestTask: {
      ...baseState.requestTaskItem.requestTask,
      payload: {
        ...basePayload,
        nonComplianceDetails: {
          nonComplianceType: 'FAILURE_TO_PROVIDE_TPR',
          isEnforcementResponseNoticeRequired: true,
        },
        sectionsCompleted: {},
      },
    },
  },
} as unknown as RequestTaskState;
