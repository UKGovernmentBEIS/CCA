import { Service } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import { CcaPeerReviewDecision } from 'cca-api';

export interface AdminTerminationPeerReviewState {
  decision: CcaPeerReviewDecision;
  attachments: Record<string, string>;
}

const initialState: AdminTerminationPeerReviewState = {
  decision: null,
  attachments: {},
};

@Service({ autoProvided: false })
export class AdminTerminationPeerReviewStore extends SignalStore<AdminTerminationPeerReviewState> {
  constructor() {
    super(initialState);
  }
}
