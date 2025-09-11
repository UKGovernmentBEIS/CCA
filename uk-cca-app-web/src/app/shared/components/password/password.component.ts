import { Component, Input } from '@angular/core';
import { ControlContainer, FormGroupDirective, ReactiveFormsModule } from '@angular/forms';

import { ButtonDirective, TagComponent, TextInputComponent } from '@netz/govuk-components';
import { PasswordStrengthMeterComponent } from 'angular-password-strength-meter';

@Component({
  selector: 'cca-password',
  templateUrl: './password.component.html',
  standalone: true,
  imports: [TextInputComponent, ReactiveFormsModule, PasswordStrengthMeterComponent, TagComponent, ButtonDirective],
  viewProviders: [{ provide: ControlContainer, useExisting: FormGroupDirective }],
})
export class PasswordComponent {
  @Input() passwordLabel = 'Create a password to activate your account';
  @Input() confirmPasswordLabel = 'Re-enter your password';
  showLabel: 'Show' | 'Hide' = 'Show';
  passwordInputType: 'password' | 'text' = 'password';
  passwordStrength: number;

  constructor(readonly formGroupDirective: FormGroupDirective) {}

  togglePassword() {
    if (this.showLabel === 'Show') {
      this.showLabel = 'Hide';
      this.passwordInputType = 'text';
    } else {
      this.showLabel = 'Show';
      this.passwordInputType = 'password';
    }
  }
}
