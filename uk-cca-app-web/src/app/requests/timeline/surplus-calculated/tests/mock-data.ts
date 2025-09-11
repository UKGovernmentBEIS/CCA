import { RequestActionState } from '@netz/common/store';

import { RequestActionDTO, TP6SurplusCalculatedAccountProcessingSubmittedRequestActionPayload } from 'cca-api';

export const surplusCalculated: TP6SurplusCalculatedAccountProcessingSubmittedRequestActionPayload = {
  payloadType: 'TP6_SURPLUS_ACCOUNT_PROCESSING_SUBMITTED_PAYLOAD',
  details: {
    targetPeriodType: 'TP6',
    performanceDataReportVersion: 4,
    submissionType: 'SECONDARY',
    tpOutcome: 'TARGET_MET',
    paymentStatus: 'AWAITING_PAYMENT',
    transactionCode: 'CCA060006',
    officialNotice: {
      name: 'CCA060006 Secondary buy-out MoA.pdf',
      uuid: 'c7417c98-99ee-4376-a9dc-845e6c8ce36e',
    },
    dueDate: '2025-05-11',
    runId: 'BOS-TP6012',
  },
  defaultContacts: [
    {
      name: 'resp1 user',
      email: 'resp1@cca.uk',
      recipientType: 'RESPONSIBLE_PERSON',
    },
    {
      name: 'administr1 user',
      email: 'administr1@cca.uk',
      recipientType: 'ADMINISTRATIVE_CONTACT',
    },
    {
      name: 'Fred_1 William_1',
      email: 'fredwilliam_1@agindustries.org.uk',
      recipientType: 'SECTOR_CONTACT',
    },
  ],
  surplusCalculatedDetails: {
    surplusGained: 0,
    previousPaidFees: 0,
    overPaymentFee: 25,
  },
};

export const surplusCalculatedRequestActionDTO: RequestActionDTO = {
  id: 75,
  type: 'TP6_SURPLUS_ACCOUNT_PROCESSING_SUBMITTED',
  payload: surplusCalculated,
  requestId: 'ADS_1-T00001-BOS-TP6012',
  requestType: 'BUY_OUT_SURPLUS_ACCOUNT_PROCESSING',
  requestAccountId: 1,
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator England',
  creationDate: '2025-04-11T10:36:32.424989Z',
};

export const surplusCalculatedActionStateMock: RequestActionState = {
  action: surplusCalculatedRequestActionDTO,
};
