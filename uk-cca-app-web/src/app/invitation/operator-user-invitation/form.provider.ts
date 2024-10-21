import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { GovukValidators } from '@netz/govuk-components';
import { phoneInputValidators } from '@shared/components';

import { OperatorUserInvitationStore } from './store';

export type PhoneNumberFormModel = {
  countryCode: FormControl<string>;
  number: FormControl<string>;
};

export type OperatorUserInviteFormModel = {
  email: FormControl<string>;
  firstName: FormControl<string>;
  lastName: FormControl<string>;
  jobTitle?: FormControl<string>;
  phoneNumber?: FormGroup<PhoneNumberFormModel>;
  mobileNumber?: FormGroup<PhoneNumberFormModel>;
  contactType: FormControl<'OPERATOR' | 'CONSULTANT'>;
  organisationName?: FormControl<string>;
};

export const OPERATOR_USER_INVITATION_FORM = new InjectionToken<OperatorUserInviteFormModel>(
  'Operator User Invitation Form',
);

export const OperatorUserInvitationFormProvider: Provider = {
  provide: OPERATOR_USER_INVITATION_FORM,
  deps: [FormBuilder, OperatorUserInvitationStore],
  useFactory: (fb: FormBuilder, store: OperatorUserInvitationStore) => {
    const storeUser = store.state;

    return fb.group(
      {
        firstName: fb.control(storeUser.firstName, [GovukValidators.required('Enter your first name')]),
        lastName: fb.control(storeUser.lastName, [GovukValidators.required('Enter your last name')]),
        jobTitle: fb.control(storeUser.jobTitle),
        email: fb.control({ value: storeUser.email, disabled: true }),
        contactType: fb.control({ value: storeUser.contactType, disabled: true }),
        organisationName: fb.control(storeUser.organisationName),
        phoneNumber: fb.control(storeUser.phoneNumber, phoneInputValidators),
        mobileNumber: fb.control(storeUser.mobileNumber, phoneInputValidators),
        roleCode: fb.control({ value: storeUser.roleCode, disabled: true }),
      },
      { updateOn: 'submit' },
    );
  },
};
