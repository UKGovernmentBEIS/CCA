import { RequestActionState } from '@netz/common/store';

import { RequestActionDTO, TargetUnitMoaGeneratedRequestActionPayload } from 'cca-api';

export const targetUnitMoaGeneratedMock: TargetUnitMoaGeneratedRequestActionPayload = {
  payloadType: 'TARGET_UNIT_MOA_GENERATED_PAYLOAD',
  businessId: 'ADS_52-T00001',
  paymentRequestId: 'S2515',
  chargingYear: '2025',
  transactionId: 'CCATM01203',
  moaDocument: {
    name: '2025 Target Unit MoA - ADS_52-T00001 - CCATM01203.pdf',
    uuid: '85d8a4d8-2fa1-4242-ba63-ad5604ee5467',
  },
  recipients: [
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
} as any; // This is to bypass `chargingYear` type of `SubsistenceFeesRunCompletedRequestActionPayloadChargingYear`;

export const targetUnitMoaGeneratedActionDTO: RequestActionDTO = {
  id: 41,
  type: 'TARGET_UNIT_MOA_GENERATED',
  submitter: 'Regulator England',
  creationDate: '2025-03-12T11:30:34.155828Z',
  payload: targetUnitMoaGeneratedMock,
};

export const targetUnitMoaGeneratedActionStateMock: RequestActionState = {
  action: targetUnitMoaGeneratedActionDTO,
};
