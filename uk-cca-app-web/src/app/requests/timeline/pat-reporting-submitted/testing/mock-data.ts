import { RequestActionState } from '@netz/common/store';

import { FacilityPerformanceAccountTemplateDataUploadCompletedRequestActionPayload, RequestActionDTO } from 'cca-api';

const mockRequestActionDTO: RequestActionDTO = {
  id: 379,
  type: 'FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_COMPLETED',
  payload: {
    payloadType: 'FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_UPLOAD_COMPLETED_PAYLOAD',
    details: {
      targetYear: 2026,
      files: ['004e24de-301e-4f77-a406-b9c27cc60369'],
    },
    results: {
      totalFilesUploaded: 1,
      facilitiesSucceeded: 0,
      facilitiesFailed: 1,
      uploadSummaryFile: 'a6ef8ecc-8b12-446b-983a-b81cd6c87e9f',
      submittedDate: '2027-01-01T00:00:00Z',
    },
    uploadAttachments: {
      '004e24de-301e-4f77-a406-b9c27cc60369': 'dummy-fail.csv',
      'a6ef8ecc-8b12-446b-983a-b81cd6c87e9f': 'Upload_Summary.csv',
    },
  } as FacilityPerformanceAccountTemplateDataUploadCompletedRequestActionPayload,
  requestId: 'ADS_1-PAT-1',
  requestType: 'FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD',
  competentAuthority: 'ENGLAND',
  submitter: 'sec-adm1 user',
  creationDate: '2026-08-25T11:31:36.300265Z',
};

export const mockRequestActionStatePATCSVUpload: RequestActionState = {
  action: mockRequestActionDTO,
};
