import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective, NotificationBannerComponent } from '@netz/govuk-components';
import { ErrorMessageTypePipe } from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors';

import { RequestTaskActionPayload, TasksService } from 'cca-api';

import { performanceDataUploadQuery } from '../../+state/performance-data-upload-selectors';
import { toSubmissionResultsSummaryData } from './submission-results-summary-data';

@Component({
  selector: 'cca-performance-data-upload-processed',
  standalone: true,
  imports: [
    NotificationBannerComponent,
    PendingButtonDirective,
    ButtonDirective,
    ErrorMessageTypePipe,
    SummaryComponent,
  ],
  templateUrl: './performance-data-upload-processed.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PerformanceDataUploadProcessedComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly tasksService = inject(TasksService);
  private readonly businessErrorService = inject(BusinessErrorService);
  readonly errorMessage = this.requestTaskStore.select(performanceDataUploadQuery.selectErrorMessage);
  readonly successfulReportsCount = this.requestTaskStore.select(
    performanceDataUploadQuery.selectSuccessfulReportsCount,
  );
  readonly failedReportsCount = this.requestTaskStore.select(performanceDataUploadQuery.selectFailedReportsCount);
  readonly csvFile = this.requestTaskStore.select(performanceDataUploadQuery.selectCsvFile);
  readonly summaryData = computed(() => {
    return toSubmissionResultsSummaryData(this.successfulReportsCount(), this.failedReportsCount(), this.csvFile());
  });

  onComplete() {
    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'PERFORMANCE_DATA_UPLOAD_COMPLETE',
        requestTaskId: this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)(),
        requestTaskActionPayload: {
          payloadType: 'EMPTY_PAYLOAD',
        } as RequestTaskActionPayload,
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      )
      .subscribe(() =>
        this.router.navigate(['performance-data-upload/confirmation'], { relativeTo: this.activatedRoute }),
      );
  }
}
