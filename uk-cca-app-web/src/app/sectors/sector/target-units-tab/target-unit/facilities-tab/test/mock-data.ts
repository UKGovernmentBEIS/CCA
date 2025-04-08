import { FacilityDataDetailsDTO } from 'cca-api';

export const mockFacilityDetails: FacilityDataDetailsDTO = {
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
};
