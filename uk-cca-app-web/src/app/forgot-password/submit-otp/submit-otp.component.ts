import { ChangeDetectionStrategy, Component, signal } from '@angular/core';
import { ReactiveFormsModule, UntypedFormBuilder } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { EMPTY } from 'rxjs';

import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { ErrorSummaryComponent, GovukValidators, TextInputComponent } from '@netz/govuk-components';
import { BackToTopComponent } from '@shared/components';
import { WizardStepComponent } from '@shared/components';
import { AuthService } from '@shared/services';

import { ForgotPasswordService } from 'cca-api';

import { ResetPasswordStore } from '../+store/reset-password.store';

@Component({
  selector: 'cca-submit-otp',
  templateUrl: './submit-otp.component.html',
  imports: [
    ErrorSummaryComponent,
    ReactiveFormsModule,
    WizardStepComponent,
    RouterLink,
    BackToTopComponent,
    TextInputComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmitOtpComponent {
  protected readonly isSummaryDisplayed = signal<boolean>(false);
  protected readonly email = this.store.state.email;
  protected isPasswordReset = false;

  protected readonly form = this.fb.group({
    otp: [
      null,
      [
        GovukValidators.required('Enter the 6-digit code'),
        GovukValidators.pattern('[0-9]*', 'Digit code must contain numbers only'),
        GovukValidators.minLength(6, 'Digit code must contain exactly 6 characters'),
        GovukValidators.maxLength(6, 'Digit code must contain exactly 6 characters'),
      ],
    ],
  });

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly forgotPasswordService: ForgotPasswordService,
    private readonly fb: UntypedFormBuilder,
    private readonly store: ResetPasswordStore,
  ) {}

  onSubmit(): void {
    this.forgotPasswordService
      .resetPassword({
        token: this.store.state.token,
        otp: this.form.value.otp,
        password: this.store.state.password,
      })
      .pipe(
        catchBadRequest([ErrorCodes.OTP1001, ErrorCodes.USER1004, ErrorCodes.USER1005], (res) => {
          switch (res.error.code) {
            case ErrorCodes.OTP1001:
              this.form.get('otp').setErrors({ otpInvalid: 'Invalid OTP' });
              break;
            case ErrorCodes.USER1004:
            case ErrorCodes.USER1005:
              this.router.navigate(['error', '404']);
          }
          this.isSummaryDisplayed.set(true);
          return EMPTY;
        }),
      )
      .subscribe(() => {
        this.isPasswordReset = true;
      });
  }

  onSignInAgain(): void {
    this.authService.login({ redirectUri: location.origin });
  }
}
