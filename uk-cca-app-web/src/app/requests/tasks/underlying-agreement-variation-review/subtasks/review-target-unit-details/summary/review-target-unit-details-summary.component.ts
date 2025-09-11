import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  toReviewTargetUnitDetailsSummaryDataWithDecision,
  transform,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { HighlightDiffComponent, SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

@Component({
  selector: 'cca-una-summary-target-unit-details',
  template: `
    <div>
      <netz-page-heading>Target unit details</netz-page-heading>

      <ng-template #contentTpl let-showOriginal="showOriginal">
        <cca-summary [data]="showOriginal ? summaryDataOriginal : summaryDataCurrent" />
      </ng-template>

      <cca-highlight-diff>
        <ng-container slot="previous" *ngTemplateOutlet="contentTpl; context: { showOriginal: true }" />
        <ng-container slot="current" *ngTemplateOutlet="contentTpl; context: { showOriginal: false }" />
      </cca-highlight-diff>
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  standalone: true,
  imports: [
    PageHeadingComponent,
    SummaryComponent,
    ReturnToTaskOrActionPageComponent,
    HighlightDiffComponent,
    NgTemplateOutlet,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewTargetUnitDetailsSummaryComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly downloadUrl = generateDownloadUrl(
    this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)().toString(),
  );

  private readonly attachments = this.requestTaskStore.select(underlyingAgreementReviewQuery.selectReviewAttachments)();

  private readonly accountReferenceData = this.requestTaskStore.select(
    underlyingAgreementQuery.selectAccountReferenceData,
  );

  private readonly originalTargetUnitDetails = transform(this.accountReferenceData());

  protected readonly summaryDataOriginal = toReviewTargetUnitDetailsSummaryDataWithDecision(
    this.originalTargetUnitDetails,
    this.requestTaskStore.select(underlyingAgreementReviewQuery.selectSubtaskDecision('TARGET_UNIT_DETAILS'))(),
    this.attachments,
    this.downloadUrl,
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
  );

  protected readonly summaryDataCurrent = toReviewTargetUnitDetailsSummaryDataWithDecision(
    this.requestTaskStore.select(underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails)(),
    this.requestTaskStore.select(underlyingAgreementReviewQuery.selectSubtaskDecision('TARGET_UNIT_DETAILS'))(),
    this.attachments,
    this.downloadUrl,
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
  );
}
