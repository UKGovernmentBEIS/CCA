import { Service } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import { CcaPeerReviewDecision } from 'cca-api';

export interface UnARegulatorLedVariationPeerReviewState {
  decision: CcaPeerReviewDecision;
  attachments: Record<string, string>;
}

const initialState: UnARegulatorLedVariationPeerReviewState = {
  decision: null,
  attachments: {},
};

@Service({ autoProvided: false })
export class UnARegulatorLedVariationPeerReviewStore extends SignalStore<UnARegulatorLedVariationPeerReviewState> {
  constructor() {
    super(initialState);
  }
}
