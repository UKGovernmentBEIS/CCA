import { Service } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import { InvitedUserInfoDTO } from 'cca-api';

@Service({ autoProvided: false })
export class InvitedRegulatorUserStore extends SignalStore<InvitedUserInfoDTO> {
  constructor() {
    super(null);
  }
}
