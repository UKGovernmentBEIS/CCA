import { ChangeDetectorRef, Component, DestroyRef, inject, input, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ControlContainer, FormGroupDirective, ReactiveFormsModule } from '@angular/forms';

import { ButtonDirective, TextInputComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-password',
  templateUrl: './password.component.html',
  viewProviders: [{ provide: ControlContainer, useExisting: FormGroupDirective }],
  imports: [TextInputComponent, ReactiveFormsModule, ButtonDirective],
})
export class PasswordComponent implements OnInit {
  protected readonly formGroupDirective = inject(FormGroupDirective);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly passwordLabel = input('Create a password to activate your account');
  protected readonly confirmPasswordLabel = input('Re-enter your password');

  protected readonly showLabel = signal<'Show' | 'Hide'>('Show');
  protected readonly passwordInputType = signal<'password' | 'text'>('password');

  ngOnInit(): void {
    this.formGroupDirective.form.statusChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.cdr.markForCheck());
  }

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
