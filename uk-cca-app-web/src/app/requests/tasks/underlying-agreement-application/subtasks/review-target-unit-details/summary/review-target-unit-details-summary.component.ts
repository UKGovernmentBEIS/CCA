import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { toReviewTargetUnitDetailsSummaryData, underlyingAgreementQuery } from '@requests/common';
import { SummaryComponent } from '@shared/components';

@Component({
  selector: 'cca-una-summary-target-unit-details',
  template: `
    <div>
      <netz-page-heading caption="Summary">Target unit details</netz-page-heading>
      <cca-summary [data]="summaryData" />
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  standalone: true,
  imports: [PageHeadingComponent, SummaryComponent, ReturnToTaskOrActionPageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ReviewTargetUnitDetailsSummaryComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly summaryData = toReviewTargetUnitDetailsSummaryData(
    this.requestTaskStore.select(underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
  );
}
