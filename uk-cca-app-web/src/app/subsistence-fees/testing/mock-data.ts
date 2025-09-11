import { SubsistenceFeesRunRequestMetadata } from 'cca-api';

import { SubsistenceFeesState } from '../subsistence-fees.store';

export const subsistenceFeesStateMockData: SubsistenceFeesState = {
  currentPage: 0,
  workflowsHistory: [
    {
      id: 'S2509',
      requestType: 'SUBSISTENCE_FEES_RUN',
      requestStatus: 'COMPLETED',
      creationDate: '2025-02-10',
      requestMetadata: {
        type: 'SUBSISTENCE_FEES_RUN',
        chargingYear: '2025',
        sectorsReports: {
          '1': {
            moaType: 'SECTOR_MOA',
            sectorAcronym: 'ADS_1',
            sectorName: 'Aerospace_1',
            issueDate: '2025-02-10',
            succeeded: true,
            errors: [],
          },
        },
        accountsReports: {},
        failedInvoices: 0,
        sentInvoices: 1,
      } as SubsistenceFeesRunRequestMetadata,
    },
    {
      id: 'S2508',
      requestType: 'SUBSISTENCE_FEES_RUN',
      requestStatus: 'COMPLETED',
      creationDate: '2025-02-10',
      requestMetadata: {
        type: 'SUBSISTENCE_FEES_RUN',
        chargingYear: '2025',
        sectorsReports: {
          '1': {
            moaType: 'SECTOR_MOA',
            sectorAcronym: 'ADS_1',
            sectorName: 'Aerospace_1',
            issueDate: '2025-02-10',
            succeeded: true,
            errors: [],
          },
        },
        accountsReports: {},
        failedInvoices: 0,
        sentInvoices: 1,
      } as SubsistenceFeesRunRequestMetadata,
    },
  ],
  totalWorkflowHistoryItems: 2,
  badgeNumber: 0,
  isValidChargeDate: true,
  runInProgress: false,
  pageSize: 0,
  subsistenceFeesRuns: [
    {
      runId: 3,
      paymentRequestId: 'S2503',
      submissionDate: '2025-01-01',
      paymentStatus: 'CANCELLED',
      markFacilitiesStatus: 'CANCELLED',
      currentTotalAmount: '0',
      outstandingTotalAmount: '0',
    },
    {
      runId: 2,
      paymentRequestId: 'S2502',
      submissionDate: '2025-01-01',
      paymentStatus: 'CANCELLED',
      markFacilitiesStatus: 'CANCELLED',
      currentTotalAmount: '0',
      outstandingTotalAmount: '0',
    },
    {
      runId: 1,
      paymentRequestId: 'S2501',
      submissionDate: '2025-01-01',
      paymentStatus: 'AWAITING_PAYMENT',
      markFacilitiesStatus: 'IN_PROGRESS',
      currentTotalAmount: '2850',
      outstandingTotalAmount: '2549',
    },
  ],
  totalSubsistenceFeesRunItems: 3,
};
