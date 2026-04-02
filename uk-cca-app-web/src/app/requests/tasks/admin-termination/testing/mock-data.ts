import {
  AdditionalNoticeRecipientDTO,
  AdminTerminationSubmitRequestTaskPayload,
  CaExternalContactsDTO,
  CcaNotifyOperatorForDecisionRequestTaskActionPayload,
  RegulatorUsersAuthoritiesInfoDTO,
} from 'cca-api';

export const mockReasonForAdminTerminationPayload: AdminTerminationSubmitRequestTaskPayload = {
  payloadType: '',
  sectionsCompleted: { adminTerminationReasonDetails: 'COMPLETED' },
  adminTerminationAttachments: {},
  adminTerminationReasonDetails: { explanation: 'mplah mplah', reason: 'FAILURE_TO_AGREE', relevantFiles: [] },
};

export const mockNotifyOperatorOfDecisionPayload: CcaNotifyOperatorForDecisionRequestTaskActionPayload = {
  payloadType: 'CCA_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD',
  decisionNotification: {
    signatory: 'reg-userid',
    externalContacts: [1],
    operators: [],
    sectorUsers: ['sec-id2'],
  },
};

export const mockAdminTerminationNotifyOperatorDefaultUsers: AdditionalNoticeRecipientDTO[] = [
  {
    email: 'test@example.com',
    firstName: 'fname',
    lastName: 'lname',
    type: 'OPERATOR',
    userId: 'oper-id1',
  },
];

export const mockAdminTerminationNotifyOperatorAdditionalUsers: AdditionalNoticeRecipientDTO[] = [
  {
    email: 'test-add@example.com',
    firstName: 'fname2',
    lastName: 'lname2',
    type: 'SECTOR_USER',
    userId: 'sec-id2',
  },
];

export const mockAdminTerminationNotifyOperatorExternalContacts: CaExternalContactsDTO = {
  caExternalContacts: [
    {
      description: 'descr',
      email: 'ext-cont@cca.uk',
      id: 1,
      name: 'John',
    },
  ],
};

export const mockAdminTerminationNotifyOperatorRegulatorAuthorities: RegulatorUsersAuthoritiesInfoDTO = {
  caUsers: [
    {
      firstName: 'reg-fname',
      lastName: 'reg-lname',
      userId: 'reg-userid',
    },
  ],
};
