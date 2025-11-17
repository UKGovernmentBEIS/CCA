import { RequestTaskItemDTO } from 'cca-api';

import { CCA3MigrationRequestTaskPayload } from '../types';

const mockPayload: CCA3MigrationRequestTaskPayload = {
  payloadType: 'CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION_PAYLOAD',
  sendEmailNotification: true,
  activationDetails: {
    evidenceFiles: ['c8ce37c4-be75-4198-9f45-4e70f6b3e0c5'],
    comments: 'My comments',
  },
  sectionsCompleted: {
    activationDetails: 'COMPLETED',
  },
  activationAttachments: {
    'bbca1294-963f-4d49-bce2-fd5bb59b049f': 'sample_profile1.png',
    'c8ce37c4-be75-4198-9f45-4e70f6b3e0c5': 'sample_profile1.png',
  },
};

export const mockRequestTaskItemDTO: RequestTaskItemDTO = {
  requestTask: {
    id: 136,
    type: 'CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION',
    payload: mockPayload,
    assignable: true,
    assigneeUserId: '38ddb238-97e7-4dd1-9799-57d5a43a6ce2',
    assigneeFullName: 'Regulator England',
    startDate: '2025-10-01T16:35:13.492152Z',
  },
  allowedRequestTaskActions: [
    'CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION_NOTIFY_OPERATOR_FOR_DECISION',
    'CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION_SAVE_APPLICATION',
    'CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION_UPLOAD_ATTACHMENT',
    'CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION_CANCEL_APPLICATION',
  ],
  userAssignCapable: true,
  requestInfo: {
    id: 'ADS_1-T00001-CCA3-EFM-1',
    type: 'CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING',
    competentAuthority: 'ENGLAND',
    accountId: 1,
    requestMetadata: {
      type: 'CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING',
      parentRequestId: 'CCA3-EFM-1',
      accountBusinessId: 'ADS_1-T00001',
      cca3Participating: true,
    },
    creationDate: '2025-10-01T16:35:13.709016Z',
  },
};
