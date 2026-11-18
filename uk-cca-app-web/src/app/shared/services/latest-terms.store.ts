import { Injectable } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import { TermsDTO } from 'cca-api';

export const initialState: TermsDTO = {
  url: null,
  version: null,
};

@Injectable({ providedIn: 'root' })
export class LatestTermsStore extends SignalStore<TermsDTO> {
  constructor() {
    super(initialState);
  }

  setLatestTerms(latestTerms: TermsDTO) {
    this.setState(latestTerms);
  }
}
