import { RequestTaskItemDTO, UnderlyingAgreementActivationDetails } from 'cca-api';

import { UNAActivationRequestTaskPayload } from '../underlying-agreement-activation.types';

const mockUnderlyingAgreementActivation: UnderlyingAgreementActivationDetails = {
  evidenceFiles: ['evidenceFile'],
  comments: 'My comments',
};

const mockUnaRequestTaskPayload: UNAActivationRequestTaskPayload = {
  payloadType: 'UNDERLYING_AGREEMENT_APPLICATION_SUBMIT_PAYLOAD',
  underlyingAgreementActivationDetails: mockUnderlyingAgreementActivation,
  sectionsCompleted: {
    underlyingAgreementActivationDetails: 'COMPLETED',
  },
  underlyingAgreementActivationAttachments: {
    evidenceFile: 'evidenceFile.xlsx',
  },
};

export const mockRequestTaskItemDTO: RequestTaskItemDTO = {
  requestTask: {
    id: 20,
    type: 'UNDERLYING_AGREEMENT_APPLICATION_ACTIVATION',
    payload: mockUnaRequestTaskPayload,
    assignable: true,
    assigneeUserId: '088fe8e5-9eb9-49d0-a6d0-d2f78031fe79',
    assigneeFullName: 'sector user',
    startDate: '2024-08-05T15:47:22.695292Z',
  },
  allowedRequestTaskActions: [
    'UNDERLYING_AGREEMENT_ACTIVATION_SAVE_APPLICATION',
    'UNDERLYING_AGREEMENT_ACTIVATION_UPLOAD_ATTACHMENT',
    'UNDERLYING_AGREEMENT_ACTIVATION_NOTIFY_OPERATOR_FOR_DECISION',
  ],
  userAssignCapable: false,
  requestInfo: {
    id: 'ADS_53-T00002-UNA',
    type: 'UNDERLYING_AGREEMENT',
    competentAuthority: 'ENGLAND',
    accountId: 15,
    requestMetadata: {
      type: 'UNDERLYING_AGREEMENT',
    },
  },
};
