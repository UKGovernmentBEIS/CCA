import { SectorPerformanceAccountTemplateDataReportListDTO } from 'cca-api';

export const mockPatAccountsReport: SectorPerformanceAccountTemplateDataReportListDTO = {
  items: [
    {
      id: 1,
      businessId: 'ADS-T00040',
      name: 'Operator name',
      submissionDate: '2024-04-25T00:00:00',
      status: 'SUBMITTED',
    },
    {
      id: 2,
      businessId: 'ADS-T00041',
      name: 'Operator name',
      submissionDate: '2024-08-12T00:00:00',
      status: 'SUBMITTED',
    },
    {
      id: 3,
      businessId: 'ADS-T00042',
      name: 'Operator name',
      status: 'OUTSTANDING',
    },
  ],
  total: 3,
};

export const mockPatFacilitiesReport: SectorPerformanceAccountTemplateDataReportListDTO = {
  items: [
    {
      id: 40,
      parentId: 100,
      businessId: 'ADS-F00040',
      name: 'Facility 40',
      submissionDate: '2027-04-25T00:00:00Z',
      status: 'SUBMITTED',
    },
    {
      id: 41,
      parentId: 100,
      businessId: 'ADS-F00041',
      name: 'Facility 41',
      submissionDate: '2027-08-12T00:00:00Z',
      status: 'SUBMITTED',
    },
    {
      id: 42,
      parentId: 101,
      businessId: 'ADS-F00042',
      name: 'Facility 42',
      status: 'OUTSTANDING',
    },
  ],
  total: 3,
};
