import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { TaskItemStatus, TasksApiService } from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { FinalDecisionTypePipe } from '@shared/pipes';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { AdminTerminationFinalDecisionRequestTaskPayload } from 'cca-api';

import { adminTerminationFinalDecisionQuery } from '../../../admin-termination-final-decision.selectors';
import { toFinalDecisionReasonSummaryData } from '../../../final-decision-reason-summary-data';
import { createRequestTaskActionProcessDTO } from '../../../transform';
import { ADMIN_TERMINATION_FINAL_DECISION_SUBTASK } from '../../../types';

@Component({
  selector: 'cca-final-decision-reason-check-your-answers',
  template: `
    <div>
      <netz-page-heading data-testid="heading" [caption]="finalDecisionType | finalDecisionType">
        Check your answers
      </netz-page-heading>

      <cca-summary [data]="summaryData" />

      <button netzPendingButton govukButton type="button" (click)="onSubmit()">Confirm and complete</button>
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [
    SummaryComponent,
    PageHeadingComponent,
    ButtonDirective,
    FinalDecisionTypePipe,
    ReturnToTaskOrActionPageComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class FinalDecisionReasonCheckYourAnswersComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  protected readonly tasksApiService = inject(TasksApiService);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly finalDecisionType = this.requestTaskStore.select(
    adminTerminationFinalDecisionQuery.selectReasonDetails,
  )()?.finalDecisionType;

  protected readonly summaryData = toFinalDecisionReasonSummaryData(
    this.requestTaskStore.select(adminTerminationFinalDecisionQuery.selectReasonDetails)(),
    this.requestTaskStore.select(adminTerminationFinalDecisionQuery.selectAttachments)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
    this.downloadUrl,
  );

  onSubmit() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as AdminTerminationFinalDecisionRequestTaskPayload;

    const currentSectionsCompleted = this.requestTaskStore.select(
      adminTerminationFinalDecisionQuery.selectSectionsCompleted,
    )();

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[ADMIN_TERMINATION_FINAL_DECISION_SUBTASK] = TaskItemStatus.COMPLETED;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, payload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../../..'], { relativeTo: this.activatedRoute, replaceUrl: true });
    });
  }
}
