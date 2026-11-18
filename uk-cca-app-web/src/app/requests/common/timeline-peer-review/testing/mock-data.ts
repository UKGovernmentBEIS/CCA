import { RequestActionState } from '@netz/common/store';
import { PeerReviewDecisionActionPayload } from '@requests/common';

import { RequestActionDTO } from 'cca-api';

export const acceptedPayload: PeerReviewDecisionActionPayload = {
  payloadType: 'ADMIN_TERMINATION_PEER_REVIEW_SUBMITTED_PAYLOAD',
  decision: {
    type: 'AGREE',
    notes: 'notes',
    files: [],
  },
  peerReviewAttachments: { '8e3b-442c-s235-12ab': 'file.csv' },
};

export const actionPayload: RequestActionDTO = {
  id: 6,
  type: 'ADMIN_TERMINATION_APPLICATION_PEER_REVIEWER_ACCEPTED',
  payload: acceptedPayload,
  requestId: '1',
  requestType: 'ADMIN_TERMINATION',
  competentAuthority: 'ENGLAND',
  submitter: 'Regulator England',
  creationDate: '2025-02-11T13:17:35.149924Z',
};

export const mockPeerReviewActionState: RequestActionState = {
  action: actionPayload,
};
