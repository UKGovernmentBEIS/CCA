import { RequestTaskState } from '@netz/common/store';
import { NonComplianceEnforcementResponseNoticePeerReviewRequestTaskPayload } from '@requests/common';

export const mockEnforcementResponseNoticePeerReviewPayload: NonComplianceEnforcementResponseNoticePeerReviewRequestTaskPayload =
  {
    payloadType: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_PEER_REVIEW_PAYLOAD',
    sendEmailNotification: true,
    enforcementResponseNotice: {
      type: 'PENALTY',
      file: 'uuid-1',
      comments: 'Test enforcement response notice comments',
    },
    penaltyReissue: false,
    nonComplianceAttachments: {
      'uuid-1': 'enforcement-response-notice.pdf',
      'uuid-2': 'attachment2.pdf',
    },
    decision: null,
    peerReviewAttachments: {},
    sectionsCompleted: {},
  };

export const mockEnforcementResponseNoticePeerReviewRequestTaskState: RequestTaskState = {
  requestTaskItem: {
    allowedRequestTaskActions: [],
    requestInfo: {
      id: 'REQ123',
      type: 'NON_COMPLIANCE',
      requestMetadata: {},
    },
    requestTask: {
      id: 123,
      type: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_APPLICATION_PEER_REVIEW',
      assigneeUserId: 'user123',
      assigneeFullName: 'John Doe',
      payload: mockEnforcementResponseNoticePeerReviewPayload,
    },
    userAssignCapable: true,
  },
  relatedTasks: [],
  timeline: [],
  taskReassignedTo: null,
  isEditable: true,
};
