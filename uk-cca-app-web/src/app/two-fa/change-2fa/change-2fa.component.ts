import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, UntypedFormBuilder } from '@angular/forms';

import { of } from 'rxjs';

import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { PendingRequestService } from '@netz/common/services';
import { GovukValidators, PanelComponent, TextInputComponent } from '@netz/govuk-components';

import { UsersSecuritySetupService } from 'cca-api';

import { WizardStepComponent } from '../../shared/components/wizard/wizard-step.component';

@Component({
  selector: 'cca-change-2fa',
  templateUrl: './change-2fa.component.html',
  imports: [WizardStepComponent, ReactiveFormsModule, TextInputComponent, PanelComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Change2faComponent {
  readonly pendingRequest = inject(PendingRequestService);
  private readonly usersSecuritySetupService = inject(UsersSecuritySetupService);
  private readonly fb = inject(UntypedFormBuilder);

  protected readonly is2FaChanged = signal(false);

  protected readonly form = this.fb.group({
    password: [
      null,
      [
        GovukValidators.required('Enter the 6-digit code'),
        GovukValidators.pattern('[0-9]*', 'Digit code must contain numbers only'),
        GovukValidators.minLength(6, 'Digit code must contain exactly 6 characters'),
        GovukValidators.maxLength(6, 'Digit code must contain exactly 6 characters'),
      ],
    ],
  });

  onSubmit() {
    this.usersSecuritySetupService
      .requestTwoFactorAuthChange(this.form.value)
      .pipe(
        this.pendingRequest.trackRequest(),
        catchBadRequest(ErrorCodes.OTP1001, () => of('invalid-code')),
      )
      .subscribe((res) => {
        if (res === 'invalid-code') {
          this.form.get('password')?.setErrors({ invalidCode: 'Invalid code. Please try again.' });
        } else {
          this.is2FaChanged.set(true);
        }
      });
  }
}
