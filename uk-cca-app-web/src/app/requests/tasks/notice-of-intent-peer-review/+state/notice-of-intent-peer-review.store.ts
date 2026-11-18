import { Injectable } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import { CcaPeerReviewDecision } from 'cca-api';

export interface NoticeOfIntentPeerReviewState {
  decision: CcaPeerReviewDecision;
  attachments: Record<string, string>;
}

const initialState: NoticeOfIntentPeerReviewState = {
  decision: null,
  attachments: {},
};

@Injectable()
export class NoticeOfIntentPeerReviewStore extends SignalStore<NoticeOfIntentPeerReviewState> {
  constructor() {
    super(initialState);
  }
}
