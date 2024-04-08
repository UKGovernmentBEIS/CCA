import { Component } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { OperatorUsersRegistrationService } from 'cca-api';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'cca-email',
  templateUrl: './email.component.html',
})
export class EmailComponent {
  isSummaryDisplayed: boolean;
  isVerificationSent: boolean;
  isSubmitDisabled: boolean;

  form = this.fb.group({
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
    private readonly operatorUsersRegistrationService: OperatorUsersRegistrationService,
    private readonly fb: UntypedFormBuilder,
  ) {}

  submitEmail(): void {
    if (this.form.valid) {
      this.isSubmitDisabled = true;
      this.operatorUsersRegistrationService
        .sendVerificationEmail({ email: this.form.get('email').value })
        .subscribe(() => {
          this.isVerificationSent = true;
        });
    } else {
      this.isSummaryDisplayed = true;
    }
  }
}
