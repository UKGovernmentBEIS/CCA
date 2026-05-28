import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { toEnforcementResponseNoticeSummaryData } from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

import { enforcementResponseNoticeQuery } from '../../../enforcement-response-notice.selectors';

@Component({
  selector: 'cca-enforcement-response-notice-summary',
  template: `
    <div>
      <netz-page-heading caption="Enforcement response notice">Summary</netz-page-heading>
      <cca-summary [data]="summaryData()" />
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [SummaryComponent, PageHeadingComponent, ReturnToTaskOrActionPageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class SummaryPageComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);
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
}
