import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { AuthStore, selectUserId } from '@netz/common/auth';
import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective, WarningTextComponent } from '@netz/govuk-components';
import { SummaryComponent } from '@shared/components';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors';

import { RequestTaskActionPayload, TasksService } from 'cca-api';

import { patCsvUploadQuery } from '../performance-account-template-csv-upload.selectors';
import { toSubmissionResultsSummaryData } from './pat-csv-submission-results-summary-data';

@Component({
  selector: 'cca-pat-csv-submission-results',
  templateUrl: './pat-csv-submission-results.component.html',
  imports: [
    WarningTextComponent,
    ButtonDirective,
    PendingButtonDirective,
    PageHeadingComponent,
    SummaryComponent,
    RouterLink,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PatCsvSubmissionResultsComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly authStore = inject(AuthStore);
  private readonly tasksService = inject(TasksService);
  private readonly businessErrorService = inject(BusinessErrorService);

  protected readonly errorMessage = this.requestTaskStore.select(patCsvUploadQuery.selectErrorMessage);
  private readonly results = this.requestTaskStore.select(patCsvUploadQuery.selectResults);
  private readonly attachments = this.requestTaskStore.select(patCsvUploadQuery.selectUploadAttachments);

  private readonly taskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId);
  private readonly assigneeUserId = this.requestTaskStore.select(requestTaskQuery.selectAssigneeUserId);
  protected readonly isUserAssignee = computed(() => this.authStore.select(selectUserId)() === this.assigneeUserId());

  protected readonly relatedActions = computed<{ text: string; link: string[] }[]>(() =>
    this.isUserAssignee()
      ? [
          { text: 'Reassign task', link: ['/', 'tasks', this.taskId().toString(), 'change-assignee'] },
          { text: 'Close task', link: ['..', 'close-task'] },
        ]
      : [{ text: 'Reassign task', link: ['/', 'tasks', this.taskId().toString(), 'change-assignee'] }],
  );

  protected readonly summaryData = computed(() =>
    toSubmissionResultsSummaryData({
      results: this.results() ?? {},
      attachments: this.attachments() ?? {},
    }),
  );

  onComplete() {
    this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_COMPLETE',
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
      .subscribe(() => this.router.navigate(['../confirmation'], { relativeTo: this.activatedRoute }));
  }
}
