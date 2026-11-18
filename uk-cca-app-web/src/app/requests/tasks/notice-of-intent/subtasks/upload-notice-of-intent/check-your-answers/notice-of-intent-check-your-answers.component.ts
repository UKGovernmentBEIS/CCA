import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import { TaskItemStatus, TasksApiService } from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { noticeOfIntentQuery } from '../../../notice-of-intent.selectors';
import { UPLOAD_NOTICE_OF_INTENT_SUBTASK } from '../../../notice-of-intent.types';
import { toNoticeOfIntentSummaryData } from '../../../notice-of-intent-summary-data';
import { createRequestTaskActionProcessDTO } from '../../../transform';

@Component({
  selector: 'cca-notice-of-intent-check-your-answers',
  template: `
    <div>
      <netz-page-heading caption="Upload notice of intent">Check your answers</netz-page-heading>
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
export default class NoticeOfIntentCheckYourAnswersComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
  protected readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);
  private readonly noticeOfIntent = this.requestTaskStore.select(noticeOfIntentQuery.selectNoticeOfIntent);
  private readonly nonComplianceAttachments = this.requestTaskStore.select(
    noticeOfIntentQuery.selectNonComplianceAttachments,
  );

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  private readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryData = computed(() =>
    toNoticeOfIntentSummaryData(
      this.noticeOfIntent(),
      this.nonComplianceAttachments(),
      this.isEditable(),
      this.downloadUrl,
    ),
  );

  onSubmit() {
    if (!this.isEditable()) {
      return;
    }

    const payload = this.requestTaskStore.select(noticeOfIntentQuery.selectPayload)();

    const currentSectionsCompleted = this.requestTaskStore.select(noticeOfIntentQuery.selectSectionsCompleted)();

    const sectionsCompleted = produce(currentSectionsCompleted ?? {}, (draft) => {
      draft[UPLOAD_NOTICE_OF_INTENT_SUBTASK] = TaskItemStatus.COMPLETED;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, payload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../../..'], { relativeTo: this.activatedRoute, replaceUrl: true });
    });
  }
}
