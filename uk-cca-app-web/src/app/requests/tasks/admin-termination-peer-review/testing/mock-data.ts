import { RequestTaskState } from '@netz/common/store';

import { AdminTerminationPeerReviewRequestTaskPayload } from 'cca-api';

export const mockAdminTerminationPeerReviewPayload: AdminTerminationPeerReviewRequestTaskPayload = {
  payloadType: 'ADMIN_TERMINATION_PEER_REVIEW_PAYLOAD',
  sendEmailNotification: true,
  adminTerminationReasonDetails: {
    explanation: 'Test termination notes',
    reason: 'FAILURE_TO_PAY',
  },
  adminTerminationAttachments: {
    'uuid-1': 'attachment1.pdf',
    'uuid-2': 'attachment2.pdf',
  },
  decision: null,
  peerReviewAttachments: {},
  sectionsCompleted: {},
};

export const mockAdminTerminationPeerReviewRequestTaskState: RequestTaskState = {
  requestTaskItem: {
    allowedRequestTaskActions: ['ADMIN_TERMINATION_PEER_REVIEW_SAVE_APPLICATION'],
    requestInfo: {
      id: 'REQ123',
      type: 'ADMIN_TERMINATION',
      requestMetadata: {},
    },
    requestTask: {
      id: 123,
      type: 'ADMIN_TERMINATION_PEER_REVIEW',
      assigneeUserId: 'user123',
      assigneeFullName: 'John Doe',
      payload: mockAdminTerminationPeerReviewPayload,
    },
    userAssignCapable: true,
  },
  relatedTasks: [],
  timeline: [],
  taskReassignedTo: null,
  isEditable: true,
};
