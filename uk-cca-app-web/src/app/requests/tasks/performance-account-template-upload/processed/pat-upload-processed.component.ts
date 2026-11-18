import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective, NotificationBannerComponent } from '@netz/govuk-components';
import { SummaryComponent } from '@shared/components';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors';

import { RequestTaskActionPayload, TasksService } from 'cca-api';

import { PATUploadQuery } from '../+state/pat-selectors';
import { ErrorTypePipe } from '../pipes/error-type.pipe';
import { toSubmissionResultsSummaryData } from './pat-submission-results-summary-data';

@Component({
  selector: 'cca-pat-processed',
  templateUrl: './pat-upload-processed.component.html',
  imports: [NotificationBannerComponent, ButtonDirective, PendingButtonDirective, SummaryComponent, ErrorTypePipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PatUploadProcessedComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly tasksService = inject(TasksService);
  private readonly businessErrorService = inject(BusinessErrorService);

  protected readonly submissionDate = this.requestTaskStore.select(requestTaskQuery.selectRequestTask)()?.startDate;
  protected readonly fileReports = this.requestTaskStore.select(PATUploadQuery.selectFileReports)();
  protected readonly csvReportFile = this.requestTaskStore.select(PATUploadQuery.selectCsvReportFile)();
  protected readonly errorType = this.requestTaskStore.select(PATUploadQuery.selectErrorType);

  protected readonly summaryData = toSubmissionResultsSummaryData(
    this.submissionDate,
    this.fileReports,
    this.csvReportFile,
  );

  onComplete() {
    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_COMPLETE',
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
        this.router.navigate(['performance-account-template-upload/confirmation'], { relativeTo: this.activatedRoute }),
      );
  }
}
