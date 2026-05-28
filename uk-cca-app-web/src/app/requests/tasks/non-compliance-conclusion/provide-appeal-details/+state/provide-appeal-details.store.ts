import { Injectable } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import { NonComplianceAppealDetails } from 'cca-api';

export interface ProvideAppealDetailsState {
  appealDetails: NonComplianceAppealDetails;
  attachments: Record<string, string>;
}

const initialState: ProvideAppealDetailsState = {
  appealDetails: null,
  attachments: {},
};

@Injectable()
export class ProvideAppealDetailsStore extends SignalStore<ProvideAppealDetailsState> {
  constructor() {
    super(initialState);
  }
}
