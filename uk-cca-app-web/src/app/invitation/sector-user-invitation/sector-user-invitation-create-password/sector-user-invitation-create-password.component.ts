import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { ButtonDirective, ErrorSummaryComponent } from '@netz/govuk-components';
import { PASSWORD_FORM, PasswordComponent, passwordFormFactory } from '@shared/components';

import { SectorUserInvitationStore } from '../sector-user-invitation.store';

@Component({
  selector: 'cca-sector-user-invitation-create-password',
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
    ReactiveFormsModule,
    ButtonDirective,
    ErrorSummaryComponent,
    PendingButtonDirective,
  ],
  providers: [passwordFormFactory],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectorUserInvitationCreatePasswordComponent {
  private readonly store = inject(SectorUserInvitationStore);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly form = inject<FormGroup>(PASSWORD_FORM);

  protected readonly isErrorSummaryDisplayed = signal(false);

  onSubmitPassword() {
    if (this.form.invalid) {
      this.isErrorSummaryDisplayed.set(true);
    } else {
      this.store.updateState({ ...this.store.state, password: this.form.value.password });
      this.router.navigate(['..', 'summary'], { relativeTo: this.activatedRoute });
    }
  }
}
