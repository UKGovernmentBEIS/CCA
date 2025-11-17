import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  areEntitiesIdentical,
  BaselineAndTargetPeriodsSubtasks,
  TaskItemStatus,
  TasksApiService,
  toBaselineAndTargetsSummaryDataWithDecision,
  UNAVariationReviewRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
  underlyingAgreementVariationQuery,
} from '@requests/common';
import { HighlightDiffComponent, SummaryComponent } from '@shared/components';
import { SchemeVersion } from '@shared/types';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { createSaveActionDTO, toUnderlyingAgreementVariationReviewSavePayload } from '../../../transform';

@Component({
  selector: 'cca-check-your-answers',
  templateUrl: './tp5-check-your-answers.component.html',
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

  private readonly originalBaselineExists = this.store.select(
    underlyingAgreementVariationQuery.selectOriginalBaselineExists,
  )();
  private readonly currentBaselineExists = this.store.select(underlyingAgreementQuery.selectTargetPeriodExists)();

  private readonly sectorAssociationDetailsSchemeData = this.store.select(
    underlyingAgreementQuery.selectSectorAssociationDetailsSchemeData(SchemeVersion.CCA_2),
  )();

  private readonly originalTargetPeriodDetails = this.store.select(
    underlyingAgreementVariationQuery.selectOriginalTargetPeriodDetails(true),
  )();
  private readonly currentTargetPeriodDetails = this.store.select(
    underlyingAgreementQuery.selectTargetPeriodDetails(true),
  )();

  private readonly originalSubmitAttachments = this.store.select(
    underlyingAgreementVariationQuery.selectOriginalUnderlyingAgreementAttachments,
  )();
  private readonly currentSubmitAttachments = this.store.select(underlyingAgreementQuery.selectAttachments)();

  private readonly reviewAttachments = this.store.select(underlyingAgreementReviewQuery.selectReviewAttachments)();
  private readonly isEditable = this.store.select(requestTaskQuery.selectIsEditable)();

  private readonly areIdentical = areEntitiesIdentical(
    this.currentTargetPeriodDetails,
    this.originalTargetPeriodDetails,
  );

  private readonly decision = this.store.select(
    underlyingAgreementReviewQuery.selectSubtaskDecision('TARGET_PERIOD5_DETAILS'),
  )();

  private readonly summaryOriginalMetadata = {
    isTp5Period: true,
    baselineExists: this.originalBaselineExists,
    downloadUrl: generateDownloadUrl(this.taskId),
    isEditable: this.isEditable,
    attachments: { submit: this.originalSubmitAttachments, review: this.reviewAttachments },
  };

  private readonly summaryCurrentMetadata = {
    isTp5Period: true,
    baselineExists: this.currentBaselineExists,
    downloadUrl: generateDownloadUrl(this.taskId),
    isEditable: this.isEditable,
    attachments: { submit: this.currentSubmitAttachments, review: this.reviewAttachments },
  };

  protected readonly summaryDataOriginal = toBaselineAndTargetsSummaryDataWithDecision(
    this.sectorAssociationDetailsSchemeData,
    this.originalTargetPeriodDetails,
    this.decision,
    this.summaryOriginalMetadata,
  );

  protected readonly summaryDataCurrent = toBaselineAndTargetsSummaryDataWithDecision(
    this.sectorAssociationDetailsSchemeData,
    this.currentTargetPeriodDetails,
    this.decision,
    this.summaryCurrentMetadata,
  );

  onSubmit() {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationReviewRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationReviewSavePayload(payload);

    const sectionsCompleted = produce(
      this.store.select(underlyingAgreementQuery.selectSectionsCompleted)(),
      (draft) => {
        draft[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS] = TaskItemStatus.COMPLETED;
      },
    );

    const currentReviewSectionsCompleted = this.store.select(
      underlyingAgreementReviewQuery.selectReviewSectionsCompleted,
    )();

    const reviewSectionsCompleted = produce(currentReviewSectionsCompleted, (draft) => {
      draft[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS] = this.areIdentical
        ? TaskItemStatus.UNCHANGED
        : this.decision.type === 'ACCEPTED'
          ? TaskItemStatus.ACCEPTED
          : TaskItemStatus.REJECTED;
    });

    const determination = this.store.select(underlyingAgreementReviewQuery.selectDetermination)();

    const dto = createSaveActionDTO(requestTaskId, actionPayload, {
      sectionsCompleted,
      reviewSectionsCompleted,
      determination: determination,
      reviewGroupDecisions: payload.reviewGroupDecisions,
      facilitiesReviewGroupDecisions: payload.facilitiesReviewGroupDecisions,
    });

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .subscribe(() => this.router.navigate(['../../..'], { relativeTo: this.activatedRoute, replaceUrl: true }));
  }
}
