import { Injectable } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import { CcaPeerReviewDecision } from 'cca-api';

export interface PeerReviewState {
  decision: CcaPeerReviewDecision;
  attachments: Record<string, string>;
}

const initialState: PeerReviewState = {
  decision: null,
  attachments: {},
};

@Injectable()
export class PeerReviewStore extends SignalStore<PeerReviewState> {
  constructor() {
    super(initialState);
  }
}
