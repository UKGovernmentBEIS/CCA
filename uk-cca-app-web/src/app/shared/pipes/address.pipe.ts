import { Pipe, PipeTransform } from '@angular/core';

import { AccountAddressDTO } from 'cca-api';

import { Country } from '../types/country';
import { logger } from '../utils/logger';

@Pipe({ name: 'address' })
export class AddressPipe implements PipeTransform {
  transform = transformAddress;
}

function getAddressAsArray(address: AccountAddressDTO): string[] {
  return [address?.line1, address?.line2, address?.city, address?.county, address?.postcode, address?.country].filter(
    Boolean,
  );
}

export function transformAddress(address: AccountAddressDTO, countries: Country[]) {
  if (!countries) {
    logger.error('The countries were not loaded from the service.');
    return;
  }

  if (!address) return getAddressAsArray(address);

  const country = countries.find((c) => c.code === address.country);

  return getAddressAsArray({
    ...address,
    country: country ? country.name : address.country,
  });
}
