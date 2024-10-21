import { ItemDTO, ItemDTOResponse, RequestActionInfoDTO, RequestDetailsDTO } from 'cca-api';

export const mockWorkflowDetails: RequestDetailsDTO = {
  id: 'ACC-ADS_2T00002',
  requestType: 'TARGET_UNIT_ACCOUNT_CREATION',
  requestStatus: 'COMPLETED',
  creationDate: '2024-06-18',
};

export const mockEmptyRequestItems: ItemDTOResponse = {
  items: [],
  totalItems: 0,
};

export const mockRequestActions: RequestActionInfoDTO[] = [
  {
    id: 2,
    type: 'TARGET_UNIT_ACCOUNT_CREATION_SUBMITTED',
    submitter: 'sector user',
    creationDate: '2024-06-18T19:45:48.887068',
  },
];

export const mockResquestItemsDTO: ItemDTO[] = [
  {
    creationDate: '2024-06-01',
    requestId: 'ACC-ADS_2T00001',
    taskId: 1,
    requestType: 'requestType',
    taskType: 'TARGET_UNIT_ACCOUNT_CREATION_SUBMITTED',
    taskAssigneeType: 'OPERATOR',
    isNew: true,
  },
];

export const mockRequestItems: ItemDTOResponse = {
  items: mockResquestItemsDTO,
  totalItems: mockResquestItemsDTO.length,
};
