// Dummy data for the CCA3 (facility) buy-out and surplus screens. Shapes follow the generated `cca-api` models, so
// values can be passed straight to the services or used to stub their responses while the API is unavailable.
import {
  BuyOutSurplusTransactionDetailHistoryDTO,
  BuyOutSurplusTransactionDetailUpdateAmountDTO,
  BuyOutSurplusTransactionsListDTO,
  BuyOutSurplusTransactionsListSearchCriteria,
  BuyOutSurplusTransactionUpdatePaymentStatusDTO,
  FacilityBuyOutSurplusTransactionDetailSearchResultsDTO,
  FacilityBuyOutSurplusTransactionDetailsSearchCriteria,
  FacilityBuyOutSurplusTransactionDTO,
  SurplusGainedDTO,
  SurplusHistoryDTO,
  SurplusUpdateDTO,
} from 'cca-api';

/**
 * Search criteria for the facility transactions list. `schemeVersion` is fixed to CCA_3 because this screen only
 * serves the facility workflow.
 */
export const mockCca3FacilityTransactionsSearchCriteria: BuyOutSurplusTransactionsListSearchCriteria = {
  term: undefined,
  schemeVersion: 'CCA_3',
  targetPeriodType: 'TP8',
  paymentStatus: undefined,
  pageNumber: 0,
  pageSize: 10,
};

/** First page of the facility transactions list: every payment status appears at least once. */
export const mockCca3FacilityTransactionsPageOne: BuyOutSurplusTransactionsListDTO = {
  total: 17,
  transactions: [
    {
      id: 1001,
      accountBusinessId: 'ADS_1-T00001',
      operatorName: 'TPS Ltd',
      transactionCode: 'CCA080001',
      creationDate: '2026-04-01T09:15:00Z',
      schemeVersion: 'CCA_3',
      resourceType: 'FACILITY',
      paymentStatus: 'AWAITING_PAYMENT',
      buyOutFee: '1250.00',
    },
    {
      id: 1002,
      accountBusinessId: 'ADS_1-T00002',
      operatorName: 'TPS Ltd',
      transactionCode: 'CCA080002',
      creationDate: '2026-04-01T09:20:00Z',
      schemeVersion: 'CCA_3',
      resourceType: 'FACILITY',
      paymentStatus: 'AWAITING_REFUND',
      buyOutFee: '0.00',
    },
    {
      id: 1003,
      accountBusinessId: 'ADS_1-T00003',
      operatorName: 'Green Cement Ltd',
      transactionCode: 'CCA080003',
      creationDate: '2026-04-02T11:05:00Z',
      schemeVersion: 'CCA_3',
      resourceType: 'FACILITY',
      paymentStatus: 'PAID',
      buyOutFee: '430.50',
    },
    {
      id: 1004,
      accountBusinessId: 'ADS_1-T00004',
      operatorName: 'Green Cement Ltd',
      transactionCode: 'CCA080004',
      creationDate: '2026-04-02T11:10:00Z',
      schemeVersion: 'CCA_3',
      resourceType: 'FACILITY',
      paymentStatus: 'REFUNDED',
      buyOutFee: '0.00',
    },
    {
      id: 1005,
      accountBusinessId: 'ADS_1-T00005',
      operatorName: 'Northwind Steel',
      transactionCode: 'CCA080005',
      creationDate: '2026-04-03T08:45:00Z',
      schemeVersion: 'CCA_3',
      resourceType: 'FACILITY',
      paymentStatus: 'NOT_REQUIRED',
      buyOutFee: '0.00',
    },
    {
      id: 1006,
      accountBusinessId: 'ADS_1-T00006',
      operatorName: 'Northwind Steel',
      transactionCode: 'CCA080006',
      creationDate: '2026-04-03T14:30:00Z',
      schemeVersion: 'CCA_3',
      resourceType: 'FACILITY',
      paymentStatus: 'UNDER_APPEAL',
      buyOutFee: '980.75',
    },
    {
      id: 1007,
      accountBusinessId: 'ADS_1-T00007',
      operatorName: 'Orion Chemicals',
      transactionCode: 'CCA080007',
      creationDate: '2026-04-04T07:00:00Z',
      schemeVersion: 'CCA_3',
      resourceType: 'FACILITY',
      paymentStatus: 'TERMINATED',
      buyOutFee: '0.00',
    },
    {
      id: 1008,
      accountBusinessId: 'ADS_1-T00008',
      operatorName: 'Orion Chemicals',
      transactionCode: 'CCA080008',
      creationDate: '2026-04-04T16:40:00Z',
      schemeVersion: 'CCA_3',
      resourceType: 'FACILITY',
      paymentStatus: 'AWAITING_PAYMENT',
      buyOutFee: '315.20',
    },
    {
      id: 1009,
      accountBusinessId: 'ADS_1-T00009',
      operatorName: 'Pennine Paper',
      transactionCode: 'CCA080009',
      creationDate: '2026-04-05T10:12:00Z',
      schemeVersion: 'CCA_3',
      resourceType: 'FACILITY',
      paymentStatus: 'AWAITING_PAYMENT',
      buyOutFee: '0.00',
    },
    {
      id: 1010,
      accountBusinessId: 'ADS_1-T00010',
      operatorName: 'Pennine Paper',
      transactionCode: 'CCA080010',
      creationDate: '2026-04-05T13:55:00Z',
      schemeVersion: 'CCA_3',
      resourceType: 'FACILITY',
      paymentStatus: 'PAID',
      buyOutFee: '76.40',
    },
  ],
};

