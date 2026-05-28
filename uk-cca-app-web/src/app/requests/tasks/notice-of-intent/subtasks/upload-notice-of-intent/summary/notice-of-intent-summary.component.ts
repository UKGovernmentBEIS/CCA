import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { toNoticeOfIntentSummaryData } from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

import { noticeOfIntentQuery } from '../../../notice-of-intent.selectors';

@Component({
  selector: 'cca-notice-of-intent-summary',
  template: `
    <div>
      <netz-page-heading caption="Upload notice of intent">Summary</netz-page-heading>
      <cca-summary [data]="summaryData()" />
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [SummaryComponent, PageHeadingComponent, ReturnToTaskOrActionPageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class NoticeOfIntentSummaryComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);
  private readonly noticeOfIntent = this.requestTaskStore.select(noticeOfIntentQuery.selectNoticeOfIntent);
  private readonly nonComplianceAttachments = this.requestTaskStore.select(
    noticeOfIntentQuery.selectNonComplianceAttachments,
  );

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly summaryData = computed(() =>
    toNoticeOfIntentSummaryData(
      this.noticeOfIntent(),
      this.nonComplianceAttachments(),
      this.isEditable(),
      generateDownloadUrl(this.taskId),
    ),
  );
}
