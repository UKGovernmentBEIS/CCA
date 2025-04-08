import { ItemDTOResponse, RequestActionInfoDTO, RequestDetailsDTO } from 'cca-api';

export const mockWorkflowDetails: RequestDetailsDTO = {
  id: 'S2502',
  requestType: 'SUBSISTENCE_FEES_RUN',
  requestStatus: 'COMPLETED_WITH_FAILURES',
  creationDate: '2025-02-11',
  requestMetadata: {
    type: 'SUBSISTENCE_FEES_RUN',
    chargingYear: '2025',
    sectorsReports: {
      '1': {
        moaType: 'SECTOR_MOA',
        sectorAcronym: 'ADS_1',
        sectorName: 'Aerospace_1',
        issueDate: '2025-02-11',
        succeeded: true,
        errors: [],
      },
    },
    accountsReports: {},
    failedInvoices: 0,
    sentInvoices: 1,
  },
} as any; // This is to bypass `chargingYear` type of `SubsistenceFeesRunCompletedRequestActionPayloadChargingYear`

export const mockEmptyRequestItems: ItemDTOResponse = {
  items: [],
  totalItems: 0,
};

export const mockRequestActions: RequestActionInfoDTO[] = [
  {
    id: 8,
    type: 'SUBSISTENCE_FEES_RUN_COMPLETED',
    submitter: 'Regulator England',
    creationDate: '2025-02-11T17:15:21.331846Z',
  },
  {
    id: 7,
    type: 'SUBSISTENCE_FEES_RUN_SUBMITTED',
    submitter: 'Regulator England',
    creationDate: '2025-02-11T17:15:20.618994Z',
  },
  {
    id: 197,
    type: 'SUBSISTENCE_FEES_RUN_COMPLETED_WITH_FAILURES',
    submitter: 'Regulator England',
    creationDate: '2025-02-10T10:26:04.043Z',
  },
];
