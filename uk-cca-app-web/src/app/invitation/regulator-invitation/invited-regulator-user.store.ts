import { Injectable } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import { InvitedUserInfoDTO } from 'cca-api';

@Injectable()
export class InvitedRegulatorUserStore extends SignalStore<InvitedUserInfoDTO> {
  constructor() {
    super(null);
  }
}
