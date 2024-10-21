import { UntypedFormGroup, ValidatorFn } from '@angular/forms';

import { GovukValidators, MessageValidatorFn } from '@netz/govuk-components';

export function requiredFieldsValidator(): ValidatorFn {
  return GovukValidators.builder('You must fill all required values', (group: UntypedFormGroup) =>
    Object.keys(group.controls).find((key) => group.controls[key].hasError('required'))
      ? { emptyRequiredFields: true }
      : null,
  );
}

export function atLeastOneRequiredValidator(message: string): ValidatorFn {
  return GovukValidators.builder(message, (group: UntypedFormGroup) =>
    Object.keys(group.controls).find((key) => !!group.controls[key].value) ? null : { atLeastOneRequired: true },
  );
}

/** Add **required** and **max length** validators to the given field name. */
export function textFieldValidators(fieldName: string, size = 255): MessageValidatorFn[] {
  return [
    GovukValidators.required(`Enter the ${fieldName}`),
    GovukValidators.maxLength(size, `The ${fieldName} should not be more than ${size} characters`),
  ];
}

export function facilityIDValidators(requiredMessage: string, patternMessage: string): MessageValidatorFn[] {
  return [
    GovukValidators.required(requiredMessage),
    GovukValidators.pattern(new RegExp(`^[A-Z0-9_]+-F[0-9]{5}$`), patternMessage),
  ];
}

export const CCAGovukValidators = {
  required: (fieldName: string) => GovukValidators.required(`Enter the ${fieldName}`),
  maxLength: (fieldName: string, size = 255) =>
    GovukValidators.maxLength(size, `The ${fieldName} should not be more than ${size} characters`),
  email: () => GovukValidators.email('Enter an email address in the correct format, like name@example.com'),
};
