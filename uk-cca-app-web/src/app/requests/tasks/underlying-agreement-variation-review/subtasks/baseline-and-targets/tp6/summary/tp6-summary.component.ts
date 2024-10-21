import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  toBaselineAndTargetsSummaryDataWithDecision,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { PageHeadingComponent, SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils/download-url-generator';

@Component({
  selector: 'cca-baseline-and-targets-summary',
  standalone: true,
  imports: [PageHeadingComponent, SummaryComponent, ReturnToTaskOrActionPageComponent],
  template: `
    <div>
      <cca-page-heading caption="TP6 (2024)">Summary</cca-page-heading>

      <cca-summary [data]="summaryData" />
    </div>

    <netz-return-to-task-or-action-page></netz-return-to-task-or-action-page>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TP6SummaryComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly store = inject(RequestTaskStore);
  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');
  private readonly decision = this.store.select(
    underlyingAgreementReviewQuery.selectSubtaskDecision('TARGET_PERIOD6_DETAILS'),
  )();

  protected readonly sectorAssociationDetails = this.store.select(
    underlyingAgreementQuery.selectAccountReferenceDataSectorAssociationDetails,
  )();

  protected readonly targetPeriodDetails = this.store.select(
    underlyingAgreementQuery.selectTargetPeriodDetails(false),
  )();

  protected attachments = this.store.select(underlyingAgreementQuery.selectAttachments)();
  protected submitAttachments = this.store.select(underlyingAgreementQuery.selectAttachments)();
  protected reviewAttachments = this.store.select(underlyingAgreementReviewQuery.selectReviewAttachments)();

  protected isEditable = this.store.select(requestTaskQuery.selectIsEditable)();

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  private readonly summaryMetadata = {
    isTp5Period: false,
    baselineExists: null,
    downloadUrl: generateDownloadUrl(this.taskId),
    isEditable: this.isEditable,
    attachments: { submit: this.submitAttachments, review: this.reviewAttachments },
  };

  protected readonly summaryData = toBaselineAndTargetsSummaryDataWithDecision(
    this.sectorAssociationDetails,
    this.targetPeriodDetails,
    this.decision,
    this.summaryMetadata,
  );
}
