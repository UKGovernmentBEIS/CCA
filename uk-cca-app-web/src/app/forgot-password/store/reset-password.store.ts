import { Injectable } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import { initialState, ResetPasswordState } from './reset-password.state';

@Injectable({ providedIn: 'root' })
export class ResetPasswordStore extends SignalStore<ResetPasswordState> {
  constructor() {
    super(initialState);
  }
}
