import { AccountSearchResultInfoDTO, AccountSearchResults } from 'cca-api';

const mockAccountSearchResultInfoDTOList: AccountSearchResultInfoDTO[] = [
  {
    id: 1,
    name: 'Target unit name 01',
    businessId: 'AIC-T00038',
    status: 'LIVE',
  },
  {
    id: 2,
    name: 'Target unit name 02',
    businessId: 'AIC-T00039',
    status: 'NEW',
  },
  {
    id: 3,
    name: 'Target unit name 03',
    businessId: 'AIC-T00040',
    status: 'NEW',
  },
  {
    id: 4,
    name: 'Target unit name 04',
    businessId: 'AIC-T00041',
    status: 'LIVE',
  },
];
export const mockAccountSearchResults: AccountSearchResults = {
  accounts: mockAccountSearchResultInfoDTOList,
  total: mockAccountSearchResultInfoDTOList.length,
};
