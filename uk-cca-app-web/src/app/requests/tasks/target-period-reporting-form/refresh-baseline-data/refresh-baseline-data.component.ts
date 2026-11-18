import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { catchError, EMPTY } from 'rxjs';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { TasksApiService, TP_REPORTING_REFRESH_ERROR_MESSAGES, TpReportingRefreshErrorCode } from '@requests/common';
import { ErrorSummaryComponent, ErrorSummaryInfo } from '@shared/components';

@Component({
  selector: 'cca-refresh-baseline-data',
  templateUrl: './refresh-baseline-data.component.html',
  imports: [
    PendingButtonDirective,
    ButtonDirective,
    ReturnToTaskOrActionPageComponent,
    PageHeadingComponent,
    ErrorSummaryComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RefreshBaselineDataComponent {
  private readonly tasksApiService = inject(TasksApiService);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  readonly isErrorSummaryDisplayed = signal(false);
  readonly errorSummaryInfo = signal<ErrorSummaryInfo>({ message: '', link: '', linkText: '' });

  onRefreshBaselineData() {
    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();

    this.tasksApiService
      .saveRequestTaskAction({
        requestTaskId,
        requestTaskActionType: 'PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_REFRESH_APPLICATION',
        requestTaskActionPayload: {
          payloadType: 'EMPTY_PAYLOAD',
        },
      })
      .pipe(
        catchError((err) => {
          const info = TP_REPORTING_REFRESH_ERROR_MESSAGES[err.error.code as TpReportingRefreshErrorCode];

          if (info) {
            this.errorSummaryInfo.set(info);
            this.isErrorSummaryDisplayed.set(true);
          }

          return EMPTY;
        }),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.activatedRoute, replaceUrl: true }));
  }
}
