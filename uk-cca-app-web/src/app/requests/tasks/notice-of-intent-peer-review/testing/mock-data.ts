import { RequestTaskState } from '@netz/common/store';

import { NonComplianceNoticeOfIntentPeerReviewRequestTaskPayload } from 'cca-api';

export const mockNoticeOfIntentPeerReviewPayload: NonComplianceNoticeOfIntentPeerReviewRequestTaskPayload = {
  payloadType: 'NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEW_PAYLOAD',
  sendEmailNotification: true,
  noticeOfIntent: {
    file: 'uuid-1',
    comments: 'Test notice of intent comments',
  },
  nonComplianceAttachments: {
    'uuid-1': 'notice-of-intent.pdf',
    'uuid-2': 'attachment2.pdf',
  },
  decision: null,
  peerReviewAttachments: {},
  sectionsCompleted: {},
};

export const mockNoticeOfIntentPeerReviewRequestTaskState: RequestTaskState = {
  requestTaskItem: {
    allowedRequestTaskActions: ['NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEW_SAVE_APPLICATION'],
    requestInfo: {
      id: 'REQ123',
      type: 'NON_COMPLIANCE',
      requestMetadata: {},
    },
    requestTask: {
      id: 123,
      type: 'NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW',
      assigneeUserId: 'user123',
      assigneeFullName: 'John Doe',
      payload: mockNoticeOfIntentPeerReviewPayload,
    },
    userAssignCapable: true,
  },
  relatedTasks: [],
  timeline: [],
  taskReassignedTo: null,
  isEditable: true,
};
