import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  BaselineAndTargetPeriodsSubtasks,
  DECISION_FORM_PROVIDER,
  DecisionComponent,
  DecisionFormModel,
  decisionFormProvider,
  OVERALL_DECISION_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  toBaselineAndTargetsSummaryData,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
  underlyingAgreementVariationQuery,
} from '@requests/common';
import { underlyingAgreementVariationReviewQuery } from '@requests/common';
import { HighlightDiffComponent, SummaryComponent, WizardStepComponent } from '@shared/components';
import { SchemeVersion } from '@shared/types';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { UnderlyingAgreementReviewDecision } from 'cca-api';

import { createSaveDecisionActionDTO } from '../../../transform';
import { resetDetermination } from '../../../utils';

@Component({
  selector: 'cca-baseline-and-targets-summary',
  templateUrl: './tp5-decision.component.html',
  imports: [
    PageHeadingComponent,
    SummaryComponent,
    DecisionComponent,
    ReactiveFormsModule,
    WizardStepComponent,
    ReturnToTaskOrActionPageComponent,
    HighlightDiffComponent,
    NgTemplateOutlet,
  ],
  providers: [decisionFormProvider('TARGET_PERIOD5_DETAILS')],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TP5DecisionComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly store = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);

  protected readonly form = inject<DecisionFormModel>(DECISION_FORM_PROVIDER);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

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

  private readonly attachments = this.store.select(underlyingAgreementQuery.selectAttachments)();

  private readonly originalAttachments = this.store.select(
    underlyingAgreementVariationQuery.selectOriginalUnderlyingAgreementAttachments,
  )();

  private readonly isEditable = this.store.select(requestTaskQuery.selectIsEditable)();

  private readonly multipleFilesDownloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryDataOriginal = toBaselineAndTargetsSummaryData(
    true,
    this.originalBaselineExists,
    this.sectorAssociationDetailsSchemeData,
    this.originalTargetPeriodDetails,
    this.originalAttachments,
    this.isEditable,
    this.multipleFilesDownloadUrl,
  );

  protected readonly summaryDataCurrent = toBaselineAndTargetsSummaryData(
    true,
    this.baselineExists,
    this.sectorAssociationDetailsSchemeData,
    this.targetPeriodDetails,
    this.attachments,
    this.isEditable,
    this.multipleFilesDownloadUrl,
  );

  submit() {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const decision: UnderlyingAgreementReviewDecision = {
      type: this.form.value.type,
      details: {
        notes: this.form.value.notes,
        files: this.form.value.files.map((f) => f.uuid),
      },
    };

    const determination = resetDetermination(
      this.store.select(underlyingAgreementVariationReviewQuery.selectDetermination)(),
    );

    const reviewSectionsCompleted = produce(
      this.store.select(underlyingAgreementReviewQuery.selectReviewSectionsCompleted)(),
      (draft) => {
        draft[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS] = TaskItemStatus.UNDECIDED;
        draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
      },
    );

    const payload = createSaveDecisionActionDTO(
      requestTaskId,
      'TARGET_PERIOD5_DETAILS',
      reviewSectionsCompleted,
      decision,
      determination,
    );

    this.tasksApiService.saveRequestTaskAction(payload).subscribe(() => {
      this.router.navigate(['../', 'check-your-answers'], { relativeTo: this.activatedRoute });
    });
  }
}
