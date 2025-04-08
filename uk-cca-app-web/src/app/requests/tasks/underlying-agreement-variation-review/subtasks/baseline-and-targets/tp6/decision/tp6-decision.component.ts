import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsReviewStep,
  DECISION_FORM_PROVIDER,
  DecisionComponent,
  DecisionFormModel,
  decisionFormProvider,
  toBaselineAndTargetsSummaryData,
  underlyingAgreementQuery,
  underlyingAgreementVariationQuery,
} from '@requests/common';
import { HighlightDiffComponent, SummaryComponent, WizardStepComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

import { UnderlyingAgreementVariationReviewTaskService } from '../../../../services/underlying-agreement-variation-review-task.service';

@Component({
  selector: 'cca-baseline-and-targets-summary',
  standalone: true,
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
  templateUrl: './tp6-decision.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TP6DecisionComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly taskService = inject(TaskService);
  private readonly router = inject(Router);

  readonly form = inject<DecisionFormModel>(DECISION_FORM_PROVIDER);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  private readonly baselineExists = this.requestTaskStore.select(underlyingAgreementQuery.selectTargetPeriodExists)();

  private readonly originalBaselineExists = this.requestTaskStore.select(
    underlyingAgreementVariationQuery.selectOriginalBaselineExists,
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

  private readonly attachments = this.requestTaskStore.select(underlyingAgreementQuery.selectAttachments)();

  private readonly originalAttachments = this.requestTaskStore.select(
    underlyingAgreementVariationQuery.selectOriginalUnderlyingAgreementAttachments,
  )();

  private readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable)();

  private readonly multipleFilesDownloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryDataOriginal = toBaselineAndTargetsSummaryData(
    false,
    this.originalBaselineExists,
    this.sectorAssociationDetails,
    this.originalTargetPeriodDetails,
    this.originalAttachments,
    this.isEditable,
    this.multipleFilesDownloadUrl,
  );

  protected readonly summaryDataCurrent = toBaselineAndTargetsSummaryData(
    false,
    this.baselineExists,
    this.sectorAssociationDetails,
    this.targetPeriodDetails,
    this.attachments,
    this.isEditable,
    this.multipleFilesDownloadUrl,
  );

  submit() {
    (this.taskService as UnderlyingAgreementVariationReviewTaskService)
      .saveDecision(this.form.value, 'TARGET_PERIOD6_DETAILS', BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS)
      .subscribe(() => {
        this.router.navigate(['../', BaseLineAndTargetsReviewStep.CHECK_YOUR_ANSWERS], {
          relativeTo: this.activatedRoute,
        });
      });
  }
}
