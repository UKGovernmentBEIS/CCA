import { inject } from '@angular/core';
import { FormBuilder, FormControl } from '@angular/forms';

import { GovukValidators } from '@netz/govuk-components';

import { FacilityAddressDTO } from 'cca-api';

export type FacilityAddressFormModel = {
  line1: FormControl<FacilityAddressDTO['line1']>;
  line2?: FormControl<FacilityAddressDTO['line2']>;
  city: FormControl<FacilityAddressDTO['city']>;
  county?: FormControl<FacilityAddressDTO['county']>;
  postcode: FormControl<FacilityAddressDTO['postcode']>;
  country: FormControl<FacilityAddressDTO['country']>;
};

export function createFacilityAddressForm(address: FacilityAddressDTO) {
  const fb = inject(FormBuilder);

  return fb.group<FacilityAddressFormModel>({
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
