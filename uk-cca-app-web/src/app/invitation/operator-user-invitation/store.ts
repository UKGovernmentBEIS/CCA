import { Injectable } from '@angular/core';

import { SignalStore } from '@netz/common/store';

import { PhoneNumberDTO } from 'cca-api';

export type InvitedOperatorUserExtended = {
  email: string;
  firstName: string;
  lastName: string;
  roleCode?: string;
  contactType: string;
  emailToken?: string;
  jobTitle?: string;
  accountName?: string;
  organisationName?: string;
  mobileNumber?: PhoneNumberDTO;
  phoneNumber?: PhoneNumberDTO;
  password?: string;
};

const INITIAL_STATE: InvitedOperatorUserExtended = {
  firstName: null,
  lastName: null,
  jobTitle: null,
  contactType: null,
  roleCode: null,
  email: null,
  emailToken: null,
  organisationName: null,
  phoneNumber: null,
  mobileNumber: null,
  accountName: null,
};

@Injectable()
export class OperatorUserInvitationStore extends SignalStore<InvitedOperatorUserExtended> {
  constructor() {
    super(INITIAL_STATE);
  }
}
