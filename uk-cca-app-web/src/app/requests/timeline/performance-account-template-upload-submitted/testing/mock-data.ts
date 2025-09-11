import { RequestActionState } from '@netz/common/store';

import { RequestActionDTO } from 'cca-api';

import { PATUploadedActionPayload } from '../pat-upload-submitted.types';

const payload: PATUploadedActionPayload = {
  payloadType: 'PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING_SUBMITTED_PAYLOAD',
  businessId: 'ADS_1-T00001',
  targetPeriodType: 'TP6',
  targetPeriodYear: null,
  data: {
    targetUnitIdentityAndPerformance: {
      improvementAccountedPercentage: -0.0215,
      improvementAchievedPercentage: -1.99,
      targetType: 'NOVEM_CARBON',
      performanceImpactedByAnyImplementedMeasures: 'Yes',
      performanceImpactedByAnyImplementedMeasuresSupportingText: null,
    },
    energyOrCarbonSavingActionsAndMeasuresImplementedItems: [],
    file: {
      name: 'ADS_1-T00001_PAT_TP6.xlsx',
      uuid: 'd435d7e3-0f99-4152-b39d-e1c5f44ce041',
    },
  },
};
const mockPATRequestActionDTO: RequestActionDTO = {
  id: 64,
  type: 'PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING_SUBMITTED',
  payload: payload,
  requestId: 'ADS-T00045-PAT-TP6-V1',
  requestType: 'PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING',
  requestAccountId: 56,
  competentAuthority: 'ENGLAND',
  submitter: 'sector user',
  creationDate: '2025-01-21T12:54:11.164626Z',
};

export const mockRequestActionStatePATUpload: RequestActionState = {
  action: mockPATRequestActionDTO,
};
