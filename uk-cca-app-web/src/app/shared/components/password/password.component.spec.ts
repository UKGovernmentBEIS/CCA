import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { Component, Inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { ErrorSummaryComponent } from '@netz/govuk-components';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';
import { type UserEvent as UE } from '@testing-library/user-event';
import UserEvent from '@testing-library/user-event';
import { provideZxvbnServiceForPSM } from 'angular-password-strength-meter/zxcvbn';

import { PasswordComponent } from './password.component';
import { PasswordValidators } from './password.service';
import { PASSWORD_FORM, passwordFormFactory } from './password-form.factory';
@Component({
  template: `
    @if (form.invalid && form.touched) {
      <govuk-error-summary [form]="form" />
    }
    <form [formGroup]="form">
      <cca-password></cca-password>
      <button type="submit">Submit</button>
    </form>
  `,
  imports: [PasswordComponent, ReactiveFormsModule, ErrorSummaryComponent],
  providers: [passwordFormFactory],
})
export class TestComponent {
  constructor(@Inject(PASSWORD_FORM) readonly form: FormGroup) {}
}

describe('PasswordComponent', () => {
  beforeEach(async () => {
    PasswordValidators.blacklisted = jest
      .fn()
      .mockReturnValue(of({ blacklisted: 'Password has been blacklisted. Please select another password' }));

    await render(TestComponent, {
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting(), provideZxvbnServiceForPSM()],
    });
  });

  it('should create', () => {
    expect(screen.getByTestId('password-form')).toBeVisible();
  });

  it('should require the password', async () => {
    const user = UserEvent.setup();
    await user.click(screen.getByText('Submit'));
    expect(screen.getAllByText('Please enter your password')).toHaveLength(1);
    expect(screen.getAllByText('Enter a strong password')).toHaveLength(1);
    expect(screen.getAllByText('Re-enter your password')).toHaveLength(2);
  });

  it('should require more than 12 characters for the password', async () => {
    const user = UserEvent.setup();
    await user.type(document.getElementById('password'), 'test');
    await insertValidatePassword(user, 'test');
    await user.type(document.getElementById('validatePassword'), 'test');
    await user.click(screen.getByText('Submit'));

    expect(screen.getAllByText('Password must be 12 characters or more')).toHaveLength(2);
  });

  it('should not accept weak password', async () => {
    const user = UserEvent.setup();
    await user.type(document.getElementById('password'), '12345678');
    await insertValidatePassword(user, '12345678');
    await user.click(screen.getByText('Submit'));
    expect(screen.getAllByText('Enter a strong password')).toHaveLength(2);
  });

  it('should require the passwords to match', async () => {
    const user = UserEvent.setup();
    await user.type(document.getElementById('password'), '12345678');
    await insertValidatePassword(user, '123456789');
    await user.click(screen.getByText('Submit'));
    expect(
      screen.getByText('Password and re-typed password do not match. Please enter both passwords again'),
    ).toBeVisible();
  });

  it('should not accept a blacklisted password', async () => {
    const user = UserEvent.setup();
    await user.type(document.getElementById('password'), 'password123123');
    await insertValidatePassword(user, 'password123123');
    await user.click(screen.getByText('Submit'));
    expect(screen.getAllByText('Password has been blacklisted. Please select another password')).toHaveLength(2);
  });
});

async function insertValidatePassword(user: UE, input: string) {
  await user.click(document.getElementById('validatePassword'));
  await user.type(document.getElementById('validatePassword'), input);
}
