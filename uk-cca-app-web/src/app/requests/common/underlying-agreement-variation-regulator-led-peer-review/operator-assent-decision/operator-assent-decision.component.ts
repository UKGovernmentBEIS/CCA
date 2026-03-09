import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

import { underlyingAgreementVariationRegulatorLedQuery } from '../../underlying-agreement/+state';
import { toOperatorAssentDecisionSummaryData } from '../../underlying-agreement/summaries';

@Component({
  selector: 'cca-una-variation-regulator-led-peer-review-operator-assent-decision',
  template: `
    <div>
      <netz-page-heading>Determine operator assent</netz-page-heading>
      <cca-summary [data]="summaryData" />
    </div>
    <netz-return-to-task-or-action-page />
  `,
  imports: [PageHeadingComponent, SummaryComponent, ReturnToTaskOrActionPageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorAssentDecisionComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly determination = this.requestTaskStore.select(
    underlyingAgreementVariationRegulatorLedQuery.selectDetermination,
  )();
  private readonly attachments = this.requestTaskStore.select(
    underlyingAgreementVariationRegulatorLedQuery.selectRegulatorLedSubmitAttachments,
  )();

  private readonly downloadUrl = generateDownloadUrl(
    this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)().toString(),
  );

  protected readonly summaryData = toOperatorAssentDecisionSummaryData(
    this.determination,
    this.attachments,
    this.downloadUrl,
    false,
  );
}
