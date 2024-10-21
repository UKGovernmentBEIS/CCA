import { inject } from '@angular/core';
import { FormBuilder, FormControl } from '@angular/forms';

import { GovukValidators } from '@netz/govuk-components';

import { AccountAddressDTO } from 'cca-api';

export type AccountAddressFormModel = {
  line1: FormControl<AccountAddressDTO['line1']>;
  line2?: FormControl<AccountAddressDTO['line2']>;
  city: FormControl<AccountAddressDTO['city']>;
  county?: FormControl<AccountAddressDTO['county']>;
  postcode: FormControl<AccountAddressDTO['postcode']>;
  country: FormControl<AccountAddressDTO['country']>;
};

export function createAccountAddressForm(address: AccountAddressDTO) {
  const fb = inject(FormBuilder);
  return fb.group<AccountAddressFormModel>({
    line1: fb.control(address?.line1 ?? null, [
      GovukValidators.required('Enter address line 1, typically the building and street'),
      GovukValidators.maxLength(255, 'The address should not be more than 255 characters'),
    ]),
    line2: fb.control(
      address?.line2 ?? null,
      GovukValidators.maxLength(255, 'The address should not be more than 255 characters'),
    ),
    city: fb.control(address?.city ?? null, [
      GovukValidators.required('Enter a town or city'),
      GovukValidators.maxLength(255, 'The city should not be more than 255 characters'),
    ]),
    postcode: fb.control(address?.postcode ?? null, [
      GovukValidators.required('Enter a postcode'),
      GovukValidators.maxLength(64, 'The postcode should not be more than 64 characters'),
    ]),
    county: fb.control(
      address?.county ?? null,
      GovukValidators.maxLength(255, 'The county should not be more than 255 characters'),
    ),
    country: fb.control(address?.country, GovukValidators.required('Select a country')),
  });
}
