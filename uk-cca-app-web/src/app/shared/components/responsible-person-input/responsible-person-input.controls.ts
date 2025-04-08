import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { GovukValidators } from '@netz/govuk-components';
import { FormControlConfig } from '@shared/types';
import { CCAGovukValidators, textFieldValidators } from '@shared/validators';

import { TargetUnitAccountContactDTO } from 'cca-api';

import { AccountAddressFormModel } from '../account-address-input/account-address-input-controls';
import { phoneInputValidators } from '../phone-input/phone-input.validators';

export type ResponsiblePersonFormModel = {
  email: FormControl<TargetUnitAccountContactDTO['email']>;
  firstName: FormControl<TargetUnitAccountContactDTO['firstName']>;
  lastName: FormControl<TargetUnitAccountContactDTO['lastName']>;
  jobTitle: FormControl<TargetUnitAccountContactDTO['jobTitle']>;
  phoneNumber: FormControl<TargetUnitAccountContactDTO['phoneNumber']>;
  address: FormGroup<AccountAddressFormModel>;
  sameAddress?: FormControl<boolean[]>;
};

export type ResponsiblePersonFormConfig = {
  email: FormControlConfig<TargetUnitAccountContactDTO['email']>;
  firstName: FormControlConfig<TargetUnitAccountContactDTO['firstName']>;
  lastName: FormControlConfig<TargetUnitAccountContactDTO['lastName']>;
  jobTitle: FormControlConfig<TargetUnitAccountContactDTO['jobTitle']>;
  phoneNumber: FormControlConfig<TargetUnitAccountContactDTO['phoneNumber']>;
  address: FormControlConfig<FormGroup<AccountAddressFormModel>>;
  sameAddress?: FormControlConfig<boolean[]>;
};

export function createResponsibleForm(
  fb: FormBuilder,
  formConfig: ResponsiblePersonFormConfig,
  sameAddressWithOperator: boolean,
  addressFormGroupDisabled?: boolean,
): FormGroup<ResponsiblePersonFormModel> {
  const addressFormGroup = formConfig.address.value;

  if (formConfig.address.disabled) {
    addressFormGroup.disable();
  }

  return fb.group<ResponsiblePersonFormModel>(
    {
      email: fb.control(
        {
          value: formConfig.email.value,
          disabled: formConfig.email.disabled,
        },
        [...textFieldValidators('email address'), GovukValidators.email()],
      ),
      firstName: fb.control(
        {
          value: formConfig.firstName.value,
          disabled: formConfig.firstName.disabled,
        },
        textFieldValidators('first name'),
      ),
      lastName: fb.control(
        {
          value: formConfig.lastName.value,
          disabled: formConfig.lastName.disabled,
        },
        textFieldValidators('last name'),
      ),
      jobTitle: fb.control(
        {
          value: formConfig.jobTitle.value,
          disabled: formConfig.jobTitle.disabled,
        },
        CCAGovukValidators.maxLength('job title'),
      ),
      phoneNumber: fb.control(
        {
          value: formConfig.phoneNumber.value,
          disabled: formConfig.phoneNumber.disabled,
        },
        phoneInputValidators,
      ),
      address: addressFormGroup,
      ...(addressFormGroupDisabled
        ? {}
        : { sameAddress: fb.control(formConfig.sameAddress?.value || [sameAddressWithOperator]) }),
    },
    { updateOn: 'change' },
  );
}
