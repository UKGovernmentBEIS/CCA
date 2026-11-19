import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { catchError, EMPTY } from 'rxjs';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { TasksApiService } from '@requests/common';
import { ErrorSummaryComponent, ErrorSummaryInfo } from '@shared/components';

import { createEtsSubmitDto } from '../ets-reporting.transform';
import {
  ETS_SUBMIT_ERROR_MESSAGES,
  ETS_SUBMIT_UNEXPECTED_ERROR,
  EtsSubmitErrorCode,
} from './ets-reporting-submit-errors';

@Component({
  selector: 'cca-ets-reporting-submit',
  template: `
    @if (errorSummaryInfo(); as error) {
      <cca-error-summary [errorSummaryInfo]="error" />
    }
    <netz-page-heading>Submit your UK ETS report</netz-page-heading>
    <div class="govuk-!-width-two-thirds">
      <p>
        By selecting ‘Confirm and submit’ you confirm that the information in your report is correct to the best of your
        knowledge.
      </p>
    </div>
    <button netzPendingButton govukButton type="button" (click)="onSubmit()" class="govuk-!-margin-top-8">
      Confirm and submit UK ETS report
    </button>
    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [
    PageHeadingComponent,
    ButtonDirective,
    PendingButtonDirective,
    ErrorSummaryComponent,
    ReturnToTaskOrActionPageComponent,
  ],
})
export default class EtsReportingSubmitComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly api = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  readonly errorSummaryInfo = signal<ErrorSummaryInfo | null>(null);

  onSubmit(): void {
    this.errorSummaryInfo.set(null);
    this.api
      .saveRequestTaskAction(createEtsSubmitDto(this.store.select(requestTaskQuery.selectRequestTaskId)()))
      .pipe(
        catchError((error) => {
          const code = error?.error?.code as EtsSubmitErrorCode;
          this.errorSummaryInfo.set({ ...(ETS_SUBMIT_ERROR_MESSAGES[code] ?? ETS_SUBMIT_UNEXPECTED_ERROR) });
          return EMPTY;
        }),
      )
      .subscribe(() =>
        this.router.navigate(['confirmation'], {
          relativeTo: this.route,
          replaceUrl: true,
          state: { etsReportSubmitted: true },
        }),
      );
  }
}
