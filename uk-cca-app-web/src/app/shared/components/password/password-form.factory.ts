import { FactoryProvider, InjectionToken } from '@angular/core';
import { AsyncValidatorFn, FormBuilder, FormControl, FormGroup, ValidationErrors } from '@angular/forms';

import { map, switchMap, take, timer } from 'rxjs';

import { GovukValidators } from '@netz/govuk-components';

import { PasswordValidationErrorDTO, PasswordValidationResponseDTO, ValidatePasswordService } from 'cca-api';

type PasswordFormModel = {
  email: FormControl<string | null>;
  password: FormControl<string | null>;
  validatePassword: FormControl<string | null>;
};

export const PASSWORD_FORM = new InjectionToken<FormGroup<PasswordFormModel>>('Password form');

const apiPasswordErrorMap: Partial<Record<NonNullable<PasswordValidationErrorDTO['code']>, string>> = {
  INVALID_MIN_LENGTH: 'Password must be 12 characters or more',
  INVALID_MAX_LENGTH: 'Password must be 127 characters or less',
  WEAK: 'For security reasons, you must enter a different password that is hard to guess',
  PWNED_SERVICE_UNAVAILABLE: 'Password check service is temporarily unavailable. Please try again later',
};

const apiPasswordErrorMapper = (response: PasswordValidationResponseDTO) => {
  if (response?.errors?.length) {
    return response.errors.reduce(
      (acc, error) => {
        acc[error.code] = apiPasswordErrorMap?.[error.code] ?? error.message;
        return acc;
      },
      {} as Record<string, string>,
    );
  }
  return null;
};

const passwordsMatch = (group: FormGroup<PasswordFormModel>): ValidationErrors | null =>
  group.controls.password.value === group.controls.validatePassword.value ? null : { notEquivalent: true };

export const passwordFormFactory: FactoryProvider = {
  provide: PASSWORD_FORM,
  deps: [FormBuilder, ValidatePasswordService],
  useFactory: (fb: FormBuilder, validatePasswordService: ValidatePasswordService) => {
    const apiPasswordValidator: AsyncValidatorFn = (control) =>
      timer(300).pipe(
        switchMap(() => validatePasswordService.validatePassword({ password: control.value })),
        map((res) => apiPasswordErrorMapper(res)),
        take(1),
      );

    return fb.group(
      {
        email: fb.control({ value: null, disabled: true }),
        password: fb.control(null, {
          validators: [GovukValidators.required('Please enter your password')],
          asyncValidators: apiPasswordValidator,
        }),
        validatePassword: fb.control(null, { validators: GovukValidators.required('Re-enter your password') }),
      },
      {
        validators: GovukValidators.builder(
          'Password and re-typed password do not match. Please enter both passwords again',
          (control) => passwordsMatch(control as FormGroup<PasswordFormModel>),
        ),
      },
    );
  },
};
