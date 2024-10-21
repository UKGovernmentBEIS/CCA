import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { phoneInputValidators } from '@shared/components';
import { CCAGovukValidators, textFieldValidators } from '@shared/validators';

import { SectorUserInvitationStore } from './sector-user-invitation.store';

export type PhoneNumberFormModel = {
  countryCode: FormControl<string>;
  number: FormControl<string>;
};

export type SectorUserInviteFormModel = {
  email: FormControl<string>;
  firstName: FormControl<string>;
  lastName: FormControl<string>;
  jobTitle?: FormControl<string>;
  phoneNumber?: FormGroup<PhoneNumberFormModel>;
  mobileNumber?: FormGroup<PhoneNumberFormModel>;
  contactType: FormControl<'SECTOR_ASSOCIATION' | 'CONSULTANT'>;
  organisationName?: FormControl<string>;
};

export const SECTOR_USER_INVITATION_FORM = new InjectionToken<SectorUserInviteFormModel>('Sector User Invitation Form');

export const SectorUserInvitationFormProvider: Provider = {
  provide: SECTOR_USER_INVITATION_FORM,
  deps: [FormBuilder, SectorUserInvitationStore],
  useFactory: (fb: FormBuilder, store: SectorUserInvitationStore) => {
    const storeUser = store.state;

    return fb.group({
      firstName: fb.control(storeUser.firstName, textFieldValidators('first name')),
      lastName: fb.control(storeUser.lastName, textFieldValidators('last name')),
      jobTitle: fb.control(storeUser.jobTitle, CCAGovukValidators.maxLength('Job title')),
      email: fb.control({ value: storeUser.email, disabled: true }),
      contactType: fb.control({ value: storeUser.contactType, disabled: true }),
      organisationName: fb.control(storeUser.organisationName, CCAGovukValidators.maxLength('Organization name')),
      phoneNumber: fb.control(storeUser.phoneNumber, phoneInputValidators),
      mobileNumber: fb.control(storeUser.mobileNumber, phoneInputValidators),
    });
  },
};
