import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, UntypedFormBuilder } from '@angular/forms';

import { ButtonDirective, GovukValidators, TextInputComponent } from '@netz/govuk-components';
import { BackToTopComponent } from '@shared/components';

import { ForgotPasswordService } from 'cca-api';

import { EmailSentComponent } from '../email-sent/email-sent.component';

@Component({
  selector: 'cca-submit-email',
  templateUrl: './submit-email.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule, BackToTopComponent, EmailSentComponent, TextInputComponent, ButtonDirective],
})
export class SubmitEmailComponent {
  private readonly forgotPasswordService = inject(ForgotPasswordService);
  private readonly fb = inject(UntypedFormBuilder);

  protected readonly isSummaryDisplayed = signal<boolean>(false);
  protected readonly isEmailSent = signal<boolean>(false);

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

  onSubmit(): void {
    if (this.form.valid) {
      this.forgotPasswordService.sendResetPasswordEmail({ email: this.form.get('email').value }).subscribe(() => {
        this.isEmailSent.set(true);
      });
    } else {
      this.isSummaryDisplayed.set(true);
    }
  }

  retryResetPassword() {
    this.isEmailSent.set(false);
  }
}
