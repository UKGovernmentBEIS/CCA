import { RequestActionState } from '@netz/common/store';

import { RequestActionDTO, SectorMoaGeneratedRequestActionPayload } from 'cca-api';

export const sectorMoaGeneratedMock: SectorMoaGeneratedRequestActionPayload = {
  payloadType: 'SECTOR_MOA_GENERATED_PAYLOAD',
  paymentRequestId: 'S2513',
  chargingYear: '2025',
  transactionId: 'CCACM01204',
  moaDocument: {
    name: '2025 Sector MoA - ADS_1 - CCACM01204.pdf',
    uuid: '2f7cf12e-5347-46eb-80df-5a16a732146c',
  },
  recipients: [
    {
      name: 'Fred_1 William_1',
      email: 'fredwilliam_1@agindustries.org.uk',
      recipientType: 'SECTOR_CONTACT',
    },
    {
      name: 'Fred_2 William_2',
      email: 'fredwilliam_2@agindustries.org.uk',
      recipientType: 'SECTOR_CONTACT',
    },
  ],
} as any; // This is to bypass `chargingYear` type of `SubsistenceFeesRunCompletedRequestActionPayloadChargingYear`;

export const sectorMoaGeneratedActionDTO: RequestActionDTO = {
  id: 30,
  type: 'SECTOR_MOA_GENERATED',
  submitter: 'Regulator England',
  creationDate: '2025-03-06T11:33:50.248854Z',
  payload: sectorMoaGeneratedMock,
};

export const sectorMoaGeneratedActionStateMock: RequestActionState = {
  action: sectorMoaGeneratedActionDTO,
};
