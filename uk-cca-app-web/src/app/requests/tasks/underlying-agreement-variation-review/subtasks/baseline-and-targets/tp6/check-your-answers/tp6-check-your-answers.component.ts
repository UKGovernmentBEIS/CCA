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
  templateUrl: './tp6-check-your-answers.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TP6CheckYourAnswersComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly taskService = inject(TaskService);
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  private readonly decision = this.requestTaskStore.select(
    underlyingAgreementReviewQuery.selectSubtaskDecision('TARGET_PERIOD6_DETAILS'),
  )();

  private readonly sectorAssociationDetails = this.requestTaskStore.select(
    underlyingAgreementQuery.selectAccountReferenceDataSectorAssociationDetails,
  )();

  private readonly targetPeriodDetails = this.requestTaskStore.select(
    underlyingAgreementQuery.selectTargetPeriodDetails(false),
  )();

  private readonly originalTargetPeriodDetails = this.requestTaskStore.select(
    underlyingAgreementVariationQuery.selectOriginalTargetPeriodDetails(false),
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
    isTp5Period: false,
    baselineExists: null,
    downloadUrl: generateDownloadUrl(this.taskId),
    isEditable: this.isEditable,
    attachments: { submit: this.submitOriginalAttachments, review: this.reviewAttachments },
  };

  private readonly summaryMetadata = {
    isTp5Period: false,
    baselineExists: null,
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
      .submitSubtask(BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS)
      .subscribe(() => this.router.navigate(['../../..'], { relativeTo: this.activatedRoute, replaceUrl: true }));
  }
}
