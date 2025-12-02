import { AccountAddressDTO } from 'cca-api';

export const isAddressCompleted = (address: AccountAddressDTO): boolean =>
  !!address?.city && !!address?.country && !!address?.line1 && !!address?.postcode;
