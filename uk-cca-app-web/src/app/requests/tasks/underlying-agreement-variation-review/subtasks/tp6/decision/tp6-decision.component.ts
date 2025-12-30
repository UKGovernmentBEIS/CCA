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

import { createSaveDecisionActionDTO } from '../../../transform';
import { resetDetermination } from '../../../utils';

@Component({
  selector: 'cca-baseline-and-targets-summary',
  templateUrl: './tp6-decision.component.html',
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
  providers: [decisionFormProvider('TARGET_PERIOD6_DETAILS')],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TP6DecisionComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);

  readonly form = inject<DecisionFormModel>(DECISION_FORM_PROVIDER);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  private readonly baselineExists = this.requestTaskStore.select(underlyingAgreementQuery.selectTargetPeriodExists)();

  private readonly originalBaselineExists = this.requestTaskStore.select(
    underlyingAgreementVariationQuery.selectOriginalBaselineExists,
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

  private readonly attachments = this.requestTaskStore.select(underlyingAgreementQuery.selectAttachments)();

  private readonly originalAttachments = this.requestTaskStore.select(
    underlyingAgreementVariationQuery.selectOriginalUnderlyingAgreementAttachments,
  )();

  private readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable)();

  private readonly multipleFilesDownloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryDataOriginal = toBaselineAndTargetsSummaryData(
    false,
    this.originalBaselineExists,
    this.sectorAssociationDetailsSchemeData,
    this.originalTargetPeriodDetails,
    this.originalAttachments,
    this.isEditable,
    this.multipleFilesDownloadUrl,
  );

  protected readonly summaryDataCurrent = toBaselineAndTargetsSummaryData(
    false,
    this.baselineExists,
    this.sectorAssociationDetailsSchemeData,
    this.targetPeriodDetails,
    this.attachments,
    this.isEditable,
    this.multipleFilesDownloadUrl,
  );

  submit() {
    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();

    // Create decision object from form values
    const decision = {
      type: this.form.value.type,
      details: {
        notes: this.form.value.notes,
        files: this.form.value.files.map((file) => file.uuid),
      },
    };

    // Get current state
    const currentReviewSectionsCompleted = this.requestTaskStore.select(
      underlyingAgreementReviewQuery.selectReviewSectionsCompleted,
    )();

    const currDetermination = this.requestTaskStore.select(
      underlyingAgreementVariationReviewQuery.selectDetermination,
    )();

    // Reset determination and sectionsCompleted for variation review
    const determination = resetDetermination(currDetermination);

    const reviewSectionsCompleted = produce(currentReviewSectionsCompleted, (draft) => {
      draft[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS] = TaskItemStatus.UNDECIDED;
      draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
    });

    // Create the payload using the helper function
    const payload = createSaveDecisionActionDTO(
      requestTaskId,
      'TARGET_PERIOD6_DETAILS',
      reviewSectionsCompleted,
      decision,
      determination,
    );

    this.tasksApiService.saveRequestTaskAction(payload).subscribe(() => {
      this.router.navigate(['../', 'check-your-answers'], { relativeTo: this.activatedRoute });
    });
  }
}
