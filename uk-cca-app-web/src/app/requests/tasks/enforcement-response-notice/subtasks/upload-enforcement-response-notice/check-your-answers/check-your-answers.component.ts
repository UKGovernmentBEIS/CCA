import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { TaskItemStatus, TasksApiService, toEnforcementResponseNoticeSummaryData } from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { enforcementResponseNoticeQuery } from '../../../enforcement-response-notice.selectors';
import { UPLOAD_ENFORCEMENT_RESPONSE_NOTICE_SUBTASK } from '../../../enforcement-response-notice.types';
import { createRequestTaskActionProcessDTO } from '../../../transform';

@Component({
  selector: 'cca-enforcement-response-notice-check-your-answers',
  template: `
    <div>
      <netz-page-heading caption="Enforcement response notice">Check your answers</netz-page-heading>
      <cca-summary [data]="summaryData()" />
      @if (isEditable()) {
        <button netzPendingButton govukButton type="button" (click)="onSubmit()">Confirm and complete</button>
      }
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [SummaryComponent, PageHeadingComponent, ButtonDirective, ReturnToTaskOrActionPageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class CheckYourAnswersComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);

  protected readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);

  private readonly enforcementResponseNotice = this.requestTaskStore.select(
    enforcementResponseNoticeQuery.selectEnforcementResponseNotice,
  );
  private readonly nonComplianceAttachments = this.requestTaskStore.select(
    enforcementResponseNoticeQuery.selectNonComplianceAttachments,
  );
  private readonly isPenaltyReissue = this.requestTaskStore.select(
    enforcementResponseNoticeQuery.selectIsPenaltyReissue,
  );

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');
  private readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryData = computed(() =>
    toEnforcementResponseNoticeSummaryData(
      this.enforcementResponseNotice(),
      this.nonComplianceAttachments(),
      this.isEditable(),
      this.downloadUrl,
      this.isPenaltyReissue(),
    ),
  );

  onSubmit() {
    if (!this.isEditable()) {
      return;
    }

    const payload = this.requestTaskStore.select(enforcementResponseNoticeQuery.selectPayload)();
    const currentSectionsCompleted = this.requestTaskStore.select(
      enforcementResponseNoticeQuery.selectSectionsCompleted,
    )();

    const sectionsCompleted = produce(currentSectionsCompleted ?? {}, (draft) => {
      draft[UPLOAD_ENFORCEMENT_RESPONSE_NOTICE_SUBTASK] = TaskItemStatus.COMPLETED;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, payload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../../..'], { relativeTo: this.activatedRoute, replaceUrl: true });
    });
  }
}
