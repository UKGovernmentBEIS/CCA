import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { GovukValidators } from '@netz/govuk-components';
import { AccountAddressFormModel, createAccountAddressForm, phoneInputValidators } from '@shared/components';
import { CCAGovukValidators, textFieldValidators } from '@shared/validators';

import { TargetUnitAccountContactDTO } from 'cca-api';

export type AdministrativeContactDetailsFormModel = {
  email: FormControl<TargetUnitAccountContactDTO['email']>;
  firstName: FormControl<TargetUnitAccountContactDTO['firstName']>;
  lastName: FormControl<TargetUnitAccountContactDTO['lastName']>;
  jobTitle: FormControl<TargetUnitAccountContactDTO['jobTitle']>;
  phoneNumber: FormControl<TargetUnitAccountContactDTO['phoneNumber']>;
  address: FormGroup<AccountAddressFormModel>;
  sameAddress?: FormControl<boolean[]>;
};

export function createAdministrativeForm(
  fb: FormBuilder,
  administrativeContactDetails: TargetUnitAccountContactDTO,
  sameAddress: boolean,
  isEditable?: boolean,
): FormGroup<AdministrativeContactDetailsFormModel> {
  const addressFormGroup = createAccountAddressForm(administrativeContactDetails?.address);

  if (sameAddress) {
    addressFormGroup.disable();
  }

  return fb.group<AdministrativeContactDetailsFormModel>(
    {
      firstName: fb.control(administrativeContactDetails?.firstName ?? null, textFieldValidators('first name')),
      lastName: fb.control(administrativeContactDetails?.lastName ?? null, textFieldValidators('last name')),
      email: fb.control(administrativeContactDetails?.email ?? null, [
        ...textFieldValidators('email address'),
        CCAGovukValidators.email(),
      ]),
      jobTitle: fb.control(
        administrativeContactDetails?.jobTitle ?? null,
        GovukValidators.maxLength(255, 'The job title should not be more than 255 characters'),
      ),
      phoneNumber: fb.control(administrativeContactDetails?.phoneNumber ?? null, phoneInputValidators),
      address: addressFormGroup,
      ...(isEditable ? {} : { sameAddress: fb.control([sameAddress]) }),
    },
    { updateOn: 'change' },
  );
}
