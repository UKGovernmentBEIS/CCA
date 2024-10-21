import { UKCountryCodes } from '@shared/types';
import { PhoneNumberUtil } from 'google-libphonenumber';

import { PhoneNumberDTO } from 'cca-api';

export function transformPhoneNumber(phoneNumber: PhoneNumberDTO | null): string {
  if (!phoneNumber?.countryCode) return '';
  const countryCode = PhoneNumberUtil.getInstance().getRegionCodeForCountryCode(Number(phoneNumber.countryCode));
  const countryCodePrefix = `${UKCountryCodes.GB === countryCode ? UKCountryCodes.UK : countryCode} (${phoneNumber.countryCode})`;
  return `${countryCodePrefix} ${phoneNumber.number}`;
}
