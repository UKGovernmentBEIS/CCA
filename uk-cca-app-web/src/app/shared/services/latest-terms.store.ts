import { Service } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import { TermsDTO } from 'cca-api';

export const initialState: TermsDTO = {
  url: null,
  version: null,
};

@Service()
export class LatestTermsStore extends SignalStore<TermsDTO> {
  constructor() {
    super(initialState);
  }

  setLatestTerms(latestTerms: TermsDTO) {
    this.setState(latestTerms);
  }
}
