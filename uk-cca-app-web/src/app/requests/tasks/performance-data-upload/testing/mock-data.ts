import { RequestTaskState } from '@netz/common/store';

import { RequestTaskItemDTO } from 'cca-api';

export const performanceDataUploadPayload = {
  payloadType: 'PERFORMANCE_DATA_UPLOAD_SUBMIT_PAYLOAD',
  sendEmailNotification: true,
  sectorAssociationInfo: {
    id: 2,
    acronym: 'ADS_2',
    name: 'Aerospace_2',
    competentAuthority: 'ENGLAND',
  },
  processCompleted: false,
};

export const mockRequestTaskItemDTOPerformanceDataUpload: RequestTaskItemDTO = {
  requestTask: {
    id: 336,
    type: 'PERFORMANCE_DATA_UPLOAD_SUBMIT',
    payload: performanceDataUploadPayload,
    assignable: true,
    assigneeUserId: '088fe8e5-9eb9-49d0-a6d0-d2f78031fe79',
    assigneeFullName: 'sector user',
    startDate: '2025-01-22T17:45:19.657346Z',
  },
  allowedRequestTaskActions: [
    'PERFORMANCE_DATA_UPLOAD_COMPLETE',
    'PERFORMANCE_DATA_UPLOAD_PROCESSING',
    'PERFORMANCE_DATA_UPLOAD_ATTACH_REPORT_PACKAGE',
  ],
  userAssignCapable: true,
  requestInfo: {
    id: 'ADS_2-TPRUL-6',
    type: 'PERFORMANCE_DATA_UPLOAD',
    competentAuthority: 'ENGLAND',
  },
};

export const mockRequestTaskStatePerformanceDataUploadState: RequestTaskState = {
  requestTaskItem: mockRequestTaskItemDTOPerformanceDataUpload,
  relatedTasks: [],
  timeline: [],
  taskReassignedTo: 'abc',
  isEditable: true,
};
