import { AbstractControl, FormArray, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

import { GovukValidators, MessageValidatorFn } from '@netz/govuk-components';

export function requiredFieldsValidator(): ValidatorFn {
  return GovukValidators.builder('You must fill all required values', (group: UntypedFormGroup) =>
    Object.keys(group.controls).find((key) => group.controls[key].hasError('required'))
      ? { emptyRequiredFields: true }
      : null,
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

export function futureDateValidator(errorMessage: string): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const date = new Date();
    return control.value && control.value > date ? { invalidDate: errorMessage } : null;
  };
}

export function dateRangeValidator(startDate: string | Date, endDate: string | Date, errorMsg: string): ValidatorFn {
  const min = new Date(startDate);
  const max = new Date(endDate);

  return (control: AbstractControl): ValidationErrors | null => {
    const value = new Date(control.value);

    return value < min || value > max ? { invalidDateRange: errorMsg } : null;
  };
}

export function uniqueFieldValidator(formArray: AbstractControl, formControlName: string): ValidationErrors | null {
  if (!(formArray instanceof FormArray)) return null;

  const names = formArray.controls
    .map((ctrl) => ctrl.get(formControlName)?.value?.trim()?.toLowerCase())
    .filter(Boolean);

  const hasDuplicate = names.some((name, index) => names.indexOf(name) !== index);

  return hasDuplicate
    ? { duplicateNames: 'The product name must be unique within the list. Please enter a different name.' }
    : null;
}

export function requireProductsValidator(predicate: () => boolean): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    if (!predicate()) {
      return null;
    }

    const value = control instanceof FormArray ? control.controls : control.value;
    const length = Array.isArray(value) ? value.length : 0;

    return length > 0 ? null : { productsRequired: 'There must be at least one product' };
  };
}

export const CCAGovukValidators = {
  required: (fieldName: string) => GovukValidators.required(`Enter the ${fieldName}`),
  maxLength: (fieldName: string, size = 255) =>
    GovukValidators.maxLength(size, `The ${fieldName} should not be more than ${size} characters`),
  email: () => GovukValidators.email('Enter an email address in the correct format, like name@example.com'),
  maxDecimalsWithMessage: (decimalDigits: number, message: string): MessageValidatorFn => {
    const regex = new RegExp(`^-?[0-9]+(\\.[0-9]{1,${decimalDigits}})?$`, '');
    return GovukValidators.pattern(regex, message);
  },
};
