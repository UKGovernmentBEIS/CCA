import { provideHttpClient, withXhr } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { Component, inject } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { ErrorSummaryComponent } from '@netz/govuk-components';
import { click, getAllByText, getByTestId, getByText, type } from '@testing';

import { ValidatePasswordService } from 'cca-api';

import { PasswordComponent } from './password.component';
import { PASSWORD_FORM, passwordFormFactory } from './password-form.factory';
@Component({
  template: `
    @if (form.invalid && form.touched) {
      <govuk-error-summary [form]="form" />
    }
    <form [formGroup]="form" (ngSubmit)="onSubmit()">
      <cca-password />
      <button type="submit">Submit</button>
    </form>
  `,
  providers: [passwordFormFactory],
  imports: [PasswordComponent, ReactiveFormsModule, ErrorSummaryComponent],
})
export class TestComponent {
  protected readonly form = inject<FormGroup>(PASSWORD_FORM);

  onSubmit() {
    this.form.markAllAsTouched();
  }
}

const mockValidatePasswordService = { validatePassword: vi.fn().mockReturnValue(of(null)) };

describe('PasswordComponent', () => {
  let fixture: ComponentFixture<TestComponent>;

  beforeEach(async () => {
    mockValidatePasswordService.validatePassword.mockReturnValue(of(null));

    await TestBed.configureTestingModule({
      imports: [TestComponent],
      providers: [
        provideRouter([]),
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        { provide: ValidatePasswordService, useValue: mockValidatePasswordService },
      ],
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
    expect(getAllByText('Re-enter your password').length).toBeGreaterThanOrEqual(2);
  });

  it('should not accept a weak password', async () => {
    mockValidatePasswordService.validatePassword.mockReturnValue(
      of({ errors: [{ code: 'WEAK', message: 'Password is too weak' }] }),
    );

    type(document.getElementById('password') as HTMLInputElement, 'password123123');
    insertValidatePassword('password123123');
    click(getByText('Submit'));
    fixture.detectChanges();
    await new Promise((r) => setTimeout(r, 400));
    fixture.detectChanges();

    expect(
      getAllByText('For security reasons, you must enter a different password that is hard to guess').length,
    ).toBeGreaterThanOrEqual(2);
  });

  it('should not run a client-side strength check', async () => {
    type(document.getElementById('password') as HTMLInputElement, 'aaaaaaaaaaaa');
    insertValidatePassword('aaaaaaaaaaaa');
    click(getByText('Submit'));
    fixture.detectChanges();
    await new Promise((r) => setTimeout(r, 400));
    fixture.detectChanges();

    expect(mockValidatePasswordService.validatePassword).toHaveBeenCalledWith({ password: 'aaaaaaaaaaaa' });
    expect(document.querySelector('.govuk-error-summary')).toBeNull();
  });

  it('should require the passwords to match', async () => {
    type(document.getElementById('password') as HTMLInputElement, '12345678');
    insertValidatePassword('123456789');
    click(getByText('Submit'));
    fixture.detectChanges();
    await fixture.whenStable();

    expect(getByText('Password and re-typed password do not match. Please enter both passwords again')).toBeTruthy();
  });

  it('should reject passwords below the minimum length', async () => {
    mockValidatePasswordService.validatePassword.mockReturnValue(
      of({ errors: [{ code: 'INVALID_MIN_LENGTH', message: 'Password must be 12 characters or more' }] }),
    );

    type(document.getElementById('password') as HTMLInputElement, 'SomeStrongP@ssw0rd');
    insertValidatePassword('SomeStrongP@ssw0rd');
    click(getByText('Submit'));
    fixture.detectChanges();
    await new Promise((r) => setTimeout(r, 400));
    fixture.detectChanges();

    expect(getAllByText('Password must be 12 characters or more').length).toBeGreaterThanOrEqual(2);
  });

  it('should reject passwords above the maximum length', async () => {
    mockValidatePasswordService.validatePassword.mockReturnValue(
      of({ errors: [{ code: 'INVALID_MAX_LENGTH', message: 'Password must be 127 characters or less' }] }),
    );

    type(document.getElementById('password') as HTMLInputElement, 'SomeStrongP@ssw0rd');
    insertValidatePassword('SomeStrongP@ssw0rd');
    click(getByText('Submit'));
    fixture.detectChanges();
    await new Promise((r) => setTimeout(r, 400));
    fixture.detectChanges();

    expect(getAllByText('Password must be 127 characters or less').length).toBeGreaterThanOrEqual(2);
  });

  it('should show a message when the password check service is unavailable', async () => {
    mockValidatePasswordService.validatePassword.mockReturnValue(
      of({
        errors: [
          {
            code: 'PWNED_SERVICE_UNAVAILABLE',
            message: 'Password check service is temporarily unavailable. Please try again later',
          },
        ],
      }),
    );

    type(document.getElementById('password') as HTMLInputElement, 'SomeStrongP@ssw0rd');
    insertValidatePassword('SomeStrongP@ssw0rd');
    click(getByText('Submit'));
    fixture.detectChanges();
    await new Promise((r) => setTimeout(r, 400));
    fixture.detectChanges();

    expect(
      getAllByText('Password check service is temporarily unavailable. Please try again later').length,
    ).toBeGreaterThanOrEqual(2);
  });

  it('should fall back to the error message when the code is unknown', async () => {
    mockValidatePasswordService.validatePassword.mockReturnValue(
      of({ errors: [{ code: 'UNKNOWN_ERROR_CODE', message: 'A custom validation message from the server' }] }),
    );

    type(document.getElementById('password') as HTMLInputElement, 'SomeStrongP@ssw0rd');
    insertValidatePassword('SomeStrongP@ssw0rd');
    click(getByText('Submit'));
    fixture.detectChanges();
    await new Promise((r) => setTimeout(r, 400));
    fixture.detectChanges();

    expect(getAllByText('A custom validation message from the server').length).toBeGreaterThanOrEqual(2);
  });
});

function insertValidatePassword(input: string) {
  click(document.getElementById('validatePassword') as HTMLElement);
  type(document.getElementById('validatePassword') as HTMLInputElement, input);
}
