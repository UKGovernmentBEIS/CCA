import { RequestActionState } from '@netz/common/store';

import {
  Cca3ExistingFacilitiesMigrationAccountProcessingActivatedRequestActionPayload,
  Cca3ExistingFacilitiesMigrationAccountProcessingSubmittedRequestActionPayload,
  RequestActionDTO,
} from 'cca-api';

export const cca3MigrationCompletedPayload: Cca3ExistingFacilitiesMigrationAccountProcessingSubmittedRequestActionPayload =
  {
    payloadType: 'CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_SUBMITTED_PAYLOAD',
    facilityMigrationDataList: [
      {
        accountBusinessId: 'ADS_1-T00001',
        facilityBusinessId: 'ADS_1-F00001',
        facilityName: 'fac1-1-1',
        participatingInCca3Scheme: true,
        baselineDate: '2022-01-01',
        measurementType: 'ENERGY_KWH',
        energyCarbonFactor: 153.1234568,
        usedReportingMechanism: true,
        tp7Improvement: 55.0,
        tp8Improvement: 12.0,
        tp9Improvement: 97.0,
        totalFixedEnergy: 0.000001,
        totalVariableEnergy: 123.4444,
        totalThroughput: 123.0,
        throughputUnit: 'unit',
        calculatorFileUuid: 'cc5be65e-e8c0-4163-b741-8b366271066d',
        calculatorFileName: 'ADS_1-F00001.xlsx',
      },
      {
        accountBusinessId: 'ADS_1-T00001',
        facilityBusinessId: 'ADS_1-F00002',
        facilityName: 'fac1-1-2',
        participatingInCca3Scheme: false,
      },
    ],
    defaultContacts: [
      {
        name: 'resp1@cca.uk user',
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
  };

export const cca3MigrationCompletedRequestActionDTO: RequestActionDTO = {
  id: 66,
  type: 'CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_SUBMITTED',
  payload: cca3MigrationCompletedPayload,
  requestId: 'ADS_1-T00001-CCA3-EFM-2',
  requestType: 'CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING',
  requestAccountId: 1,
  competentAuthority: 'ENGLAND',
  creationDate: '2025-09-24T12:15:40.870364Z',
};

export const cca3MigrationCompletedActionStateMock: RequestActionState = {
  action: cca3MigrationCompletedRequestActionDTO,
};

export const cca3MigrationActivatedPayload: Cca3ExistingFacilitiesMigrationAccountProcessingActivatedRequestActionPayload =
  {
    payloadType: 'CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATED_PAYLOAD',
    activationDetails: {
      evidenceFiles: ['d873d3a0-aaf6-42a1-ab6d-400d539c79fc'],
    },
    activationAttachments: {
      'd873d3a0-aaf6-42a1-ab6d-400d539c79fc': 'sample_profile1.png',
    },
    decisionNotification: {
      signatory: '38ddb238-97e7-4dd1-9799-57d5a43a6ce2',
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
        name: 'Fred_50 William_50',
        email: 'fredwilliam_50@agindustries.org.uk',
        recipientType: 'SECTOR_CONTACT',
      },
    ],
    usersInfo: {
      '38ddb238-97e7-4dd1-9799-57d5a43a6ce2': {
        name: 'Regulator England',
      },
    },
    officialNotice: {
      name: 'CCA3 Migration Activated underlying agreement cover letter.pdf',
      uuid: 'bbf43918-a3c1-4022-b1ef-3085015417a0',
    },
    underlyingAgreementDocument: {
      name: 'ADS_50-T00004 CCA3 Underlying Agreement v1.pdf',
      uuid: '70d68454-3000-416b-9998-7fe742b54ac1',
    },
  };

export const cca3MigrationActivatedRequestActionDTO: RequestActionDTO = {
  id: 132,
  type: 'CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATED',
  payload: cca3MigrationActivatedPayload,
  requestId: 'ADS_50-T00004-CCA3-EFM-2',
  requestType: 'CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING',
  requestAccountId: 18,
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator England',
  creationDate: '2025-10-31T10:59:51.394981Z',
};

export const cca3MigrationActivatedActionStateMock: RequestActionState = {
  action: cca3MigrationActivatedRequestActionDTO,
};
