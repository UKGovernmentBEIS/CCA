import { Injectable } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import { CcaPeerReviewDecision } from 'cca-api';

export interface UnderlyingAgreementPeerReviewState {
  decision: CcaPeerReviewDecision;
  attachments: Record<string, string>;
}

const initialState: UnderlyingAgreementPeerReviewState = {
  decision: null,
  attachments: {},
};

@Injectable()
export class UnderlyingAgreementPeerReviewStore extends SignalStore<UnderlyingAgreementPeerReviewState> {
  constructor() {
    super(initialState);
  }
}
