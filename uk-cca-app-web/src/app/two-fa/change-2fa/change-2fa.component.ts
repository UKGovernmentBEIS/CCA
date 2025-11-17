import { ChangeDetectionStrategy, Component, signal } from '@angular/core';
import { ReactiveFormsModule, UntypedFormBuilder } from '@angular/forms';
import { Router } from '@angular/router';

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

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly usersSecuritySetupService: UsersSecuritySetupService,
    private readonly fb: UntypedFormBuilder,
  ) {}

  onSubmit() {
    this.usersSecuritySetupService
      .requestTwoFactorAuthChange(this.form.value)
      .pipe(
        this.pendingRequest.trackRequest(),
        catchBadRequest(ErrorCodes.OTP1001, () => of('invalid-code')),
      )
      .subscribe((res) => {
        if (res === 'invalid-code') {
          this.router.navigate(['2fa', 'invalid-code']);
        } else {
          this.is2FaChanged.set(true);
        }
      });
  }
}
