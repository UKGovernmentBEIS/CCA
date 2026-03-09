import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { toVariationDetailsSummaryData, underlyingAgreementVariationRegulatorLedQuery } from '@requests/common';
import { SummaryComponent } from '@shared/components';

@Component({
  selector: 'cca-variation-details-summary',
  template: `
    <div>
      <netz-page-heading caption="Variation details">Summary</netz-page-heading>
      <cca-summary [data]="summaryData" />
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [PageHeadingComponent, SummaryComponent, ReturnToTaskOrActionPageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class VariationDetailsSummaryComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly summaryData = toVariationDetailsSummaryData(
    this.requestTaskStore.select(underlyingAgreementVariationRegulatorLedQuery.selectVariationDetails)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
  );
}
