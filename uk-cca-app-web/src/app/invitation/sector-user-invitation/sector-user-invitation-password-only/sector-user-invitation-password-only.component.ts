import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { PASSWORD_FORM, PasswordComponent, passwordFormFactory, WizardStepComponent } from '@shared/components';

import { SectorUsersRegistrationService } from 'cca-api';

import { SectorUserInvitationStore } from '../sector-user-invitation.store';

@Component({
  selector: 'cca-sector-user-invitation-password-only',
  template: `
    <cca-wizard-step
      [formGroup]="form"
      submitText="Continue"
      heading="Create a password"
      caption="Create user account"
      (formSubmit)="onSubmit()"
    >
      <div class="govuk-!-width-three-quarters">
        <cca-password />
      </div>
    </cca-wizard-step>
  `,
  imports: [PasswordComponent, ReactiveFormsModule, WizardStepComponent],
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

  onSubmit() {
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
      .subscribe((result) => {
        if (result && 'url' in result) {
          this.router.navigate([result.url], {
            relativeTo: this.activatedRoute,
            queryParams: result.queryParams,
            replaceUrl: true,
          });
        } else {
          this.router.navigate(['..', 'confirmed'], { relativeTo: this.activatedRoute, replaceUrl: true });
        }
      });
  }
}
