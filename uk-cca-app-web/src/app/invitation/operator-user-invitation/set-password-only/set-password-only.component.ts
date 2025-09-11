import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { ButtonDirective, ErrorSummaryComponent } from '@netz/govuk-components';
import { PASSWORD_FORM, PasswordComponent, passwordFormFactory } from '@shared/components';

import { OperatorUsersRegistrationService } from 'cca-api';

import { OperatorUserInvitationStore } from '../store';

@Component({
  selector: 'cca-set-password-only',
  template: `
    @if (isErrorSummaryDisplayed()) {
      <govuk-error-summary [form]="form" />
    }

    <div class="govuk-!-width-three-quarters">
      <netz-page-heading [caption]="'Create user account'">Create a password</netz-page-heading>

      <form (ngSubmit)="onSubmitPassword()" [formGroup]="form" data-testid="invited-operator-user-password-form">
        <cca-password />
        <button netzPendingButton govukButton type="submit">Continue</button>
      </form>
    </div>
  `,
  standalone: true,
  imports: [
    PageHeadingComponent,
    PasswordComponent,
    ErrorSummaryComponent,
    ReactiveFormsModule,
    ButtonDirective,
    PendingButtonDirective,
  ],
  providers: [passwordFormFactory],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SetPasswordOnlyComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly operatorUsersRegistrationService = inject(OperatorUsersRegistrationService);
  private readonly store = inject(OperatorUserInvitationStore);

  protected readonly form = inject<FormGroup>(PASSWORD_FORM);

  protected readonly storeUser = this.store.state;

  protected readonly isErrorSummaryDisplayed = signal(false);

  onSubmitPassword() {
    if (this.form.invalid) {
      this.isErrorSummaryDisplayed.set(true);
    } else {
      this.operatorUsersRegistrationService
        .acceptAuthorityAndSetCredentialsToOperatorUser({
          invitationToken: this.storeUser.emailToken,
          password: this.form.value.password,
        })
        .pipe(
          catchBadRequest([ErrorCodes.EMAIL1001, ErrorCodes.TOKEN1001, ErrorCodes.USER1004], (res) =>
            of({ url: '../invalid-link', queryParams: { code: res.error.code } }),
          ),
        )
        .subscribe(() => {
          this.router.navigate(['..', 'confirmed'], { relativeTo: this.activatedRoute, replaceUrl: true });
        });
    }
  }
}
