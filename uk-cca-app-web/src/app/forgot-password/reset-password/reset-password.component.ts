import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map, of } from 'rxjs';

import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { ButtonDirective, ErrorSummaryComponent, LinkDirective } from '@netz/govuk-components';
import { PASSWORD_FORM, PasswordComponent, passwordFormFactory } from '@shared/components';

import { ForgotPasswordService } from 'cca-api';

import { ResetPasswordStore } from '../store/reset-password.store';

@Component({
  selector: 'cca-reset-password',
  templateUrl: './reset-password.component.html',
  providers: [passwordFormFactory],
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [ErrorSummaryComponent, LinkDirective, PasswordComponent, ButtonDirective, ReactiveFormsModule],
})
export class ResetPasswordComponent implements OnInit {
  isSummaryDisplayed = false;
  passwordLabel = 'New password';
  newPasswordLabel = 'Confirm new password';
  token: string;
  email: string;

  constructor(
    @Inject(PASSWORD_FORM) readonly form: UntypedFormGroup,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly resetPasswordStore: ResetPasswordStore,
    private readonly forgotPasswordService: ForgotPasswordService,
  ) {}

  ngOnInit(): void {
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
        password: this.form.get('password').value,
        token: this.token,
      });

      this.router.navigate(['../otp'], {
        relativeTo: this.route,
      });
    } else {
      this.isSummaryDisplayed = true;
    }
  }
}
