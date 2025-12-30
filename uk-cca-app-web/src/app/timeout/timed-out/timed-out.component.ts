import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

import { PageHeadingComponent } from '@netz/common/components';
import { SecondsToMinutesPipe } from '@shared/pipes';
import { AuthService } from '@shared/services';

@Component({
  selector: 'cca-timed-out',
  template: `
    <netz-page-heading size="xl">Your session has timed out</netz-page-heading>
    <p>
      @if (idle$ | async) {
        We have reset your session because you did not do anything for {{ idle$ | async | secondsToMinutes }} . We did
        this to keep your information secure.
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
  imports: [PageHeadingComponent, AsyncPipe, SecondsToMinutesPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TimedOutComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly authService = inject(AuthService);

  protected readonly idle$ = this.activatedRoute.queryParamMap.pipe(
    map((queryParamMap) => Number(queryParamMap.get('idle'))),
  );

  onSignInAgain(): void {
    this.authService.login({ redirectUri: location.origin });
  }
}
