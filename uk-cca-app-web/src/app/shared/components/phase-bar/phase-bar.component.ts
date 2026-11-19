import { Component, inject, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AuthStore, selectUserProfile } from '@netz/common/auth';
import { PhaseBannerComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-phase-bar',
  template: `
    <govuk-phase-banner phase="BETA">
      @if (isUserLoggedIn()) {
        This is a new service – your <a class="govuk-link" routerLink="feedback">feedback</a> will help us to improve
        it.
      }

      @if (userProfile(); as user) {
        <span style="float: right">
          You are signed in as: <strong>{{ user.firstName }} {{ user.lastName }}</strong>
        </span>
      }
    </govuk-phase-banner>
  `,
  imports: [PhaseBannerComponent, RouterLink],
})
export class PhaseBarComponent {
  protected readonly isUserLoggedIn = input(false);
  protected readonly userProfile = inject(AuthStore).select(selectUserProfile);
}
