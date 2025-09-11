import { of } from 'rxjs';

import {
  DocumentTemplateSearchResults,
  DocumentTemplateViewDTO,
  NotificationTemplateInfoDTO,
  NotificationTemplateSearchResults,
  NotificationTemplateViewDTO,
} from 'cca-api';

export const activatedRouteMock = {
  snapshot: {
    paramMap: {
      get: jest.fn().mockReturnValue(null),
    },
    queryParamMap: {
      get: jest.fn().mockImplementation((param) => {
        if (param === 'page') return '1';
        if (param === 'pageSize') return '30';
        return null;
      }),
    },
  },
  queryParamMap: of({
    get: jest.fn().mockImplementation((param) => {
      if (param === 'page') return '1';
      if (param === 'pageSize') return '30';
      return null;
    }),
  }),
};

export const mockNotificationTemplateViewDTO: NotificationTemplateViewDTO = {
  id: 10,
  name: 'GenericEmailTemplate',
  subject: 'An important letter from the CCA service',
  text: 'Dear ${responsibleUser},\n\nPlease find attached a communication from the Environment Agency about your Climate Change Agreement (${targetUnit}).\n\nIf you have any queries, please [contact the CCA Helpdesk](${contactRegulator}).\n\nIf you think you should not have received this email, you must [contact the CCA Helpdesk](${contactRegulator}).\n\nThis is an automatic email - please do not reply to this address.',
  eventTrigger: 'Multiple events',
  workflow: 'Multiple workflows',
  lastUpdatedDate: '2025-07-01T12:42:54.907428Z',
  documentTemplates: [
    {
      id: 9,
      name: 'Variation of Underlying agreement proposed cover letter',
      roleType: 'OPERATOR',
      workflow: 'Underlying Agreement Variation',
      lastUpdatedDate: '2024-10-02T03:00:00Z',
    },
    {
      id: 15,
      name: 'Buy-out secondary  notice',
      roleType: 'OPERATOR',
      workflow: 'Buy-out and surplus batch run',
      lastUpdatedDate: '2025-03-20T02:00:00Z',
    },
    {
      id: 1,
      name: 'Termination notice',
      roleType: 'OPERATOR',
      workflow: 'Admin Termination',
      lastUpdatedDate: '2024-07-23T03:00:00Z',
    },
  ],
};

const mockNotificationTemplateInfoDTO: NotificationTemplateInfoDTO[] = [
  {
    id: 10,
    name: 'GenericEmailTemplate',
    roleType: 'OPERATOR',
    workflow: 'Multiple workflows',
    lastUpdatedDate: '2024-07-23T03:00:00Z',
  },
  {
    id: 11,
    name: 'Generic Expiration Reminder Template',
    roleType: 'OPERATOR',
    workflow: 'Multiple workflows',
  },
  {
    id: 19,
    name: 'GenericSectorEmailTemplate',
    roleType: 'SECTOR_USER',
    workflow: 'Multiple workflows',
    lastUpdatedDate: '2025-01-31T02:00:00Z',
  },
];

export const mockNotificationTemplateSearchResults: NotificationTemplateSearchResults = {
  templates: mockNotificationTemplateInfoDTO,
  total: mockNotificationTemplateInfoDTO.length,
};

export const mockDocumentTemplateViewDTO: DocumentTemplateViewDTO = {
  id: 9,
  name: 'Variation of Underlying agreement proposed cover letter',
  workflow: 'Underlying Agreement Variation',
  lastUpdatedDate: '2024-10-02T03:00:00Z',
  notificationTemplateId: 10,
  fileUuid: '6a46fb77-12b7-4247-b6c6-25efb6d92f7f',
  filename: 'CR_L002_Variation_Approved.docx',
  notificationTemplate: {
    id: 10,
    name: 'GenericEmailTemplate',
    roleType: 'OPERATOR',
    workflow: 'Multiple workflows',
    lastUpdatedDate: '2025-07-01T12:42:54.907428Z',
  },
};

const mockDocumentTemplateInfoDTO: DocumentTemplateViewDTO[] = [
  {
    id: 3,
    name: 'Admin Termination Regulatory reason notice',
    workflow: 'Admin Termination',
    lastUpdatedDate: '2024-07-23T03:00:00Z',
  },
  {
    id: 14,
    name: 'Buy-out primary notice',
    workflow: 'Buy-out and surplus batch run',
    lastUpdatedDate: '2025-03-20T02:00:00Z',
  },
  {
    id: 17,
    name: 'Buyout Refund Claim Form',
    workflow: 'Buy-out and surplus batch run',
    lastUpdatedDate: '2025-06-11T03:00:00Z',
  },
  {
    id: 15,
    name: 'Buy-out secondary  notice',
    workflow: 'Buy-out and surplus batch run',
    lastUpdatedDate: '2025-03-20T02:00:00Z',
  },
  {
    id: 16,
    name: 'Buy-out secondary overpayment letter',
    workflow: 'Buy-out and surplus batch run',
    lastUpdatedDate: '2025-03-20T02:00:00Z',
  },
];

export const mockDocumentTemplateSearchResults: DocumentTemplateSearchResults = {
  templates: mockDocumentTemplateInfoDTO,
  total: mockDocumentTemplateInfoDTO.length,
};
