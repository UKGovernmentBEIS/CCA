import { RequestTaskState } from '@netz/common/store';

import { RequestTaskItemDTO } from 'cca-api';

import { PATUploadPayload } from '../pat.types';

export const patUploadPayload_NOT_STARTED_YET: PATUploadPayload = {
  payloadType: 'PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_SUBMIT_PAYLOAD',
  sendEmailNotification: true,
  targetPeriodType: 'TP6',
  processingStatus: 'NOT_STARTED_YET',
};

export const mockRequestTaskItemDTOPATUpload: RequestTaskItemDTO = {
  requestTask: {
    id: 26,
    type: 'PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_SUBMIT',
    payload: patUploadPayload_NOT_STARTED_YET,
    assignable: true,
    assigneeUserId: '088fe8e5-9eb9-49d0-a6d0-d2f78031fe79',
    assigneeFullName: 'sector user',
    startDate: '2025-03-20T17:14:44.77688Z',
  },
  allowedRequestTaskActions: [
    'PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_PROCESSING',
    'PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_ATTACH_REPORT_PACKAGE',
    'PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_COMPLETE',
  ],
  userAssignCapable: true,
  requestInfo: {
    id: 'ADS_1-PATUL-7',
    type: 'PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD',
    competentAuthority: 'ENGLAND',
  },
};

export const mockRequestTaskPATState: RequestTaskState = {
  requestTaskItem: mockRequestTaskItemDTOPATUpload,
  relatedTasks: [],
  timeline: [],
  taskReassignedTo: 'sector user',
  isEditable: true,
};
