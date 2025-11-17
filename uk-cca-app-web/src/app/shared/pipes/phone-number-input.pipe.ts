import { Pipe, PipeTransform } from '@angular/core';

import { PhoneNumberDTO } from 'cca-api';

export function transformPhoneInput(value: PhoneNumberDTO): string {
  if (!value || (!value.countryCode && !value.number)) {
    return '';
  }
  const countryCode = value.countryCode || '';
  const number = value.number || '';
  return `(+${countryCode})${number}`;
}
@Pipe({ name: 'phoneNumberInput', pure: true })
export class PhoneNumberInputPipe implements PipeTransform {
  transform = transformPhoneInput;
}
