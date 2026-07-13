import { RequestActionState } from '@netz/common/store';

import { PerformanceDataFacilityDataUploadCompletedRequestActionPayload, RequestActionDTO } from 'cca-api';

const mockRequestActionDTO: RequestActionDTO = {
  id: 303,
  type: 'PERFORMANCE_DATA_FACILITY_UPLOAD_COMPLETED',
  payload: {
    payloadType: 'PERFORMANCE_DATA_FACILITY_UPLOAD_COMPLETED_PAYLOAD',
    performanceDataUpload: {
      targetPeriodType: 'TP7',
      reportType: 'FINAL',
      files: ['409e0469-b221-4d19-8035-12f6224c15cb'],
    },
    results: {
      totalFilesUploaded: 1,
      facilitiesSucceeded: 0,
      facilitiesFailed: 1,
      uploadSummaryFile: '9d665a85-46f8-49c7-be18-57570df14559',
      submittedDate: '2026-07-03T12:28:35.663493945Z',
    },
    uploadAttachments: {
      '409e0469-b221-4d19-8035-12f6224c15cb': 'dummy.csv',
      '9d665a85-46f8-49c7-be18-57570df14559': 'Upload_Summary.csv',
    },
  } as PerformanceDataFacilityDataUploadCompletedRequestActionPayload,
  requestId: 'ADS_12-TPR-4',
  requestType: 'PERFORMANCE_DATA_FACILITY_DATA_UPLOAD',
  competentAuthority: 'ENGLAND',
  submitter: 'sec-adm1 user',
  creationDate: '2026-07-03T12:33:24.031667Z',
};

export const mockRequestActionStateTPRCSVUpload: RequestActionState = {
  action: mockRequestActionDTO,
};
