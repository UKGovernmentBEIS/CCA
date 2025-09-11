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
  templateUrl: './tp6-check-your-answers.component.html',
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
export class TP6CheckYourAnswersComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  private readonly decision = this.requestTaskStore.select(
    underlyingAgreementReviewQuery.selectSubtaskDecision('TARGET_PERIOD6_DETAILS'),
  )();

  private readonly sectorAssociationDetailsSchemeData = this.requestTaskStore.select(
    underlyingAgreementQuery.selectSectorAssociationDetailsSchemeData(SchemeVersion.CCA_2),
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
    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();

    const currentReviewSectionsCompleted = this.requestTaskStore.select(
      underlyingAgreementReviewQuery.selectReviewSectionsCompleted,
    )();

    const decision = this.requestTaskStore.select(
      underlyingAgreementReviewQuery.selectSubtaskDecision('TARGET_PERIOD6_DETAILS'),
    )();

    const reviewSectionsCompleted = produce(currentReviewSectionsCompleted, (draft) => {
      draft[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS] =
        decision.type === 'ACCEPTED' ? TaskItemStatus.ACCEPTED : TaskItemStatus.REJECTED;
    });

    const determination = this.requestTaskStore.select(underlyingAgreementReviewQuery.selectDetermination)();

    const dto = createSaveDecisionActionDTO(
      requestTaskId,
      'TARGET_PERIOD6_DETAILS',
      reviewSectionsCompleted,
      decision,
      determination,
    );

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .subscribe(() => this.router.navigate(['../../..'], { relativeTo: this.activatedRoute, replaceUrl: true }));
  }
}
