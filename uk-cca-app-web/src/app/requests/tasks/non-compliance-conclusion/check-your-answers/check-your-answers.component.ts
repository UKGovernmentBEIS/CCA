import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { TaskItemStatus, TasksApiService, toNonComplianceConclusionSummaryData } from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { RequestTaskActionPayload } from 'cca-api';

import { nonComplianceConclusionQuery } from '../non-compliance-conclusion.selectors';
import { NON_COMPLIANCE_CONCLUSION_SUBTASK } from '../types';

@Component({
  selector: 'cca-non-compliance-conclusion-check-your-answers',
  template: `
    <netz-page-heading caption="Provide conclusion of non-compliance">Check your answers</netz-page-heading>
    <cca-summary [data]="summaryData()" />
    @if (isEditable()) {
      <button netzPendingButton govukButton type="button" (click)="onSubmit()">Confirm and complete</button>
    }
    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [
    SummaryComponent,
    PageHeadingComponent,
    ButtonDirective,
    PendingButtonDirective,
    ReturnToTaskOrActionPageComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckYourAnswersComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);

  private readonly conclusion = this.requestTaskStore.select(
    nonComplianceConclusionQuery.selectNonComplianceConclusion,
  );
  private readonly attachments = this.requestTaskStore.select(nonComplianceConclusionQuery.selectAttachments);
  private readonly taskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId);

  protected readonly summaryData = computed(() =>
    toNonComplianceConclusionSummaryData(
      this.conclusion(),
      this.attachments(),
      this.isEditable(),
      generateDownloadUrl(this.taskId()?.toString()),
    ),
  );

  onSubmit() {
    if (!this.isEditable()) return;

    const payload = this.requestTaskStore.select(nonComplianceConclusionQuery.selectPayload)();

    const sectionsCompleted = produce(
      this.requestTaskStore.select(nonComplianceConclusionQuery.selectSectionsCompleted)() ?? {},
      (draft) => {
        draft[NON_COMPLIANCE_CONCLUSION_SUBTASK] = TaskItemStatus.COMPLETED;
      },
    );

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();

    const dto = {
      requestTaskId,
      requestTaskActionType: 'NON_COMPLIANCE_CONCLUSION_SAVE_APPLICATION',
      requestTaskActionPayload: {
        payloadType: 'NON_COMPLIANCE_CONCLUSION_SAVE_PAYLOAD',
        nonComplianceConclusion: payload.nonComplianceConclusion,
        sectionsCompleted,
      } as RequestTaskActionPayload,
    };

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../../'], { relativeTo: this.activatedRoute, replaceUrl: true });
    });
  }
}
