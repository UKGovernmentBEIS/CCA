import { UntypedFormControl } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { CountyAddressDTO } from 'cca-api';

export function createCountyAddressControl(
  address: CountyAddressDTO,
): Record<keyof CountyAddressDTO, UntypedFormControl> {
  return {
    line1: new UntypedFormControl(address?.line1, [
      GovukValidators.required('Enter an address'),
      GovukValidators.maxLength(255, 'The address should not be more than 255 characters'),
    ]),
    line2: new UntypedFormControl(
      address?.line2,
      GovukValidators.maxLength(255, 'The address should not be more than 255 characters'),
    ),
    city: new UntypedFormControl(address?.city, [
      GovukValidators.required('Enter a town or city'),
      GovukValidators.maxLength(255, 'The city should not be more than 255 characters'),
    ]),
    postcode: new UntypedFormControl(address?.postcode, [
      GovukValidators.required('Enter a postcode'),
      GovukValidators.maxLength(64, 'The postcode should not be more than 64 characters'),
    ]),
    county: new UntypedFormControl(address?.county, GovukValidators.required('Enter a county')),
  };
}