/** Second page of the same list, as returned for `pageNumber: 1`. */
export const mockCca3FacilityTransactionsPageTwo: BuyOutSurplusTransactionsListDTO = {
  total: 17,
  transactions: [
    {
      id: 1011,
      accountBusinessId: 'ADS_1-T00011',
      operatorName: 'Pennine Paper',
      transactionCode: 'CCA080011',
      creationDate: '2026-04-06T09:00:00Z',
      schemeVersion: 'CCA_3',
      resourceType: 'FACILITY',
      paymentStatus: 'AWAITING_REFUND',
      buyOutFee: '0.00',
    },
    {
      id: 1012,
      accountBusinessId: 'ADS_1-T00012',
      operatorName: undefined,
      transactionCode: 'CCA080012',
      creationDate: '2026-04-06T09:30:00Z',
      schemeVersion: 'CCA_3',
      resourceType: 'FACILITY',
      paymentStatus: 'PAID',
      buyOutFee: '148.90',
    },
    {
      id: 1013,
      accountBusinessId: 'ADS_1-T00013',
      operatorName: 'Solent Glass',
      transactionCode: 'CCA080013',
      creationDate: '2026-04-07T10:45:00Z',
      schemeVersion: 'CCA_3',
      resourceType: 'FACILITY',
      paymentStatus: 'REFUNDED',
      buyOutFee: undefined,
    },
    {
      id: 1014,
      accountBusinessId: 'ADS_1-T00014',
      operatorName: 'Solent Glass',
      transactionCode: 'CCA080014',
      creationDate: '2026-04-07T15:05:00Z',
      schemeVersion: 'CCA_3',
      resourceType: 'FACILITY',
      paymentStatus: 'NOT_REQUIRED',
      buyOutFee: '0.00',
    },
    {
      id: 1015,
      accountBusinessId: 'ADS_1-T00015',
      operatorName: 'Teeside Refining',
      transactionCode: 'CCA080015',
      creationDate: '2026-04-08T08:20:00Z',
      schemeVersion: 'CCA_3',
      resourceType: 'FACILITY',
      paymentStatus: 'UNDER_APPEAL',
      buyOutFee: '2040.00',
    },
    {
      id: 1016,
      accountBusinessId: 'ADS_1-T00016',
      operatorName: 'Teeside Refining',
      transactionCode: 'CCA080016',
      creationDate: '2026-04-08T12:35:00Z',
      schemeVersion: 'CCA_3',
      resourceType: 'FACILITY',
      paymentStatus: 'TERMINATED',
      buyOutFee: '0.00',
    },
    {
      id: 1017,
      accountBusinessId: 'ADS_1-T00017',
      operatorName: 'Wye Valley Foods',
      transactionCode: 'CCA080017',
      creationDate: '2026-04-09T11:00:00Z',
      schemeVersion: 'CCA_3',
      resourceType: 'FACILITY',
      paymentStatus: 'AWAITING_PAYMENT',
      buyOutFee: '512.30',
    },
  ],
};

