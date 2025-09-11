import { FactoryProvider, InjectionToken } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { GovukValidators } from '@netz/govuk-components';

import { PasswordValidators } from './password.service';

type PasswordFormModel = {
  email: FormControl<string | null>;
  password: FormControl<string>;
  validatePassword: FormControl<string>;
};

export const PASSWORD_FORM = new InjectionToken<FormGroup<PasswordFormModel>>('Password form');

export const passwordFormFactory: FactoryProvider = {
  provide: PASSWORD_FORM,
  useFactory: (fb: FormBuilder) =>
    fb.group(
      {
        email: fb.control({ value: null, disabled: true }),
        password: fb.control(null, {
          validators: [
            GovukValidators.required('Please enter your password'),
            GovukValidators.minLength(12, 'Password must be 12 characters or more'),
            PasswordValidators.strong,
          ],
          asyncValidators: PasswordValidators.blacklisted,
        }),
        validatePassword: fb.control(null, { validators: GovukValidators.required('Re-enter your password') }),
      },
      {
        validators: GovukValidators.builder(
          'Password and re-typed password do not match. Please enter both passwords again',
          (group: FormGroup) => {
            const password = group.get('password');
            const validatePassword = group.get('validatePassword');
            return password.value === validatePassword.value ? null : { notEquivalent: true };
          },
        ),
      },
    ),
  deps: [FormBuilder],
};
