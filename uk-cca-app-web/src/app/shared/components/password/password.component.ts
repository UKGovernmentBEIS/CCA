import { Component, inject, input, signal } from '@angular/core';
import { ControlContainer, FormGroupDirective, ReactiveFormsModule } from '@angular/forms';

import { ButtonDirective, TagComponent, TextInputComponent } from '@netz/govuk-components';

import { PasswordStrengthMeterComponent } from '../password-strength-meter/password-strength-meter.component';

@Component({
  selector: 'cca-password',
  templateUrl: './password.component.html',
  imports: [TextInputComponent, ReactiveFormsModule, TagComponent, ButtonDirective, PasswordStrengthMeterComponent],
  viewProviders: [{ provide: ControlContainer, useExisting: FormGroupDirective }],
})
export class PasswordComponent {
  protected readonly formGroupDirective = inject(FormGroupDirective);

  protected readonly passwordLabel = input('Create a password to activate your account');
  protected readonly confirmPasswordLabel = input('Re-enter your password');

  protected readonly showLabel = signal<'Show' | 'Hide'>('Show');
  protected readonly passwordInputType = signal<'password' | 'text'>('password');
  protected readonly passwordStrength = signal<number | null>(null);

  togglePassword() {
    if (this.showLabel() === 'Show') {
      this.showLabel.set('Hide');
      this.passwordInputType.set('text');
    } else {
      this.showLabel.set('Show');
      this.passwordInputType.set('password');
    }
  }
}
