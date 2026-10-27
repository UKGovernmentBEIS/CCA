import { Service } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import { ConfigState, initialState } from './config.state';

@Service()
export class ConfigStore extends SignalStore<ConfigState> {
  constructor() {
    super(initialState);
  }
}
