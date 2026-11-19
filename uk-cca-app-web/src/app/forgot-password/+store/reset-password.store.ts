import { Service } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import { initialState, ResetPasswordState } from './reset-password.state';

@Service()
export class ResetPasswordStore extends SignalStore<ResetPasswordState> {
  constructor() {
    super(initialState);
  }
}
