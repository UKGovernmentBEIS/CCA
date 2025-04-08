import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { ButtonDirective, ErrorSummaryComponent } from '@netz/govuk-components';
import { PASSWORD_FORM, PasswordComponent, passwordFormFactory } from '@shared/components';

import { OperatorUserInvitationStore } from '../store';

@Component({
  selector: 'cca-operator-user-create-password',
  template: `
    @if (isErrorSummaryDisplayed()) {
      <govuk-error-summary [form]="form"></govuk-error-summary>
    }

    <div class="govuk-body govuk-!-width-three-quarters">
      <netz-page-heading [caption]="'Create user account'">Create a password</netz-page-heading>

      <form (ngSubmit)="onSubmit()" [formGroup]="form" data-testid="invited-operator-user-password-form">
        <cca-password></cca-password>
        <button netzPendingButton govukButton type="submit">Continue</button>
      </form>
    </div>
  `,
  standalone: true,
  imports: [
    PageHeadingComponent,
    PasswordComponent,
    ReactiveFormsModule,
    PendingButtonDirective,
    ErrorSummaryComponent,
    ButtonDirective,
  ],
  providers: [passwordFormFactory],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorUserCreatePasswordComponent {
  private readonly store = inject(OperatorUserInvitationStore);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  readonly form = inject<FormGroup>(PASSWORD_FORM);

  isErrorSummaryDisplayed = signal(false);

  onSubmit() {
    if (this.form.invalid) {
      this.isErrorSummaryDisplayed.set(true);
    } else {
      this.store.updateState({ password: this.form.value.password });
      this.router.navigate(['..', 'summary'], { relativeTo: this.route });
    }
  }
}
