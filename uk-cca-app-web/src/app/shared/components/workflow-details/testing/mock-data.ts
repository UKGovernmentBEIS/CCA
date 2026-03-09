import { ItemDTOResponse, RequestActionInfoDTO, RequestDetailsDTO } from 'cca-api';

export const mockWorkflowDetails: RequestDetailsDTO = {
  id: 'ADS_1-S2513',
  requestType: 'SECTOR_MOA',
  requestStatus: 'COMPLETED',
  creationDate: '2025-03-06',
  requestMetadata: {
    type: 'SECTOR_MOA',
    parentRequestId: 'S2513',
    transactionId: 'CCACM01204',
    sectorAcronym: 'ADS_1',
  } as any,
};

export const mockEmptyRequestItems: ItemDTOResponse = {
  items: [],
  totalItems: 0,
};

export const mockRequestActions: RequestActionInfoDTO[] = [
  {
    id: 30,
    type: 'SECTOR_MOA_GENERATED',
    submitter: 'Regulator England',
    creationDate: '2025-03-06T11:33:50.248854Z',
  },
];
