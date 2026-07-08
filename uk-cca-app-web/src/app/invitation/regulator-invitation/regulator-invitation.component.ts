import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, of, switchMap } from 'rxjs';

import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { PASSWORD_FORM, PasswordComponent, passwordFormFactory, WizardStepComponent } from '@shared/components';

import { RegulatorUsersRegistrationService } from 'cca-api';

import { InvitedRegulatorUserStore } from './invited-regulator-user.store';

@Component({
  selector: 'cca-regulator-invitation',
  template: `
    <cca-wizard-step [formGroup]="form" submitText="Submit" heading="Activate your account" (formSubmit)="onSubmit()">
      <div class="govuk-!-width-three-quarters">
        <cca-password />
      </div>
    </cca-wizard-step>
  `,
  imports: [PasswordComponent, ReactiveFormsModule, WizardStepComponent],
  providers: [passwordFormFactory],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegulatorInvitationComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly regulatorUsersRegistrationService = inject(RegulatorUsersRegistrationService);
  private readonly store = inject(InvitedRegulatorUserStore);

  readonly form = inject<FormGroup>(PASSWORD_FORM);

  ngOnInit() {
    const email = this.store.state.email;
    this.form.patchValue({ email });
  }

  onSubmit() {
    this.route.queryParamMap
      .pipe(
        map((paramMap) => paramMap.get('token')),
        first(),
        switchMap((invitationToken) =>
          this.regulatorUsersRegistrationService.acceptAuthorityAndActivateRegulatorUserFromInvite({
            invitationToken,
            password: this.form.get('password').value,
          }),
        ),
        map(() => ({ url: 'confirmed' })),
        catchBadRequest(
          [
            ErrorCodes.EMAIL1001,
            ErrorCodes.TOKEN1001,
            ErrorCodes.USER1004,
            ErrorCodes.USER1001,
            ErrorCodes.AUTHORITY1005,
            ErrorCodes.AUTHORITY1014,
          ],
          (res) => of({ url: 'invalid-link', queryParams: { code: res.error.code } }),
        ),
      )
      .subscribe(({ queryParams, url }: { url: string; queryParams?: any }) =>
        this.router.navigate([url], { relativeTo: this.route, queryParams }),
      );
  }
}
