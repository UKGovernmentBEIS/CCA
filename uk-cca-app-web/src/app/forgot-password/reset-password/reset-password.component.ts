import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map, of } from 'rxjs';

import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { PASSWORD_FORM, PasswordComponent, passwordFormFactory, WizardStepComponent } from '@shared/components';

import { ForgotPasswordService } from 'cca-api';

import { ResetPasswordStore } from '../+store/reset-password.store';

@Component({
  selector: 'cca-reset-password',
  template: `
    <cca-wizard-step [formGroup]="form" submitText="Submit" heading="Reset your password" (formSubmit)="onSubmit()">
      <div class="govuk-!-width-two-thirds">
        <cca-password passwordLabel="New password" confirmPasswordLabel="Confirm new password" />
      </div>
    </cca-wizard-step>
  `,
  imports: [PasswordComponent, ReactiveFormsModule, WizardStepComponent],
  providers: [passwordFormFactory],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ResetPasswordComponent implements OnInit {
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly resetPasswordStore = inject(ResetPasswordStore);
  private readonly forgotPasswordService = inject(ForgotPasswordService);

  readonly form = inject<FormGroup>(PASSWORD_FORM);

  protected token: string;

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

  onSubmit() {
    this.resetPasswordStore.setState({
      ...this.resetPasswordStore.state,
      password: this.form.controls.password.value,
      token: this.token,
    });

    this.router.navigate(['../otp'], { relativeTo: this.route, replaceUrl: true });
  }
}
