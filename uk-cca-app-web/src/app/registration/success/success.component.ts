import { ChangeDetectionStrategy, Component } from '@angular/core';

import { AuthService } from '@core/services/auth.service';

import { UserRegistrationStore } from '../store/user-registration.store';

@Component({
  selector: 'cca-success',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel title="You've successfully created a user account"></govuk-panel>
        <p class="govuk-body">We have sent an email with your user account details.</p>
        <p class="govuk-body">
          When you sign in to the Energy Savings Opportunity Scheme reporting service for the first time, you'll be able
          to set up two-factor authentication.
        </p>
        <h3 class="govuk-heading-m">What happens next</h3>
        <p class="govuk-body">
          You can sign in to the Energy Savings Opportunity Scheme reporting service and apply to create a new
          organisation account.
        </p>
        <button type="button" (click)="authService.login({})" govukButton>Sign in</button>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SuccessComponent {
  constructor(
    readonly authService: AuthService,
    private readonly store: UserRegistrationStore,
  ) {
    store.reset();
  }
}
