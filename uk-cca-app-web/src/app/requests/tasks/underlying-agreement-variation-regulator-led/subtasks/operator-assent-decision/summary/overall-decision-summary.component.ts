import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { toOperatorAssentDecisionSummaryData, underlyingAgreementVariationRegulatorLedQuery } from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

@Component({
  selector: 'cca-overall-decision-summary',
  template: `
    <div>
      <netz-page-heading caption="Determine operator assent">Summary</netz-page-heading>
      <cca-summary [data]="summaryData()" />
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [SummaryComponent, PageHeadingComponent, ReturnToTaskOrActionPageComponent],
})
export class OverallDecisionSummaryComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly determination = this.requestTaskStore.select(
    underlyingAgreementVariationRegulatorLedQuery.selectDetermination,
  );

  private readonly attachments = this.requestTaskStore.select(
    underlyingAgreementVariationRegulatorLedQuery.selectRegulatorLedSubmitAttachments,
  );

  private readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);

  private readonly downloadUrl = computed(() =>
    generateDownloadUrl(this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)().toString()),
  );

  protected readonly summaryData = computed(() =>
    toOperatorAssentDecisionSummaryData({
      determination: this.determination(),
      attachments: this.attachments(),
      downloadUrl: this.downloadUrl(),
      isEditable: this.isEditable(),
    }),
  );
}
