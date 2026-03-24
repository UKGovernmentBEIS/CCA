import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { Component, inject } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { ErrorSummaryComponent } from '@netz/govuk-components';
import { click, getAllByText, getByTestId, getByText, type } from '@testing';

import { PasswordComponent } from './password.component';
import { PasswordValidators } from './password.service';
import { PASSWORD_FORM, passwordFormFactory } from './password-form.factory';
@Component({
  template: `
    @if (form.invalid && form.touched) {
      <govuk-error-summary [form]="form" />
    }
    <form [formGroup]="form" (ngSubmit)="onSubmit()">
      <cca-password></cca-password>
      <button type="submit">Submit</button>
    </form>
  `,
  imports: [PasswordComponent, ReactiveFormsModule, ErrorSummaryComponent],
  providers: [passwordFormFactory],
})
export class TestComponent {
  protected readonly form = inject<FormGroup>(PASSWORD_FORM);

  onSubmit() {
    this.form.markAllAsTouched();
  }
}

describe('PasswordComponent', () => {
  let fixture: ComponentFixture<TestComponent>;

  beforeEach(async () => {
    PasswordValidators.blacklisted = jest
      .fn()
      .mockReturnValue(of({ blacklisted: 'Password has been blacklisted. Please select another password' }));

    await TestBed.configureTestingModule({
      imports: [TestComponent],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(getByTestId('password-form')).toBeTruthy();
  });

  it('should require the password', async () => {
    click(getByText('Submit'));
    fixture.detectChanges();
    await fixture.whenStable();

    // Errors appear in error summary + inline for each field
    expect(getAllByText('Please enter your password').length).toBeGreaterThanOrEqual(1);
    expect(getAllByText('Enter a strong password').length).toBeGreaterThanOrEqual(1);
    expect(getAllByText('Re-enter your password').length).toBeGreaterThanOrEqual(2);
  });

  it('should require more than 12 characters for the password', async () => {
    type(document.getElementById('password') as HTMLInputElement, 'test');
    insertValidatePassword('test');
    type(document.getElementById('validatePassword') as HTMLInputElement, 'test');
    click(getByText('Submit'));
    fixture.detectChanges();
    await fixture.whenStable();

    // Error appears in error summary + inline for both password fields
    expect(getAllByText('Password must be 12 characters or more').length).toBeGreaterThanOrEqual(2);
  });

  it('should not accept weak password', async () => {
    type(document.getElementById('password') as HTMLInputElement, '12345678');
    insertValidatePassword('12345678');
    click(getByText('Submit'));
    fixture.detectChanges();
    await fixture.whenStable();

    // Error appears in error summary + inline for both password fields
    expect(getAllByText('Enter a strong password').length).toBeGreaterThanOrEqual(2);
  });

  it('should require the passwords to match', async () => {
    type(document.getElementById('password') as HTMLInputElement, '12345678');
    insertValidatePassword('123456789');
    click(getByText('Submit'));
    fixture.detectChanges();
    await fixture.whenStable();

    expect(getByText('Password and re-typed password do not match. Please enter both passwords again')).toBeTruthy();
  });

  it('should not accept a blacklisted password', async () => {
    type(document.getElementById('password') as HTMLInputElement, 'password123123');
    insertValidatePassword('password123123');
    click(getByText('Submit'));
    fixture.detectChanges();
    await fixture.whenStable();

    // Error appears in error summary + inline for both password fields = 4 total
    expect(getAllByText('Password has been blacklisted. Please select another password').length).toBeGreaterThanOrEqual(
      2,
    );
  });
});

function insertValidatePassword(input: string) {
  click(document.getElementById('validatePassword') as HTMLElement);
  type(document.getElementById('validatePassword') as HTMLInputElement, input);
}