/** Empty list response, for the "no transactions" state. */
export const mockCca3FacilityTransactionsEmpty: BuyOutSurplusTransactionsListDTO = {
  total: 0,
  transactions: [],
};

/** Fee transaction awaiting payment: file present, two facilities still unmarked. */
export const mockFacilityTransactionFee: FacilityBuyOutSurplusTransactionDTO = {
  id: 1001,
  transactionCode: 'CCA080001',
  accountBusinessId: 'ADS_1-T00001',
  operatorName: 'TPS Ltd',
  targetPeriodType: 'TP8',
  chargeType: 'FEE',
  fileInfoDTO: {
    name: 'CCA080001 buy-out and surplus summary report.pdf',
    uuid: '3f1c1e6c-9f2b-4c81-9d78-6b2a4e0d1a11',
  },
  creationDate: '2026-04-01T09:15:00Z',
  dueDate: '2026-06-30',
  invoicedBuyOutFee: '1250.00',
  paymentStatus: 'AWAITING_PAYMENT',
  expectedAmount: '1250.00',
  receivedAmount: '0.00',
  amountAllocated: '0.00',
  expectedMarkedFacilities: 12,
  actualMarkedFacilities: 10,
};

/** Refund transaction: no file, refund still awaited. */
export const mockFacilityTransactionRefund: FacilityBuyOutSurplusTransactionDTO = {
  id: 1002,
  transactionCode: 'CCA080002',
  accountBusinessId: 'ADS_1-T00002',
  operatorName: 'TPS Ltd',
  targetPeriodType: 'TP8',
  chargeType: 'REFUND',
  fileInfoDTO: undefined,
  creationDate: '2026-04-01T09:20:00Z',
  dueDate: '2026-06-30',
  invoicedBuyOutFee: '0.00',
  paymentStatus: 'AWAITING_REFUND',
  expectedAmount: '430.50',
  receivedAmount: '0.00',
  amountAllocated: '0.00',
  expectedMarkedFacilities: 0,
  actualMarkedFacilities: 0,
};

/** Paid transaction that was overpaid: received amount exceeds the expected amount. */
export const mockFacilityTransactionPaid: FacilityBuyOutSurplusTransactionDTO = {
  id: 1003,
  transactionCode: 'CCA080003',
  accountBusinessId: 'ADS_1-T00003',
  operatorName: 'Green Cement Ltd',
  targetPeriodType: 'TP8',
  chargeType: 'FEE',
  fileInfoDTO: {
    name: 'CCA080003 buy-out and surplus summary report.pdf',
    uuid: '8c5d2a44-1b6e-4a3f-9c5e-2f0b7d9a6e22',
  },
  creationDate: '2026-04-02T11:05:00Z',
  dueDate: '2026-06-30',
  invoicedBuyOutFee: '430.50',
  paymentStatus: 'PAID',
  expectedAmount: '430.50',
  receivedAmount: '500.00',
  amountAllocated: '430.50',
  expectedMarkedFacilities: 5,
  actualMarkedFacilities: 5,
};

/** Terminated transaction: no file and no monetary values, only the identifying fields. */
export const mockFacilityTransactionTerminated: FacilityBuyOutSurplusTransactionDTO = {
  id: 1007,
  transactionCode: 'CCA080007',
  accountBusinessId: 'ADS_1-T00007',
  operatorName: 'Orion Chemicals',
  targetPeriodType: 'TP8',
  chargeType: 'FEE',
  paymentStatus: 'TERMINATED',
  expectedMarkedFacilities: 0,
  actualMarkedFacilities: 0,
};

