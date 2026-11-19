import { Service } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import { CcaPeerReviewDecision } from 'cca-api';

export interface EnforcementResponseNoticePeerReviewState {
  decision: CcaPeerReviewDecision;
  attachments: Record<string, string>;
}

const initialState: EnforcementResponseNoticePeerReviewState = {
  decision: null,
  attachments: {},
};

@Service({ autoProvided: false })
export class EnforcementResponseNoticePeerReviewStore extends SignalStore<EnforcementResponseNoticePeerReviewState> {
  constructor() {
    super(initialState);
  }
}
