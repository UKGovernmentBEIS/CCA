import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { GovukValidators } from '@netz/govuk-components';
import { phoneInputValidators } from '@shared/components';
import { textFieldValidators } from '@shared/validators/validators';

import { CcaOperatorUserDetailsDTO } from 'cca-api';

import { ActiveOperatorStore } from '../active-operator.store';

export type PhoneNumberFormModel = {
  countryCode: FormControl<string>;
  number: FormControl<string>;
};

export type OperatorUserDetailsFormModel = {
  email: FormControl<CcaOperatorUserDetailsDTO['email']>;
  firstName: FormControl<CcaOperatorUserDetailsDTO['firstName']>;
  lastName: FormControl<CcaOperatorUserDetailsDTO['lastName']>;
  jobTitle?: FormControl<CcaOperatorUserDetailsDTO['jobTitle']>;
  phoneNumber?: FormGroup<PhoneNumberFormModel>;
  mobileNumber?: FormGroup<PhoneNumberFormModel>;
  contactType: FormControl<CcaOperatorUserDetailsDTO['contactType']>;
  organisationName?: FormControl<CcaOperatorUserDetailsDTO['organisationName']>;
};

export const TARGET_UNIT_OPERATOR_USER_DETAILS_FORM = new InjectionToken<OperatorUserDetailsFormModel>(
  'Target Unit Operator User Details Form',
);

export const OperatorUserDetailsFormProvider: Provider = {
  provide: TARGET_UNIT_OPERATOR_USER_DETAILS_FORM,
  deps: [FormBuilder, ActiveOperatorStore],
  useFactory: (fb: FormBuilder, store: ActiveOperatorStore) => {
    const operatorUserDetails = store.state.details;

    return fb.group(
      {
        firstName: fb.control(operatorUserDetails.firstName, textFieldValidators('first name')),
        lastName: fb.control(operatorUserDetails.lastName, textFieldValidators('last name')),
        jobTitle: fb.control(operatorUserDetails?.jobTitle),
        email: fb.control({ value: operatorUserDetails?.email, disabled: true }),
        contactType: fb.control({ value: operatorUserDetails?.contactType, disabled: !store.state.editable }, [
          GovukValidators.required('Choose your contact type'),
        ]),
        organisationName: fb.control(operatorUserDetails?.organisationName),
        phoneNumber: fb.control(operatorUserDetails?.phoneNumber, phoneInputValidators),
        mobileNumber: fb.control(operatorUserDetails?.mobileNumber, phoneInputValidators),
      },
      { updateOn: 'submit' },
    );
  },
};
