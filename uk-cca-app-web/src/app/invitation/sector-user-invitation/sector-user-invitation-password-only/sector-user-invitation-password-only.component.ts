import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { ButtonDirective, ErrorSummaryComponent } from '@netz/govuk-components';
import { PASSWORD_FORM, PasswordComponent, passwordFormFactory } from '@shared/components';

import { SectorUsersRegistrationService } from 'cca-api';

import { SectorUserInvitationStore } from '../sector-user-invitation.store';

@Component({
  selector: 'cca-sector-user-invitation-password-only',
  template: `
    @if (isErrorSummaryDisplayed()) {
      <govuk-error-summary [form]="form" />
    }

    <div class="govuk-!-width-three-quarters">
      <netz-page-heading [caption]="'Create user account'">Create a password</netz-page-heading>

      <form (ngSubmit)="onSubmitPassword()" [formGroup]="form" data-testid="invited-sector-user-password-form">
        <cca-password />
        <button netzPendingButton govukButton type="submit">Continue</button>
      </form>
    </div>
  `,
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
export class SectorUserInvitationPasswordOnlyComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly sectorUsersRegistrationService = inject(SectorUsersRegistrationService);
  private readonly store = inject(SectorUserInvitationStore);

  protected readonly form = inject<FormGroup>(PASSWORD_FORM);

  protected readonly storeUser = this.store.state;

  protected readonly isErrorSummaryDisplayed = signal(false);

  onSubmitPassword() {
    if (this.form.invalid) {
      this.isErrorSummaryDisplayed.set(true);
    } else {
      this.sectorUsersRegistrationService
        .acceptAuthorityAndSetCredentialsToSectorUser({
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
