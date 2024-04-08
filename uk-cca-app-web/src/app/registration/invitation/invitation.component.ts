import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

import { UserRegistrationStore } from '../store/user-registration.store';

@Component({
  selector: 'cca-invitation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel
          title="You have been added as a user to the account of {{ (account$ | async)?.accountInstallationName }}"
        ></govuk-panel>

        <h2 class="govuk-heading-m">What happens next</h2>
        <p class="govuk-body">
          All system alerts, notices and official communications will be sent to your registered email.
        </p>

        <p class="govuk-body">
          <a routerLink="/dashboard" govukLink>Go to my dashboard</a>
        </p>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InvitationComponent {
  account$ = this.activatedRoute.data.pipe(map((data) => data?.account));

  constructor(
    private readonly activatedRoute: ActivatedRoute,
    private readonly store: UserRegistrationStore,
  ) {
    store.reset();
  }
}
