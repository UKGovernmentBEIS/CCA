import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { TaskItemStatus, TasksApiService } from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { AdminTerminationSubmitRequestTaskPayload } from 'cca-api';

import { adminTerminationQuery } from '../../../admin-termination.selectors';
import { toAdminTerminationReasonSummaryData } from '../../../admin-termination-summary-data';
import { createRequestTaskActionProcessDTO } from '../../../transform';
import { REASON_FOR_ADMIN_TERMINATION_SUBTASK } from '../../../types';

@Component({
  selector: 'cca-reason-for-admin-termination-check-your-answers',
  template: `
    <div>
      <netz-page-heading caption="Admin termination">Check your answers</netz-page-heading>
      <cca-summary [data]="summaryData" />
      <button netzPendingButton govukButton type="button" (click)="onSubmit()">Confirm and complete</button>
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [SummaryComponent, PageHeadingComponent, ButtonDirective, ReturnToTaskOrActionPageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ReasonForAdminTerminationSummaryComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  protected readonly tasksApiService = inject(TasksApiService);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  private readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryData = toAdminTerminationReasonSummaryData(
    this.requestTaskStore.select(adminTerminationQuery.selectReasonDetails)(),
    this.requestTaskStore.select(adminTerminationQuery.selectSubmitAttachments)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
    this.downloadUrl,
  );

  onSubmit() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as AdminTerminationSubmitRequestTaskPayload;

    const currentSectionsCompleted = this.requestTaskStore.select(adminTerminationQuery.selectSectionsCompleted)();

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[REASON_FOR_ADMIN_TERMINATION_SUBTASK] = TaskItemStatus.COMPLETED;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, payload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../../..'], { relativeTo: this.activatedRoute, replaceUrl: true });
    });
  }
}
