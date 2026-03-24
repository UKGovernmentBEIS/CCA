import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map, of } from 'rxjs';

import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { ButtonDirective, ErrorSummaryComponent } from '@netz/govuk-components';
import { PASSWORD_FORM, PasswordComponent, passwordFormFactory } from '@shared/components';

import { ForgotPasswordService } from 'cca-api';

import { ResetPasswordStore } from '../+store/reset-password.store';

@Component({
  selector: 'cca-reset-password',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        @if (isSummaryDisplayed()) {
          <govuk-error-summary [form]="form" />
        }

        <form (ngSubmit)="submitPassword()" [formGroup]="form">
          <h2 class="govuk-heading-l">Reset your password</h2>
          <p>Enter your new password</p>
          <cca-password [passwordLabel]="passwordLabel" [confirmPasswordLabel]="newPasswordLabel" />
          <button govukButton type="submit">Submit</button>
        </form>
      </div>
    </div>
  `,
  imports: [ErrorSummaryComponent, PasswordComponent, ButtonDirective, ReactiveFormsModule],
  providers: [passwordFormFactory],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ResetPasswordComponent implements OnInit {
  readonly form = inject<UntypedFormGroup>(PASSWORD_FORM);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly resetPasswordStore = inject(ResetPasswordStore);
  private readonly forgotPasswordService = inject(ForgotPasswordService);

  protected isSummaryDisplayed = signal(false);
  protected readonly passwordLabel = 'New password';
  protected readonly newPasswordLabel = 'Confirm new password';
  protected token: string;
  protected readonly email: string;

  ngOnInit() {
    this.token = this.route.snapshot.queryParamMap.get('token');

    this.forgotPasswordService
      .verifyToken({ token: this.token })
      .pipe(
        map((emailDTO) => {
          this.resetPasswordStore.setState({ ...this.resetPasswordStore.state, email: emailDTO.email });
        }),
        map(() => ({ url: 'success' })),
        catchBadRequest([ErrorCodes.EMAIL1001, ErrorCodes.TOKEN1001], (res) =>
          of({ url: 'invalid-link', code: res.error.code }),
        ),
      )
      .subscribe(({ code, url }: { url: string; code: string }) => {
        if (url !== 'success') {
          code === ErrorCodes.TOKEN1001
            ? this.router.navigate(['error', '404'])
            : this.router.navigate(['forgot-password', url]);
        }
      });

    const password = this.resetPasswordStore.state.password;
    this.form.patchValue({ password, validatePassword: password });
  }

  submitPassword(): void {
    if (this.form.valid) {
      this.resetPasswordStore.setState({
        ...this.resetPasswordStore.state,
        password: this.form.controls.password.value,
        token: this.token,
      });

      this.router.navigate(['../otp'], {
        relativeTo: this.route,
      });
    } else {
      this.isSummaryDisplayed.set(true);
    }
  }
}
