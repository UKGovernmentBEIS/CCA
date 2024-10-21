import {
  AdditionalNoticeRecipientDTO,
  AdminTerminationWithdrawRequestTaskPayload,
  CaExternalContactsDTO,
  CcaNotifyOperatorForDecisionRequestTaskActionPayload,
  RegulatorUsersAuthoritiesInfoDTO,
} from 'cca-api';

export const mockReasonForAdminTerminationWithdrawPayload: AdminTerminationWithdrawRequestTaskPayload = {
  payloadType: '',
  sectionsCompleted: { adminTerminationWithdrawReasonDetails: 'COMPLETED' },
  adminTerminationAttachments: {},
  adminTerminationWithdrawReasonDetails: { explanation: 'mplah mplah', relevantFiles: [] },
};

export const mockNotifyOperatorOfDecisionPayload: CcaNotifyOperatorForDecisionRequestTaskActionPayload = {
  payloadType: 'NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD',
  decisionNotification: {
    signatory: 'reg-userid',
    externalContacts: [1],
    operators: [],
    sectorUsers: ['sec-id2'],
  },
};

export const mockWithdrawAdminTerminationNotifyOperatorDefaultUsers: AdditionalNoticeRecipientDTO[] = [
  {
    email: 'test@example.com',
    firstName: 'fname',
    lastName: 'lname',
    type: 'OPERATOR',
    userId: 'oper-id1',
  },
];

export const mockWithdrawAdminTerminationNotifyOperatorAdditionalUsers: AdditionalNoticeRecipientDTO[] = [
  {
    email: 'test-add@example.com',
    firstName: 'fname2',
    lastName: 'lname2',
    type: 'SECTOR_USER',
    userId: 'sec-id2',
  },
];

export const mockWithdrawAdminTerminationNotifyOperatorExternalContacts: CaExternalContactsDTO = {
  caExternalContacts: [
    {
      description: 'descr',
      email: 'ext-cont@cca.uk',
      id: 1,
      name: 'John',
    },
  ],
};

export const mockWithdrawAdminTerminationNotifyOperatorRegulatorAuthorities: RegulatorUsersAuthoritiesInfoDTO = {
  caUsers: [
    {
      firstName: 'reg-fname',
      lastName: 'reg-lname',
      userId: 'reg-userid',
    },
  ],
};
