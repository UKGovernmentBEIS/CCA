import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { toReviewTargetUnitDetailsSummaryData, underlyingAgreementQuery } from '@requests/common';
import { PageHeadingComponent, SummaryComponent } from '@shared/components';

@Component({
  selector: 'cca-una-summary-target-unit-details',
  standalone: true,
  imports: [PageHeadingComponent, SummaryComponent, ReturnToTaskOrActionPageComponent],
  templateUrl: './review-target-unit-details-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ReviewTargetUnitDetailsSummaryComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly summaryData = toReviewTargetUnitDetailsSummaryData(
    this.requestTaskStore.select(underlyingAgreementQuery.selectAccountReferenceData)(),
    this.requestTaskStore.select(underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
  );
}
