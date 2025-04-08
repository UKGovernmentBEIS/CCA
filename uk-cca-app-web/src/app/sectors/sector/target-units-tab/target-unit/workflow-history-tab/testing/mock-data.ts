import { RequestDetailsSearchResults } from 'cca-api';

export const mockRequestDetailsSearchResultsData: RequestDetailsSearchResults = {
  requestDetails: [
    {
      id: 'ADS_53-T00001-S2513',
      requestType: 'TARGET_UNIT_MOA',
      requestStatus: 'COMPLETED',
      creationDate: '2025-03-06',
      requestMetadata: {
        type: 'TARGET_UNIT_MOA',
        parentRequestId: 'S2513',
        transactionId: 'CCATM01201',
        businessId: 'ADS_53-T00001',
      } as any,
    },
    {
      id: 'ADS_53-T00001-S2512',
      requestType: 'TARGET_UNIT_MOA',
      requestStatus: 'COMPLETED',
      creationDate: '2025-02-27',
      requestMetadata: {
        type: 'TARGET_UNIT_MOA',
        parentRequestId: 'S2512',
        transactionId: 'CCATM01200',
        businessId: 'ADS_53-T00001',
      },
    },
    {
      id: 'ADS_53-T00001-UNA',
      requestType: 'UNDERLYING_AGREEMENT',
      requestStatus: 'APPROVED',
      creationDate: '2025-02-27',
      requestMetadata: {
        type: 'UNDERLYING_AGREEMENT',
      },
    },
    {
      id: 'ADS_53-T00001-ACC',
      requestType: 'TARGET_UNIT_ACCOUNT_CREATION',
      requestStatus: 'COMPLETED',
      creationDate: '2025-02-27',
    },
  ],
  total: 4,
};

export function filterByRequestType(requestType: string): RequestDetailsSearchResults {
  const arr = mockRequestDetailsSearchResultsData.requestDetails.filter((rd) => rd.requestType === requestType);
  return { total: arr.length, requestDetails: arr };
}