/** Search criteria for the per-facility rows of a transaction. */
export const mockFacilityTransactionDetailsSearchCriteria: FacilityBuyOutSurplusTransactionDetailsSearchCriteria = {
  term: undefined,
  paymentStatus: undefined,
  pageNumber: 0,
  pageSize: 10,
};

/** Same criteria with a term and a payment status applied, for exercising the filters. */
export const mockFacilityTransactionDetailsFilteredSearchCriteria: FacilityBuyOutSurplusTransactionDetailsSearchCriteria =
  {
    term: 'Cardiff',
    paymentStatus: 'AWAITING_PAYMENT',
    pageNumber: 0,
    pageSize: 10,
  };

/** First page of facility rows: covers every payment status, both `hasHistory` values and fee variations. */
export const mockFacilityTransactionDetailsPageOne: FacilityBuyOutSurplusTransactionDetailSearchResultsDTO = {
  total: 20,
  transactionDetails: [
    {
      id: 2001,
      facilityBusinessId: 'ADS_1-F00001',
      siteName: 'Cardiff Site',
      paymentStatus: 'AWAITING_PAYMENT',
      invoicedBuyFee: '125.00',
      buyOutFee: '120.00',
      hasHistory: true,
    },
    {
      id: 2002,
      facilityBusinessId: 'ADS_1-F00002',
      siteName: 'Cardiff Site 2',
      paymentStatus: 'AWAITING_PAYMENT',
      invoicedBuyFee: '125.00',
      buyOutFee: '125.00',
      hasHistory: false,
    },
    {
      id: 2003,
      facilityBusinessId: 'ADS_1-F00003',
      siteName: 'Swansea Plant',
      paymentStatus: 'PAID',
      invoicedBuyFee: '80.50',
      buyOutFee: '80.50',
      hasHistory: true,
    },
    {
      id: 2004,
      facilityBusinessId: 'ADS_1-F00004',
      siteName: 'Newport Works',
      paymentStatus: 'PAID',
      invoicedBuyFee: '80.50',
      buyOutFee: '75.00',
      hasHistory: true,
    },
    {
      id: 2005,
      facilityBusinessId: 'ADS_1-F00005',
      siteName: 'Wrexham Works',
      paymentStatus: 'AWAITING_REFUND',
      invoicedBuyFee: '0.00',
      buyOutFee: '45.00',
      hasHistory: true,
    },
    {
      id: 2006,
      facilityBusinessId: 'ADS_1-F00006',
      siteName: 'Bangor Site',
      paymentStatus: 'REFUNDED',
      invoicedBuyFee: '0.00',
      buyOutFee: '45.00',
      hasHistory: true,
    },
    {
      id: 2007,
      facilityBusinessId: 'ADS_1-F00007',
      siteName: 'Llanelli Site',
      paymentStatus: 'NOT_REQUIRED',
      invoicedBuyFee: '0.00',
      buyOutFee: '',
      hasHistory: false,
    },
    {
      id: 2008,
      facilityBusinessId: 'ADS_1-F00008',
      siteName: 'Cardiff Bay',
      paymentStatus: 'UNDER_APPEAL',
      invoicedBuyFee: '60.00',
      buyOutFee: '60.00',
      hasHistory: true,
    },
    {
      id: 2009,
      facilityBusinessId: 'ADS_1-F00009',
      siteName: 'Bridgend Site',
      paymentStatus: 'UNDER_APPEAL',
      invoicedBuyFee: '60.00',
      buyOutFee: '55.00',
      hasHistory: false,
    },
    {
      id: 2010,
      facilityBusinessId: 'ADS_1-F00010',
      siteName: undefined,
      paymentStatus: 'TERMINATED',
      invoicedBuyFee: '0.00',
      buyOutFee: '0.00',
      hasHistory: false,
    },
  ],
};

