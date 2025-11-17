import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  toVariationReviewTargetUnitDetailsSummaryDataWithDecision,
  transformAccountReferenceData,
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

  private readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable)();
  private readonly attachments = this.requestTaskStore.select(underlyingAgreementReviewQuery.selectReviewAttachments)();
  private readonly accountReferenceData = this.requestTaskStore.select(
    underlyingAgreementQuery.selectAccountReferenceData,
  )();
  private readonly originalTargetUnitDetails = transformAccountReferenceData(this.accountReferenceData);
  private readonly currentTargetUnitDetails = this.requestTaskStore.select(
    underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails,
  )();

  private readonly decision = this.requestTaskStore.select(
    underlyingAgreementReviewQuery.selectSubtaskDecision('TARGET_UNIT_DETAILS'),
  )();

  protected readonly summaryDataOriginal = toVariationReviewTargetUnitDetailsSummaryDataWithDecision(
    this.originalTargetUnitDetails,
    this.decision,
    this.attachments,
    this.downloadUrl,
    this.isEditable,
  );

  protected readonly summaryDataCurrent = toVariationReviewTargetUnitDetailsSummaryDataWithDecision(
    this.currentTargetUnitDetails,
    this.decision,
    this.attachments,
    this.downloadUrl,
    this.isEditable,
  );
}
