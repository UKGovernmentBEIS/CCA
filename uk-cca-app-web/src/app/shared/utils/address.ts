import { AccountAddressDTO } from 'cca-api';

export function getAddressAsArray(address: AccountAddressDTO): string[] {
  return [address?.line1, address?.line2, address?.city, address?.county, address?.postcode, address?.country].filter(
    (v) => !!v,
  );
}
