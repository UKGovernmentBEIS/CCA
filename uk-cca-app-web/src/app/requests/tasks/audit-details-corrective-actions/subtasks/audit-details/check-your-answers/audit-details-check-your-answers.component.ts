import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { TaskItemStatus, TasksApiService, toAuditDetailsSummaryData } from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { auditDetailsCorrectiveActionsQuery } from '../../../audit-details-corrective-actions.selectors';
import { createRequestTaskActionProcessDTO } from '../../../transform';
import { AUDIT_DETAILS_SUBTASK } from '../../../types';

@Component({
  selector: 'cca-audit-details-check-your-answers',
  template: `
    <div>
      <netz-page-heading caption="Audit details">Check your answers</netz-page-heading>
      <cca-summary [data]="data()" />
      <button netzPendingButton govukButton type="button" (click)="onSubmit()">Confirm and complete</button>
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [
    PageHeadingComponent,
    ReturnToTaskOrActionPageComponent,
    SummaryComponent,
    ButtonDirective,
    PendingButtonDirective,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AuditDetailsCheckYourAnswersComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly requestTaskStore = inject(RequestTaskStore);
  protected readonly tasksApiService = inject(TasksApiService);

  private readonly taskId = this.activatedRoute.snapshot.params.taskId;

  private readonly auditDetailsAndCorrectiveActions = this.requestTaskStore.select(
    auditDetailsCorrectiveActionsQuery.selectAuditDetailsAndCorrectiveActions,
  );

  private readonly attachments = this.requestTaskStore.select(
    auditDetailsCorrectiveActionsQuery.selectFacilityAuditAttachments,
  );

  protected readonly data = computed(() =>
    toAuditDetailsSummaryData(
      this.auditDetailsAndCorrectiveActions()?.auditDetails,
      this.attachments(),
      this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      generateDownloadUrl(this.taskId),
    ),
  );

  onSubmit() {
    const payload = this.requestTaskStore.select(auditDetailsCorrectiveActionsQuery.selectPayload)();

    const currentSectionsCompleted = this.requestTaskStore.select(
      auditDetailsCorrectiveActionsQuery.selectSectionsCompleted,
    )();

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[AUDIT_DETAILS_SUBTASK] = TaskItemStatus.COMPLETED;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, payload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../../..'], { relativeTo: this.activatedRoute });
    });
  }
}
