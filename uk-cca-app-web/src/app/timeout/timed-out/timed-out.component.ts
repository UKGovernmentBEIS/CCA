import { Component, inject, input, numberAttribute } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { SecondsToMinutesPipe } from '@shared/pipes';
import { AuthService } from '@shared/services';

@Component({
  selector: 'cca-timed-out',
  template: `
    <netz-page-heading size="xl">Your session has timed out</netz-page-heading>
    <p>
      @if (idle()) {
        We have reset your session because you did not do anything for {{ idle() | secondsToMinutes }} . We did this to
        keep your information secure.
      } @else {
        We have reset your session because it expired. We did this to keep your information secure.
      }
    </p>

    <button type="button" class="govuk-button" (click)="onSignInAgain()">Sign in again</button>

    <p>
      If you don't want to start again, you can
      <a class="govuk-link" href="https://www.gov.uk/">return to GOV.UK</a>
    </p>
  `,
  imports: [PageHeadingComponent, SecondsToMinutesPipe],
})
export class TimedOutComponent {
  private readonly authService = inject(AuthService);

  /** `idle` query param: seconds of inactivity before the session was reset. */
  readonly idle = input(0, { transform: (value: unknown) => numberAttribute(value, 0) });

  onSignInAgain(): void {
    this.authService.login({ redirectUri: location.origin });
  }
}
