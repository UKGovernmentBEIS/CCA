import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  toReviewTargetUnitDetailsSummaryDataWithDecision,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { CountryService } from '@shared/services';
import { generateDownloadUrl } from '@shared/utils';

@Component({
  selector: 'cca-una-summary-target-unit-details',
  template: `
    <div>
      <netz-page-heading>Target unit details</netz-page-heading>
      <cca-summary [data]="summaryData" />
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [PageHeadingComponent, SummaryComponent, ReturnToTaskOrActionPageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ReviewTargetUnitDetailsSummaryComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly countries = inject(CountryService).countries;

  private readonly downloadUrl = generateDownloadUrl(
    this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)().toString(),
  );

  private readonly attachments = this.requestTaskStore.select(underlyingAgreementReviewQuery.selectReviewAttachments)();

  protected readonly summaryData = toReviewTargetUnitDetailsSummaryDataWithDecision({
    targetUnitDetails: this.requestTaskStore.select(
      underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails,
    )(),
    decision: this.requestTaskStore.select(
      underlyingAgreementReviewQuery.selectSubtaskDecision('TARGET_UNIT_DETAILS'),
    )(),
    countries: this.countries(),
    attachments: this.attachments,
    downloadUrl: this.downloadUrl,
    isEditable: this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
  });
}
