import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { toNonComplianceConclusionSummaryData } from '@requests/common';
import { SummaryComponent as CcaSummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

import { nonComplianceConclusionQuery } from '../non-compliance-conclusion.selectors';

@Component({
  selector: 'cca-non-compliance-conclusion-summary',
  template: `
    <netz-page-heading caption="Provide conclusion of non-compliance">Summary</netz-page-heading>
    <cca-summary [data]="summaryData()" />
    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [CcaSummaryComponent, PageHeadingComponent, ReturnToTaskOrActionPageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConclusionSummaryComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly conclusion = this.requestTaskStore.select(
    nonComplianceConclusionQuery.selectNonComplianceConclusion,
  );
  private readonly attachments = this.requestTaskStore.select(nonComplianceConclusionQuery.selectAttachments);
  private readonly taskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId);
  private readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);

  protected readonly summaryData = computed(() =>
    toNonComplianceConclusionSummaryData(
      this.conclusion(),
      this.attachments(),
      this.isEditable(),
      generateDownloadUrl(this.taskId()?.toString()),
    ),
  );
}
