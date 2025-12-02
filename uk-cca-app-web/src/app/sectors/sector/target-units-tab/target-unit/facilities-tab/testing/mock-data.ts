import {
  FacilityInfoDTO,
  ItemDTOResponse,
  RequestActionInfoDTO,
  RequestDetailsDTO,
  RequestDetailsSearchResults,
} from 'cca-api';

export const mockFacilityDetails: FacilityInfoDTO = {
  facilityId: 1,
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

export const mockRequestDetailsSearchResultsData: RequestDetailsSearchResults = {
  requestDetails: [
    {
      id: 'ADS_1-F00002-AUDT-1',
      requestType: 'FACILITY_AUDIT',
      requestStatus: 'IN_PROGRESS',
      creationDate: '2025-11-06',
    },
  ],
  total: 1,
};

export function filterByRequestType(requestType: string): RequestDetailsSearchResults {
  const arr = mockRequestDetailsSearchResultsData.requestDetails.filter((rd) => rd.requestType === requestType);
  return { total: arr.length, requestDetails: arr };
}

export const mockWorkflowDetails: RequestDetailsDTO = {
  id: 'ADS_1-F00002-AUDT-1',
  requestType: 'FACILITY_AUDIT',
  requestStatus: 'IN_PROGRESS',
  creationDate: '2025-11-06',
};

export const mockEmptyRequestItems: ItemDTOResponse = {
  items: [
    {
      creationDate: '2025-11-11T11:59:54.569295Z',
      requestId: 'ADS_1-F00002-AUDT-1',
      requestType: 'FACILITY_AUDIT',
      taskId: 135,
      taskAssignee: {
        firstName: 'Regulator',
        lastName: 'England',
      },
      taskAssigneeType: 'REGULATOR',
      taskType: 'AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMIT',
      accountId: 1,
      accountName: 'tu1-oper1',
      businessId: 'ADS_1-T00001',
      competentAuthority: 'ENGLAND',
      sectorId: 1,
      sectorAcronym: 'ADS_1',
      sectorName: 'Aerospace_1',
      facilityId: 1,
      facilityBusinessId: 'ADS_1-F00002',
      siteName: 'fac1-1-2',
      isNew: false,
    },
  ],
  totalItems: 1,
};

export const mockRequestActions: RequestActionInfoDTO[] = [
  {
    id: 99,
    type: 'FACILITY_AUDIT_PRE_AUDIT_REVIEW_SUBMITTED',
    submitter: 'Regulator England',
    creationDate: '2025-11-11T11:59:54.508126Z',
  },
];
