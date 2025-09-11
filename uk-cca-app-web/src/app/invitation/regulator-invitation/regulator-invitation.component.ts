import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, of, switchMap } from 'rxjs';

import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { ButtonDirective, ErrorSummaryComponent } from '@netz/govuk-components';
import { PASSWORD_FORM, PasswordComponent, passwordFormFactory } from '@shared/components';

import { RegulatorUsersRegistrationService } from 'cca-api';

import { InvitedRegulatorUserStore } from './invited-regulator-user.store';

@Component({
  selector: 'cca-regulator-invitation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-one-half">
        @if (form.invalid && form.touched) {
          <govuk-error-summary [form]="form" />
        }

        <netz-page-heading>Activate your account</netz-page-heading>
        <form (ngSubmit)="submitPassword()" [formGroup]="form">
          <cca-password />
          <button netzPendingButton govukButton type="submit">Submit</button>
        </form>
      </div>
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
export class RegulatorInvitationComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly regulatorUsersRegistrationService = inject(RegulatorUsersRegistrationService);
  private readonly store = inject(InvitedRegulatorUserStore);

  readonly form = inject(PASSWORD_FORM);

  ngOnInit() {
    const email = this.store.state.email;
    this.form.patchValue({ email });
  }

  submitPassword() {
    this.form.markAsTouched();
    if (this.form.invalid) return;

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
