import { Component } from '@angular/core';
import { ReactiveFormsModule, UntypedFormBuilder } from '@angular/forms';

import { ButtonDirective, GovukValidators, TextInputComponent } from '@netz/govuk-components';
import { BackToTopComponent } from '@shared/components';

import { ForgotPasswordService } from 'cca-api';

import { EmailSentComponent } from '../email-sent/email-sent.component';

@Component({
  selector: 'cca-submit-email',
  templateUrl: './submit-email.component.html',
  imports: [ReactiveFormsModule, BackToTopComponent, EmailSentComponent, TextInputComponent, ButtonDirective],
})
export class SubmitEmailComponent {
  protected isSummaryDisplayed: boolean;
  protected isEmailSent: boolean;

  protected readonly form = this.fb.group({
    email: [
      null,
      [
        GovukValidators.required('Enter your email address'),
        GovukValidators.email('Enter an email address in the correct format, like name@example.com'),
        GovukValidators.maxLength(255, 'Enter an email address with a maximum of 255 characters'),
      ],
    ],
  });

  constructor(
    private readonly forgotPasswordService: ForgotPasswordService,
    private readonly fb: UntypedFormBuilder,
  ) {}

  onSubmit(): void {
    if (this.form.valid) {
      this.forgotPasswordService.sendResetPasswordEmail({ email: this.form.get('email').value }).subscribe(() => {
        this.isEmailSent = true;
      });
    } else {
      this.isSummaryDisplayed = true;
    }
  }

  retryResetPassword() {
    this.isEmailSent = false;
  }
}
