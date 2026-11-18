import { SectorPerformanceAccountTemplateDataReportListDTO } from 'cca-api';

export const mockPatData: SectorPerformanceAccountTemplateDataReportListDTO = {
  items: [
    {
      accountId: 1,
      targetUnitAccountBusinessId: 'ADS-T0003',
      operatorName: 'Lorem Ipsum',
      submissionDate: '2025-04-25T00:00:00',
      status: 'SUBMITTED',
      submissionType: 'FINAL',
    },
    {
      accountId: 2,
      targetUnitAccountBusinessId: 'ADS-T0004',
      operatorName: 'Lorem Ipsum',
      submissionDate: '2025-04-25T00:00:00',
      status: 'SUBMITTED',
      submissionType: 'FINAL',
    },
    {
      accountId: 3,
      targetUnitAccountBusinessId: 'ADS-T0005',
      operatorName: 'Lorem Ipsum',
      submissionDate: '2025-04-25T00:00:00',
      status: 'SUBMITTED',
      submissionType: 'FINAL',
    },
    {
      accountId: 4,
      targetUnitAccountBusinessId: 'ADS-T0006',
      operatorName: 'Lorem Ipsum',
      submissionDate: '2025-04-25T00:00:00',
      status: 'OUTSTANDING',
      submissionType: 'INTERIM',
    },
    {
      accountId: 5,
      targetUnitAccountBusinessId: 'ADS-T0007',
      operatorName: 'Lorem Ipsum',
      submissionDate: '2025-04-25T00:00:00',
      status: 'OUTSTANDING',
      submissionType: 'INTERIM',
    },
  ],
  total: 5,
};
