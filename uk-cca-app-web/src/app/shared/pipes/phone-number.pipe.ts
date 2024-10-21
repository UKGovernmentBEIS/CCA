import { Pipe, PipeTransform } from '@angular/core';

import { UKCountryCodes } from '@shared/types';
import { PhoneNumberUtil } from 'google-libphonenumber';

@Pipe({
  name: 'phoneNumber',
  standalone: true,
})
export class PhoneNumberPipe implements PipeTransform {
  transform(callingCode: string): string {
    if (callingCode == null) {
      return null;
    }

    const countryCode = PhoneNumberUtil.getInstance().getRegionCodeForCountryCode(Number(callingCode));
    return `${UKCountryCodes.GB === countryCode ? UKCountryCodes.UK : countryCode} (${callingCode})`;
  }
}