/** Second page of facility rows, as returned for `pageNumber: 1`. */
export const mockFacilityTransactionDetailsPageTwo: FacilityBuyOutSurplusTransactionDetailSearchResultsDTO = {
  total: 20,
  transactionDetails: [
    {
      id: 2011,
      facilityBusinessId: 'ADS_1-F00011',
      siteName: 'Cardiff Site 3',
      paymentStatus: 'AWAITING_PAYMENT',
      invoicedBuyFee: '125.00',
      buyOutFee: '118.40',
      hasHistory: false,
    },
    {
      id: 2012,
      facilityBusinessId: 'ADS_1-F00012',
      siteName: 'Neath Abbey',
      paymentStatus: 'AWAITING_PAYMENT',
      invoicedBuyFee: '125.00',
      buyOutFee: '0.00',
      hasHistory: false,
    },
    {
      id: 2013,
      facilityBusinessId: 'ADS_1-F00013',
      siteName: 'Port Talbot Works',
      paymentStatus: 'PAID',
      invoicedBuyFee: '999999.99',
      buyOutFee: '999999.99',
      hasHistory: true,
    },
    {
      id: 2014,
      facilityBusinessId: 'ADS_1-F00014',
      siteName: 'Merthyr Plant',
      paymentStatus: 'PAID',
      invoicedBuyFee: '0.00',
      buyOutFee: '12.00',
      hasHistory: true,
    },
    {
      id: 2015,
      facilityBusinessId: 'ADS_1-F00015',
      siteName: 'Caerphilly Works',
      paymentStatus: 'AWAITING_REFUND',
      invoicedBuyFee: '0.00',
      buyOutFee: '330.25',
      hasHistory: false,
    },
    {
      id: 2016,
      facilityBusinessId: 'ADS_1-F00016',
      siteName: 'Chepstow Site',
      paymentStatus: 'REFUNDED',
      invoicedBuyFee: '0.00',
      buyOutFee: '330.25',
      hasHistory: false,
    },
    {
      id: 2017,
      facilityBusinessId: 'ADS_1-F00017',
      siteName: 'Abergavenny Mill',
      paymentStatus: 'NOT_REQUIRED',
      invoicedBuyFee: '0.00',
      buyOutFee: '0.00',
      hasHistory: false,
    },
    {
      id: 2018,
      facilityBusinessId: 'ADS_1-F00018',
      siteName: 'Monmouth Works',
      paymentStatus: 'UNDER_APPEAL',
      invoicedBuyFee: '210.00',
      buyOutFee: '210.00',
      hasHistory: true,
    },
    {
      id: 2019,
      facilityBusinessId: 'ADS_1-F00019',
      siteName: 'Pontypool Site',
      paymentStatus: 'UNDER_APPEAL',
      invoicedBuyFee: '210.00',
      buyOutFee: '240.00',
      hasHistory: true,
    },
    {
      id: 2020,
      facilityBusinessId: 'ADS_1-F00020',
      siteName: 'Ebbw Vale Works',
      paymentStatus: 'TERMINATED',
      invoicedBuyFee: '0.00',
      buyOutFee: '0.00',
      hasHistory: false,
    },
  ],
};

/** Empty details response, for the "no facilities match" state. */
export const mockFacilityTransactionDetailsEmpty: FacilityBuyOutSurplusTransactionDetailSearchResultsDTO = {
  total: 0,
  transactionDetails: [],
};

