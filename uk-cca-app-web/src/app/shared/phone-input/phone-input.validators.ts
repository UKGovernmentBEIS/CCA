import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

import { PhoneNumberUtil } from 'google-libphonenumber';

import { GovukValidators, MessageValidatorFn } from 'govuk-components';

const phoneNumberSizeValidator = (): ValidatorFn => {
  return (control: AbstractControl): { [key: string]: string } | null => {
    return control.value?.number?.length > 255
      ? { invalidSize: `Your phone number should not be larger than 255 characters` }
      : null;
  };
};

const phoneNumberOtherValidator = (): ValidatorFn => {
  return (control: AbstractControl): ValidationErrors | null => {
    const phoneNumberUtil = PhoneNumberUtil.getInstance();

    // If the fields are not filled, return null
    if (!control.value?.countryCode || !control.value?.number) {
      return null;
    }

    const countryCode = control.value.countryCode;
    const phone = control.value.number;

    // Regex to check for valid phone number characters
    const phoneNumberRegex = new RegExp('^[\\d \\-()]*$');

    // Check for invalid characters
    const isPhoneNumber = phoneNumberRegex.test(phone);
    if (!isPhoneNumber) {
      return { invalidChars: 'The phone number contains invalid characters' };
    }

    let validNumber = false;
    let validationResult;
    try {
      const regionCode = phoneNumberUtil.getRegionCodeForCountryCode(countryCode);
      const phoneNumber = phoneNumberUtil.parseAndKeepRawInput(phone, regionCode);
      validNumber = phoneNumberUtil.isValidNumber(phoneNumber);
      validationResult = phoneNumberUtil.isPossibleNumberWithReason(phoneNumber);
    } catch (e) {
      return { invalidPhone: 'Your phone number is not valid' };
    }
    if (!validNumber) {
      switch (validationResult) {
        case PhoneNumberUtil.ValidationResult.TOO_SHORT:
          return { tooShort: 'The phone number is too short for your country code' };
        case PhoneNumberUtil.ValidationResult.TOO_LONG:
          return { tooLong: 'The phone number is too long for your country code' };
        case PhoneNumberUtil.ValidationResult.INVALID_LENGTH:
          return { invalidLength: 'The phone number length is invalid' };
        default:
          return { invalidPhone: 'Your phone number is not valid' };
      }
    }
    return null;
  };
};

export const phoneInputValidators: MessageValidatorFn[] = [
  GovukValidators.incomplete('Enter both country code and number'),
  phoneNumberSizeValidator(),
  phoneNumberOtherValidator(),
];
