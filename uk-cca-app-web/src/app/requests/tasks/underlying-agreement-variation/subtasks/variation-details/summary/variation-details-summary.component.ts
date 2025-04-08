import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { toVariationDetailsSummaryData, underlyingAgreementVariationQuery } from '@requests/common';
import { SummaryComponent } from '@shared/components';

@Component({
  selector: 'cca-variation-details-summary',
  standalone: true,
  imports: [PageHeadingComponent, SummaryComponent, ReturnToTaskOrActionPageComponent],
  templateUrl: './variation-details-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class VariationDetailsSummaryComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly summaryData = toVariationDetailsSummaryData(
    this.requestTaskStore.select(underlyingAgreementVariationQuery.selectVariationDetails)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
  );
}
