import { FactoryProvider, InjectionToken } from '@angular/core';
import {
  AbstractControl,
  AsyncValidatorFn,
  FormBuilder,
  FormControl,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
} from '@angular/forms';

import { map, switchMap, take, timer } from 'rxjs';

import { GovukValidators, MessageValidationErrors } from '@netz/govuk-components';
import { zxcvbn } from '@zxcvbn-ts/core';

import { PasswordValidationResponseDTO, ValidatePasswordService } from 'cca-api';

type PasswordFormModel = {
  email: FormControl<string | null>;
  password: FormControl<string | null>;
  validatePassword: FormControl<string | null>;
};

export const PASSWORD_FORM = new InjectionToken<FormGroup<PasswordFormModel>>('Password form');

const apiPasswordErrorMap: Record<string, string> = {
  INVALID_MIN_LENGTH: 'Password must be 12 characters or more',
  INVALID_MAX_LENGTH: 'Password must be 127 characters or less',
  BLACKLISTED_PATTERN: 'Enter a password that does not contain words related to the service or your role',
  PWNED: 'Password has been blacklisted. Select another password',
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

const strongPasswordValidator: ValidatorFn = (control: AbstractControl): MessageValidationErrors | null => {
  const strength = zxcvbn(control.value ?? '').score;
  return strength > 2 ? null : { weakPassword: 'Enter a strong password' };
};

const passwordsMatch = (group: FormGroup<PasswordFormModel>): ValidationErrors | null =>
  group.controls.password.value === group.controls.validatePassword.value ? null : { notEquivalent: true };

export const passwordFormFactory: FactoryProvider = {
  provide: PASSWORD_FORM,
  deps: [FormBuilder, ValidatePasswordService],
  useFactory: (fb: FormBuilder, validatePasswordService: ValidatePasswordService) => {
    const apiPasswordValidator: AsyncValidatorFn = (control) =>
      timer(300).pipe(
        switchMap(() =>
          validatePasswordService.validatePassword({
            password: control.value,
          }),
        ),
        map((res) => apiPasswordErrorMapper(res)),
        take(1),
      );

    return fb.group(
      {
        email: fb.control({ value: null, disabled: true }),
        password: fb.control(null, {
          validators: [GovukValidators.required('Please enter your password'), strongPasswordValidator],
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
