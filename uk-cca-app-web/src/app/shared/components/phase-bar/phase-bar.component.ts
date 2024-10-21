import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, ViewEncapsulation } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AuthStore, selectUserProfile } from '@netz/common/auth';
import { LinkDirective, PhaseBannerComponent } from '@netz/govuk-components';

/* eslint-disable @angular-eslint/use-component-view-encapsulation */
@Component({
  selector: 'cca-phase-bar',
  template: `
    <govuk-phase-banner phase="BETA">
      This is a new service – your <a govukLink routerLink="feedback">feedback</a> will help us to improve it.
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
  imports: [PhaseBannerComponent, LinkDirective, RouterLink, AsyncPipe],
})
export class PhaseBarComponent {
  userProfile = inject(AuthStore).select(selectUserProfile);
}
