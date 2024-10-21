import { inject } from '@angular/core';
import { FormBuilder, FormControl } from '@angular/forms';

import { GovukValidators } from '@netz/govuk-components';

import { AddressDTO } from 'cca-api';

export type AddressFormModel = {
  line1: FormControl<AddressDTO['line1']>;
  line2?: FormControl<AddressDTO['line2']>;
  city: FormControl<AddressDTO['city']>;
  county?: FormControl<AddressDTO['county']>;
  postcode: FormControl<AddressDTO['postcode']>;
};

export function createCountyAddressControl(address: AddressDTO) {
  const fb = inject(FormBuilder);

  return fb.group<AddressFormModel>({
    line1: fb.control(address?.line1, [
      GovukValidators.required('Enter address line 1, typically the building and street'),
      GovukValidators.maxLength(255, 'The address should not be more than 255 characters'),
    ]),
    line2: fb.control(
      address?.line2,
      GovukValidators.maxLength(255, 'The address should not be more than 255 characters'),
    ),
    city: fb.control(address?.city, [
      GovukValidators.required('Enter a town or city'),
      GovukValidators.maxLength(255, 'The city should not be more than 255 characters'),
    ]),
    postcode: fb.control(address?.postcode, [
      GovukValidators.required('Enter a postcode'),
      GovukValidators.maxLength(64, 'The postcode should not be more than 64 characters'),
    ]),
    county: fb.control(
      address?.county,
      GovukValidators.maxLength(255, 'The county should not be more than 255 characters'),
    ),
  });
}
