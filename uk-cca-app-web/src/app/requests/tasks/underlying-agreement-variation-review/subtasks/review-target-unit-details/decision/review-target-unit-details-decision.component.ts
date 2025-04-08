import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  DECISION_FORM_PROVIDER,
  DecisionComponent,
  DecisionFormModel,
  decisionFormProvider,
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  ReviewTargetUnitDetailsReviewWizardStep,
  toReviewTargetUnitDetailsUNAReviewSummaryData,
  transform,
  underlyingAgreementQuery,
} from '@requests/common';
import { HighlightDiffComponent, SummaryComponent, WizardStepComponent } from '@shared/components';

import { UnderlyingAgreementVariationReviewTaskService } from '../../../services/underlying-agreement-variation-review-task.service';

@Component({
  selector: 'cca-una-summary-target-unit-details',
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
  providers: [decisionFormProvider('TARGET_UNIT_DETAILS')],
  templateUrl: './review-target-unit-details-decision.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ReviewTargetUnitDetailsDecisionComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly taskService = inject(TaskService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly form = inject<DecisionFormModel>(DECISION_FORM_PROVIDER);

  private readonly accountReferenceData = this.requestTaskStore.select(
    underlyingAgreementQuery.selectAccountReferenceData,
  );

  private readonly originalTargetUnitDetails = transform(this.accountReferenceData());

  protected readonly summaryDataOriginal = toReviewTargetUnitDetailsUNAReviewSummaryData(
    this.originalTargetUnitDetails,
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
  );

  protected readonly summaryDataCurrent = toReviewTargetUnitDetailsUNAReviewSummaryData(
    this.requestTaskStore.select(underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
  );

  submit() {
    (this.taskService as UnderlyingAgreementVariationReviewTaskService)
      .saveDecision(this.form.value, 'TARGET_UNIT_DETAILS', REVIEW_TARGET_UNIT_DETAILS_SUBTASK)
      .subscribe(() => {
        this.router.navigate(['../', ReviewTargetUnitDetailsReviewWizardStep.CHECK_YOUR_ANSWERS], {
          relativeTo: this.activatedRoute,
        });
      });
  }
}
