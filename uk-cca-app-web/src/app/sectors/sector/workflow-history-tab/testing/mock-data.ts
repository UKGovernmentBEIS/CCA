import { RequestDetailsSearchResults } from 'cca-api';

export const mockRequestDetailsSearchResultsData: RequestDetailsSearchResults = {
  requestDetails: [
    {
      id: 'ADS_1-S2502',
      requestType: 'SECTOR_MOA',
      requestStatus: 'COMPLETED',
      creationDate: '2025-02-11',
      requestMetadata: {
        type: 'SECTOR_MOA',
        parentRequestId: 'S2502',
        sectorAcronym: 'ADS_1',
      } as any,
    },
    {
      id: 'ADS_1-S2501',
      requestType: 'SECTOR_MOA',
      requestStatus: 'COMPLETED',
      creationDate: '2025-02-11',
      requestMetadata: {
        type: 'SECTOR_MOA',
        parentRequestId: 'S2501',
        sectorAcronym: 'ADS_1',
      },
    },
  ],
  total: 2,
};

export function filterByRequestType(requestType: string): RequestDetailsSearchResults {
  const arr = mockRequestDetailsSearchResultsData.requestDetails.filter((rd) => rd.requestType === requestType);
  return { total: arr.length, requestDetails: arr };
}
