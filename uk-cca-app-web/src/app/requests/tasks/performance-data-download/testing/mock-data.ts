import { RequestTaskState } from '@netz/common/store';

import { RequestTaskItemDTO } from 'cca-api';

import { PerformanceDataDownloadPayload } from '../../../common/performance-data/performance-data.types';

export const performanceDataDLPayload: PerformanceDataDownloadPayload = {
  payloadType: 'PERFORMANCE_DATA_DOWNLOAD_SUBMIT_PAYLOAD',
  sendEmailNotification: true,
  sectorAssociationInfo: {
    id: 2,
    acronym: 'ADS_2',
    name: 'Aerospace_2',
    competentAuthority: 'ENGLAND',
  },
  targetPeriodType: 'TP6',
};

const mockRequestTaskItemDTOPerformanceDataDL: RequestTaskItemDTO = {
  requestTask: {
    id: 281,
    type: 'PERFORMANCE_DATA_DOWNLOAD_SUBMIT',
    payload: performanceDataDLPayload,
    assignable: true,
    assigneeUserId: '088fe8e5-9eb9-49d0-a6d0-d2f78031fe79',
    assigneeFullName: 'sector user',
    startDate: '2024-12-17T19:14:12.16931Z',
  },
  allowedRequestTaskActions: ['PERFORMANCE_DATA_DOWNLOAD_GENERATE', 'PERFORMANCE_DATA_DOWNLOAD_COMPLETE'],
  userAssignCapable: true,
  requestInfo: {
    id: 'ADS_2-TPR-DWN-5',
    type: 'PERFORMANCE_DATA_DOWNLOAD',
    competentAuthority: 'ENGLAND',
  },
};

export const mockRequestTaskStatePerformanceDataDL: RequestTaskState = {
  requestTaskItem: mockRequestTaskItemDTOPerformanceDataDL,
  relatedTasks: [],
  timeline: [],
  taskReassignedTo: 'abc',
  isEditable: true,
};
