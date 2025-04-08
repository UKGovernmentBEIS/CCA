import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { TaskService } from '@netz/common/forms';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  BaselineAndTargetPeriodsSubtasks,
  toBaselineAndTargetsSummaryDataWithDecision,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
  underlyingAgreementVariationQuery,
} from '@requests/common';
import { HighlightDiffComponent, SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

@Component({
  selector: 'cca-check-your-answers',
  standalone: true,
  imports: [
    ButtonDirective,
    PageHeadingComponent,
    PendingButtonDirective,
    SummaryComponent,
    ReturnToTaskOrActionPageComponent,
    HighlightDiffComponent,
    NgTemplateOutlet,
  ],
  templateUrl: './tp5-check-your-answers.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TP5CheckYourAnswersComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly taskService = inject(TaskService);
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  private readonly decision = this.requestTaskStore.select(
    underlyingAgreementReviewQuery.selectSubtaskDecision('TARGET_PERIOD5_DETAILS'),
  )();

  private readonly baselineExists = this.requestTaskStore.select(underlyingAgreementQuery.selectTargetPeriodExists)();

  private readonly originalBaselineExists = this.requestTaskStore.select(
    underlyingAgreementVariationQuery.selectOriginalBaselineExists,
  )();

  private readonly sectorAssociationDetails = this.requestTaskStore.select(
    underlyingAgreementQuery.selectAccountReferenceDataSectorAssociationDetails,
  )();

  private readonly targetPeriodDetails = this.requestTaskStore.select(
    underlyingAgreementQuery.selectTargetPeriodDetails(true),
  )();

  private readonly originalTargetPeriodDetails = this.requestTaskStore.select(
    underlyingAgreementVariationQuery.selectOriginalTargetPeriodDetails(true),
  )();

  private readonly submitAttachments = this.requestTaskStore.select(underlyingAgreementQuery.selectAttachments)();

  private readonly submitOriginalAttachments = this.requestTaskStore.select(
    underlyingAgreementVariationQuery.selectOriginalUnderlyingAgreementAttachments,
  )();

  private readonly reviewAttachments = this.requestTaskStore.select(
    underlyingAgreementReviewQuery.selectReviewAttachments,
  )();

  private readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable)();

  private readonly summaryOriginalMetadata = {
    isTp5Period: true,
    baselineExists: this.originalBaselineExists,
    downloadUrl: generateDownloadUrl(this.taskId),
    isEditable: this.isEditable,
    attachments: { submit: this.submitOriginalAttachments, review: this.reviewAttachments },
  };

  private readonly summaryMetadata = {
    isTp5Period: true,
    baselineExists: this.baselineExists,
    downloadUrl: generateDownloadUrl(this.taskId),
    isEditable: this.isEditable,
    attachments: { submit: this.submitAttachments, review: this.reviewAttachments },
  };

  protected readonly summaryDataOriginal = toBaselineAndTargetsSummaryDataWithDecision(
    this.sectorAssociationDetails,
    this.originalTargetPeriodDetails,
    this.decision,
    this.summaryOriginalMetadata,
  );

  protected readonly summaryDataCurrent = toBaselineAndTargetsSummaryDataWithDecision(
    this.sectorAssociationDetails,
    this.targetPeriodDetails,
    this.decision,
    this.summaryMetadata,
  );

  onSubmit() {
    this.taskService
      .submitSubtask(BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS)
      .subscribe(() => this.router.navigate(['../../..'], { relativeTo: this.activatedRoute, replaceUrl: true }));
  }
}
