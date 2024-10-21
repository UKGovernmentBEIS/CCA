import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ButtonDirective, ErrorSummaryComponent } from '@netz/govuk-components';
import { PageHeadingComponent, PASSWORD_FORM, PasswordComponent, passwordFormFactory } from '@shared/components';
import { PendingButtonDirective } from '@shared/directives';

import { SectorUserInvitationStore } from '../sector-user-invitation.store';

@Component({
  selector: 'cca-sector-user-invitation-create-password',
  templateUrl: './sector-user-invitation-create-password.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    PageHeadingComponent,
    PasswordComponent,
    ReactiveFormsModule,
    ButtonDirective,
    ErrorSummaryComponent,
    PendingButtonDirective,
  ],
  providers: [passwordFormFactory],
})
export class SectorUserInvitationCreatePasswordComponent {
  private readonly store = inject(SectorUserInvitationStore);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  readonly form = inject<FormGroup>(PASSWORD_FORM);

  isErrorSummaryDisplayed = signal(false);

  onSubmitPassword() {
    if (this.form.invalid) {
      this.isErrorSummaryDisplayed.set(true);
    } else {
      this.store.updateState({ ...this.store.state, password: this.form.value.password });
      this.router.navigate(['..', 'summary'], { relativeTo: this.activatedRoute });
    }
  }
}
