import { BuyOutSurplusTransactionHistoryDTO } from 'cca-api';

export const mockTransactionHistory: BuyOutSurplusTransactionHistoryDTO[] = [
  {
    id: 59,
    submitter: 'Regulator England',
    submissionDate: '2025-05-28T19:03:38.019926Z',
    payload: {
      comments: 'under appeal giwrgos test',
      evidenceFiles: {
        'd046aada-cdcc-47eb-bbc8-e857350ff76c': 'dummyxlsx (1).xlsx',
        'dd068798-f350-4667-8d0d-2dd9ad1130c9': 'ADS_1-T00001_PAT_TP6_success.xlsx',
        'ff5e9dde-ca0e-457b-87f0-bb82ff5a6bc7': 'pat_reporting.xlsx',
      },
      type: 'PAYMENT_STATUS_CHANGED',
      paymentStatus: 'UNDER_APPEAL',
    },
  },
  {
    id: 58,
    submitter: 'Regulator England',
    submissionDate: '2025-05-28T16:25:32.083041Z',
    payload: {
      comments: 'test',
      evidenceFiles: {
        '2487bd21-1793-4b7e-92be-5b0786d65063': 'dummyxlsx.xlsx',
      },
      type: 'AMOUNT_CHANGED',
      amount: '12.22',
    },
  },
  {
    id: 57,
    submitter: 'Regulator England',
    submissionDate: '2025-05-28T16:24:47.172609Z',
    payload: {
      comments: 'test',
      evidenceFiles: {
        '6a09e3fa-8ada-4e16-8a9f-b18289238a3a': 'dummyxlsx.xlsx',
      },
      type: 'AMOUNT_CHANGED',
      amount: '12.22',
    },
  },
  {
    id: 56,
    submitter: 'Regulator England',
    submissionDate: '2025-05-28T11:03:50.343932Z',
    payload: {
      evidenceFiles: {},
      type: 'PAYMENT_STATUS_CHANGED',
      paymentStatus: 'AWAITING_PAYMENT',
    },
  },
  {
    id: 55,
    submitter: 'Regulator England',
    submissionDate: '2025-05-27T17:49:28.53886Z',
    payload: {
      comments: '6543',
      evidenceFiles: {},
      type: 'PAYMENT_STATUS_CHANGED',
      paymentStatus: 'NOT_REQUIRED',
    },
  },
  {
    id: 54,
    submitter: 'Regulator England',
    submissionDate: '2025-05-27T17:48:25.809567Z',
    payload: {
      comments: 'ytr',
      evidenceFiles: {},
      type: 'PAYMENT_STATUS_CHANGED',
      paymentStatus: 'UNDER_APPEAL',
    },
  },
  {
    id: 53,
    submitter: 'Regulator England',
    submissionDate: '2025-05-27T17:46:15.481506Z',
    payload: {
      evidenceFiles: {},
      type: 'PAYMENT_STATUS_CHANGED',
      paymentStatus: 'AWAITING_PAYMENT',
    },
  },
  {
    id: 52,
    submitter: 'Regulator England',
    submissionDate: '2025-05-27T17:45:46.887617Z',
    payload: {
      evidenceFiles: {},
      type: 'PAYMENT_STATUS_CHANGED',
      paymentStatus: 'AWAITING_PAYMENT',
    },
  },
  {
    id: 51,
    submitter: 'Regulator England',
    submissionDate: '2025-05-27T17:45:38.978404Z',
    payload: {
      evidenceFiles: {},
      type: 'PAYMENT_STATUS_CHANGED',
      paymentStatus: 'AWAITING_PAYMENT',
    },
  },
  {
    id: 50,
    submitter: 'Regulator England',
    submissionDate: '2025-05-27T17:34:50.05478Z',
    payload: {
      comments: 'gdf',
      evidenceFiles: {},
      type: 'PAYMENT_STATUS_CHANGED',
      paymentStatus: 'UNDER_APPEAL',
    },
  },
];
