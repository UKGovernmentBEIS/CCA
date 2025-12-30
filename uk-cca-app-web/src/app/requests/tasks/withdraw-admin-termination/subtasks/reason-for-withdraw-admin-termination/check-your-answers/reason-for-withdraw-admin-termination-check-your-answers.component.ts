import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { TaskItemStatus, TasksApiService } from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { AdminTerminationWithdrawSubmittedRequestActionPayload } from 'cca-api';

import { createRequestTaskActionProcessDTO } from '../../../transform';
import { REASON_FOR_WITHDRAW_ADMIN_TERMINATION_SUBTASK } from '../../../types';
import { adminTerminationWithdrawQuery } from '../../../withdraw-admin-termination.selectors';
import { toWithdrawAdminTerminationReasonSummaryData } from '../../../withdraw-admin-termination-summary-data';

@Component({
  selector: 'cca-reason-for-withdraw-admin-termination-check-your-answers',
  template: `
    <div>
      <netz-page-heading caption="Withdraw admin termination">Check your answers</netz-page-heading>
      <cca-summary [data]="summaryData" />
      <button netzPendingButton govukButton type="button" (click)="onSubmit()">Confirm and complete</button>
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [SummaryComponent, PageHeadingComponent, ButtonDirective, ReturnToTaskOrActionPageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ReasonForWithdrawAdminTerminationCheckYourAnswersComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  protected readonly tasksApiService = inject(TasksApiService);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryData = toWithdrawAdminTerminationReasonSummaryData(
    this.requestTaskStore.select(adminTerminationWithdrawQuery.selectReasonDetails)(),
    this.requestTaskStore.select(adminTerminationWithdrawQuery.selectAttachments)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
    this.downloadUrl,
  );

  onSubmit() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as AdminTerminationWithdrawSubmittedRequestActionPayload;

    const currentSectionsCompleted = this.requestTaskStore.select(
      adminTerminationWithdrawQuery.selectSectionsCompleted,
    )();

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[REASON_FOR_WITHDRAW_ADMIN_TERMINATION_SUBTASK] = TaskItemStatus.COMPLETED;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, payload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../../..'], { relativeTo: this.activatedRoute, replaceUrl: true });
    });
  }
}
