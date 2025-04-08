import { ChangeDetectionStrategy, Component, inject, input, ViewEncapsulation } from '@angular/core';
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
        <span class="logged-in-user float-right">
          You are logged in as: <span class="govuk-!-font-weight-bold">{{ user.firstName }} {{ user.lastName }}</span>
        </span>
      }
    </govuk-phase-banner>
  `,
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [PhaseBannerComponent, RouterLink],
})
export class PhaseBarComponent {
  isUserLoggedIn = input(false);
  userProfile = inject(AuthStore).select(selectUserProfile);
}
