import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  BaselineAndTargetPeriodsSubtasks,
  TaskItemStatus,
  TasksApiService,
  toBaselineAndTargetsSummaryDataWithDecision,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
  underlyingAgreementVariationQuery,
} from '@requests/common';
import { HighlightDiffComponent, SummaryComponent } from '@shared/components';
import { SchemeVersion } from '@shared/types';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { createSaveDecisionActionDTO } from '../../../transform';

@Component({
  selector: 'cca-check-your-answers',
  templateUrl: './tp5-check-your-answers.component.html',
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
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TP5CheckYourAnswersComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly store = inject(RequestTaskStore);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  private readonly decision = this.store.select(
    underlyingAgreementReviewQuery.selectSubtaskDecision('TARGET_PERIOD5_DETAILS'),
  )();

  private readonly baselineExists = this.store.select(underlyingAgreementQuery.selectTargetPeriodExists)();

  private readonly originalBaselineExists = this.store.select(
    underlyingAgreementVariationQuery.selectOriginalBaselineExists,
  )();

  private readonly sectorAssociationDetailsSchemeData = this.store.select(
    underlyingAgreementQuery.selectSectorAssociationDetailsSchemeData(SchemeVersion.CCA_2),
  )();

  private readonly targetPeriodDetails = this.store.select(underlyingAgreementQuery.selectTargetPeriodDetails(true))();

  private readonly originalTargetPeriodDetails = this.store.select(
    underlyingAgreementVariationQuery.selectOriginalTargetPeriodDetails(true),
  )();

  private readonly submitAttachments = this.store.select(underlyingAgreementQuery.selectAttachments)();

  private readonly submitOriginalAttachments = this.store.select(
    underlyingAgreementVariationQuery.selectOriginalUnderlyingAgreementAttachments,
  )();

  private readonly reviewAttachments = this.store.select(underlyingAgreementReviewQuery.selectReviewAttachments)();

  private readonly isEditable = this.store.select(requestTaskQuery.selectIsEditable)();

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
    this.sectorAssociationDetailsSchemeData,
    this.originalTargetPeriodDetails,
    this.decision,
    this.summaryOriginalMetadata,
  );

  protected readonly summaryDataCurrent = toBaselineAndTargetsSummaryDataWithDecision(
    this.sectorAssociationDetailsSchemeData,
    this.targetPeriodDetails,
    this.decision,
    this.summaryMetadata,
  );

  onSubmit() {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const currentReviewSectionsCompleted = this.store.select(
      underlyingAgreementReviewQuery.selectReviewSectionsCompleted,
    )();

    const decision = this.store.select(
      underlyingAgreementReviewQuery.selectSubtaskDecision('TARGET_PERIOD5_DETAILS'),
    )();

    const reviewSectionsCompleted = produce(currentReviewSectionsCompleted, (draft) => {
      draft[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS] =
        decision.type === 'ACCEPTED' ? TaskItemStatus.ACCEPTED : TaskItemStatus.REJECTED;
    });

    const determination = this.store.select(underlyingAgreementReviewQuery.selectDetermination)();

    const dto = createSaveDecisionActionDTO(
      requestTaskId,
      'TARGET_PERIOD5_DETAILS',
      reviewSectionsCompleted,
      decision,
      determination,
    );

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .subscribe(() => this.router.navigate(['../../..'], { relativeTo: this.activatedRoute, replaceUrl: true }));
  }
}
