import { RequestActionState } from '@netz/common/store';

import { RequestActionDTO } from 'cca-api';

import { PerformanceDataTp6, PerformanceDataUploadedActionPayload } from '../performance-data-upload-submitted.types';

const performanceDataTp6: PerformanceDataTp6 = {
  type: 'TP6',
  sector: null,
  performanceResult: {
    tpPerformance: 6566713.20022,
    tpPerformancePercent: 0.0564114,
    tpOutcome: 'TARGET_MET',
  },
  secondaryDetermination: {
    co2Emissions: 24077.9484008,
    priBuyOutCarbon: 0,
    prevBuyOutCo2: 0,
    prevSurplusUsed: 0,
    prevSurplusGained: 0,
    secondaryBuyOutCo2: 0,
    secondaryBuyOutCost: 0,
    carbonUnderTarget: 0,
    energyCarbonUnderTarget: 0,
    tpCarbonFactor: 0,
  },
  primaryDetermination: {
    tpCarbonFactor: 0.0399138,
    co2Emissions: 24077.9484008,
    surplusGained: 508.0,
    priBuyOutCarbon: 0,
    priBuyOutCost: 0,
    energyCarbonUnderTarget: null,
    carbonUnderTarget: null,
    surplusUsed: null,
  },
  reportType: 'PRIMARY',
  targetUnitDetails: {
    tuIdentifier: null,
    operatorName: null,
    numOfFacilities: null,
    energyCarbonUnit: 'ENERGY_KWH',
    byStartDate: null,
    byEnergyCarbon: null,
    percentTarget: 0.03786,
  },
  reportDate: '2025-02-07',
  actualTargetPeriodPerformance: null,
  targetType: null,
  reportVersion: 1,
  templateVersion: null,
  targetPeriod: null,
};

const performanceDataUploadedActionPayload: PerformanceDataUploadedActionPayload = {
  payloadType: 'PERFORMANCE_DATA_SPREADSHEET_PROCESSING_SUBMITTED_PAYLOAD',
  targetPeriodType: 'TP6',
  accountReportFile: {
    name: 'CONF-T00008_TPR_TP6_V1.xlsx',
    uuid: 'bf49ca9c-f532-4542-a103-705b55aa061c',
  },
  performanceData: performanceDataTp6,
};

const mockRequestActionDTO: RequestActionDTO = {
  id: 64,
  type: 'PERFORMANCE_DATA_SPREADSHEET_PROCESSING_SUBMITTED',
  payload: performanceDataUploadedActionPayload,
  requestId: 'ADS_53-T00021-TPR-TP6-V1',
  requestType: 'PERFORMANCE_DATA_SPREADSHEET_PROCESSING',
  requestAccountId: 56,
  competentAuthority: 'ENGLAND',
  submitter: 'sector user',
  creationDate: '2025-01-21T12:54:11.164626Z',
};

export const mockRequestActionStatePerformanceDataUpload: RequestActionState = {
  action: mockRequestActionDTO,
};
