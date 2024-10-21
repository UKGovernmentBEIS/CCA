import { Injectable } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import { PhoneNumberDTO, SectorInvitedUserInfoDTO } from 'cca-api';

export type InvitedSectorUserExtended = SectorInvitedUserInfoDTO & {
  emailToken?: string;
  jobTitle?: string;
  organisationName?: string;
  mobileNumber?: PhoneNumberDTO;
  phoneNumber?: PhoneNumberDTO;
  password?: string;
};

const INITIAL_STATE: InvitedSectorUserExtended = {
  firstName: null,
  lastName: null,
  jobTitle: null,
  contactType: null,
  roleCode: null,
  email: null,
  emailToken: null,
  invitationStatus: null,
  organisationName: null,
  phoneNumber: null,
  mobileNumber: null,
};

@Injectable()
export class SectorUserInvitationStore extends SignalStore<InvitedSectorUserExtended> {
  constructor() {
    super(INITIAL_STATE);
  }
}
