import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { ButtonDirective, ErrorSummaryComponent } from '@netz/govuk-components';
import { PageHeadingComponent, PASSWORD_FORM, PasswordComponent, passwordFormFactory } from '@shared/components';
import { PendingButtonDirective } from '@shared/directives';

import { SectorUsersRegistrationService } from 'cca-api';

import { SectorUserInvitationStore } from '../sector-user-invitation.store';

@Component({
  selector: 'cca-sector-user-invitation-password-only',
  templateUrl: './sector-user-invitation-password-only.component.html',
  standalone: true,
  providers: [passwordFormFactory],
  imports: [
    PageHeadingComponent,
    PasswordComponent,
    ErrorSummaryComponent,
    ReactiveFormsModule,
    ButtonDirective,
    PendingButtonDirective,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectorUserInvitationPasswordOnlyComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly sectorUsersRegistrationService = inject(SectorUsersRegistrationService);
  private readonly store = inject(SectorUserInvitationStore);

  readonly form = inject<FormGroup>(PASSWORD_FORM);

  protected readonly storeUser = this.store.state;

  isErrorSummaryDisplayed = signal(false);

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
