import { FacilityInfoDTO } from 'cca-api';

export const mockFacilityDetails: FacilityInfoDTO = {
  facilityId: '1',
  status: 'LIVE',
  chargeStartDate: '2024-01-01',
  siteName: 'Fac 1',
  schemeExitDate: '2024-02-02',
  address: {
    city: 'city',
    country: 'country',
    line1: 'address line 1',
    postcode: '505050',
  },
  facilityCertificationDetails: [
    {
      status: 'CERTIFIED',
      startDate: '2024-12-12',
      certificationPeriod: 'CP6',
      certificationPeriodStartDate: '2023-07-01',
      certificationPeriodEndDate: '2025-05-30',
    },
    {
      status: 'NOT_YET_DEFINED',
      startDate: '2025-06-01',
      certificationPeriod: 'CP7',
      certificationPeriodStartDate: '2025-06-01',
      certificationPeriodEndDate: '2027-03-31',
    },
  ],
};
