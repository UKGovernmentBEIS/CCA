import { AccountPerformanceDataReportDetailsDTO, AccountPerformanceDataStatusInfoDTO } from 'cca-api';

import { AccountPerformanceDataReportState } from '../../../performance-report-store';

const mockAccountPerformanceDataStatusInfoDTO: AccountPerformanceDataStatusInfoDTO = {
  targetPeriodType: 'TP6',
  editable: true,
  locked: true,
  targetPeriodName: 'TP6 (2024)',
  reportVersion: 1,
};

const mockAccountPerformanceDataReportDetailsDTO: AccountPerformanceDataReportDetailsDTO = {
  targetPeriodReport: {
    name: 'ADS_53-T00022_TPR_TP6_V1.xlsx',
    uuid: 'b3314abb-dce4-4ef0-9736-c9b9b700ed91',
  },
  submissionDate: '2025-02-21T12:58:56.34169Z',
  submissionType: 'PRIMARY',
  reportVersion: 1,
  energyCarbonUnit: 'ENERGY_MWH',
  tpPerformance: '1.00000000000000000000',
  percentTarget: '0.120000000',
  tpPerformancePercent: '0E-20',
  tpOutcome: 'BUY_OUT_REQUIRED',
  carbonSurplusBuyOutDTO: {
    co2Emissions: '0.20020000000000000000',
    surplusUsed: '0E-20',
    surplusGained: '0E-20',
    priBuyOutCarbon: '1.00000000000000000000',
    priBuyOutCost: '25.00000000000000000000',
  },
  secondaryMoASurplusBuyOutDTO: null,
};

export const mockAccountPerformanceState: AccountPerformanceDataReportState = {
  statusInfo: mockAccountPerformanceDataStatusInfoDTO,
  reportDetails: mockAccountPerformanceDataReportDetailsDTO,
};
