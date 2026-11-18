import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PASSWORD_FORM, PasswordComponent, passwordFormFactory, WizardStepComponent } from '@shared/components';

import { SectorUserInvitationStore } from '../sector-user-invitation.store';

@Component({
  selector: 'cca-sector-user-invitation-create-password',
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
export class SectorUserInvitationCreatePasswordComponent {
  private readonly store = inject(SectorUserInvitationStore);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly form = inject<FormGroup>(PASSWORD_FORM);

  onSubmit() {
    this.store.updateState({ ...this.store.state, password: this.form.value.password });
    this.router.navigate(['..', 'summary'], { relativeTo: this.activatedRoute });
  }
}
