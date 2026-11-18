import { Injectable } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import { CcaPeerReviewDecision } from 'cca-api';

export interface UnderlyingAgreementVariationPeerReviewState {
  decision: CcaPeerReviewDecision;
  attachments: Record<string, string>;
}

const initialState: UnderlyingAgreementVariationPeerReviewState = {
  decision: null,
  attachments: {},
};

@Injectable()
export class UnderlyingAgreementVariationPeerReviewStore extends SignalStore<UnderlyingAgreementVariationPeerReviewState> {
  constructor() {
    super(initialState);
  }
}
