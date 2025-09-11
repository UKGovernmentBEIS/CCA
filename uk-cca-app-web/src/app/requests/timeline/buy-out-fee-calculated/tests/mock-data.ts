import { RequestActionState } from '@netz/common/store';

import { RequestActionDTO, TP6BuyOutCalculatedAccountProcessingSubmittedRequestActionPayload } from 'cca-api';

export const buyoutFeeCalculated: TP6BuyOutCalculatedAccountProcessingSubmittedRequestActionPayload = {
  payloadType: 'TP6_BUY_OUT_ACCOUNT_PROCESSING_SUBMITTED_PAYLOAD',
  details: {
    submissionType: 'PRIMARY',
    tpOutcome: 'BUY_OUT_REQUIRED',
    paymentStatus: 'AWAITING_PAYMENT',
    transactionCode: 'CCA060001',
    officialNotice: {
      name: 'CCA060001 Primary buy-out MoA.pdf',
      uuid: 'a4246b8f-2ce1-4ca8-8b43-4b27235099d7',
    },
    dueDate: '2025-07-01',
    runId: 'BOS-TP6003',
    targetPeriodType: 'TP6',
    performanceDataReportVersion: 1,
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
      name: 'Fred_52 William_52',
      email: 'fredwilliam_52@agindustries.org.uk',
      recipientType: 'SECTOR_CONTACT',
    },
  ],
  buyOutCalculatedDetails: {
    priBuyOutCarbon: 1,
    priBuyOutCost: 25,
    previousPaidFees: 0,
    buyOutFee: 25,
  },
};

export const buyoutFeeCalculatedRequestActionDTO: RequestActionDTO = {
  id: 47,
  type: 'TP6_BUY_OUT_ACCOUNT_PROCESSING_SUBMITTED',
  payload: buyoutFeeCalculated,
  requestId: 'ADS_52-T00001-BOS-TP6003',
  requestType: 'BUY_OUT_SURPLUS_ACCOUNT_PROCESSING',
  requestAccountId: 4,
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator England',
  creationDate: '2025-04-09T11:36:29.249126Z',
};

export const buyoutFeeCalculatedActionStateMock: RequestActionState = {
  action: buyoutFeeCalculatedRequestActionDTO,
};
