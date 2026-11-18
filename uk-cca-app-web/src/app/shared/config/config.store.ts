import { Injectable } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import { ConfigState, initialState } from './config.state';

@Injectable({ providedIn: 'root' })
export class ConfigStore extends SignalStore<ConfigState> {
  constructor() {
    super(initialState);
  }
}