/** Change history of a single facility transaction detail row, newest first. */
export const mockFacilityTransactionDetailHistory: BuyOutSurplusTransactionDetailHistoryDTO[] = [
  {
    id: 3006,
    submitter: 'Regulator England',
    submissionDate: '2026-05-14T15:20:31.482903Z',
    payload: {
      type: 'PAYMENT_STATUS_CHANGED',
      paymentStatus: 'PAID',
      paymentDate: '2026-05-14',
      comments: 'Payment received in full',
      evidenceFiles: {
        '2f4a7c11-5b3d-4e9a-8c61-7d0e2a9b4f31': 'remittance-advice.pdf',
      },
    },
  },
  {
    id: 3005,
    submitter: 'Regulator England',
    submissionDate: '2026-05-12T11:03:50.343932Z',
    payload: {
      type: 'AMOUNT_CHANGED',
      amount: '120.00',
      comments: 'Corrected amount after appeal review',
      evidenceFiles: {
        '9b1e6a02-4c88-4d7f-9a12-6e5b0f3c8d74': 'appeal-decision.xlsx',
      },
    },
  },
  {
    id: 3004,
    submitter: 'Regulator England',
    submissionDate: '2026-05-10T09:41:12.736451Z',
    payload: {
      type: 'PAYMENT_STATUS_CHANGED',
      paymentStatus: 'UNDER_APPEAL',
      comments: 'Operator appealed the buy-out fee',
      evidenceFiles: {},
    },
  },
  {
    id: 3003,
    submitter: 'Regulator England',
    submissionDate: '2026-05-08T16:24:47.172609Z',
    payload: {
      type: 'AMOUNT_CHANGED',
      amount: '125.00',
      comments: undefined,
      evidenceFiles: {},
    },
  },
  {
    id: 3002,
    submitter: 'Regulator England',
    submissionDate: '2026-05-06T13:12:08.910455Z',
    payload: {
      type: 'PAYMENT_STATUS_CHANGED',
      paymentStatus: 'AWAITING_REFUND',
      paymentDate: '2026-05-06',
      comments: 'Facility overpaid, refund due',
      evidenceFiles: {},
    },
  },
  {
    id: 3001,
    submitter: 'Regulator England',
    submissionDate: '2026-05-04T10:05:59.238117Z',
    payload: {
      type: 'PAYMENT_STATUS_CHANGED',
      paymentStatus: 'NOT_REQUIRED',
      comments: undefined,
      evidenceFiles: {},
    },
  },
];

/** Facility surplus with history, as shown on the facility buy-out and surplus tab. */
export const mockFacilitySurplus: SurplusGainedDTO = {
  schemeVersion: 'CCA_3',
  surplusGained: '1234.56',
  hasHistory: true,
};

/** Facility with no surplus gained and no history. */
export const mockFacilitySurplusNone: SurplusGainedDTO = {
  schemeVersion: 'CCA_3',
  surplusGained: '0.00',
  hasHistory: false,
};

/** Change history backing `mockFacilitySurplus`, newest first. */
export const mockFacilitySurplusHistory: SurplusHistoryDTO[] = [
  {
    surplusGained: '1234.56',
    comments: 'Confirmed surplus after performance data review',
    submitter: 'Regulator England',
    submissionDate: '2026-05-18T14:22:05.114873Z',
  },
  {
    surplusGained: '1100.00',
    comments: 'Correction requested by the operator',
    submitter: 'Regulator England',
    submissionDate: '2026-05-16T09:47:51.602338Z',
  },
  {
    surplusGained: '0.00',
    comments: undefined,
    submitter: 'Regulator England',
    submissionDate: '2026-05-02T08:15:33.947126Z',
  },
];

/** Request bodies for the transaction update endpoints, ready to pass to the update service methods. */
export const mockFacilityTransactionUpdateAmountPayload: BuyOutSurplusTransactionDetailUpdateAmountDTO = {
  amount: '120.00',
  comments: 'Corrected amount after appeal review',
  evidenceFiles: {
    '9b1e6a02-4c88-4d7f-9a12-6e5b0f3c8d74': 'appeal-decision.xlsx',
  },
};

export const mockFacilityTransactionUpdatePaymentStatusPayload: BuyOutSurplusTransactionUpdatePaymentStatusDTO = {
  status: 'PAID',
  comments: 'Payment received in full',
  evidenceFiles: {
    '2f4a7c11-5b3d-4e9a-8c61-7d0e2a9b4f31': 'remittance-advice.pdf',
  },
  paymentDate: '2026-05-14',
  detailIds: [3],
};

/** Request body for `updateFacilitySurplusGained`. */
export const mockFacilitySurplusUpdatePayload: SurplusUpdateDTO = {
  newSurplusGained: '1500.00',
  comments: 'Surplus increased after facility audit',
};
