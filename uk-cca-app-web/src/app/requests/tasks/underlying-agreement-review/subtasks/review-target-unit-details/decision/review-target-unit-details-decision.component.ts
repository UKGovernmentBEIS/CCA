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
  underlyingAgreementQuery,
} from '@requests/common';
import { SummaryComponent, WizardStepComponent } from '@shared/components';

import { UnderlyingAgreementReviewTaskService } from '../../../services/underlying-agreement-review-task.service';

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

  protected readonly summaryData = toReviewTargetUnitDetailsUNAReviewSummaryData(
    this.requestTaskStore.select(underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
  );

  submit() {
    (this.taskService as UnderlyingAgreementReviewTaskService)
      .saveDecision(this.form.value, 'TARGET_UNIT_DETAILS', REVIEW_TARGET_UNIT_DETAILS_SUBTASK)
      .subscribe(() => {
        this.router.navigate(['../', ReviewTargetUnitDetailsReviewWizardStep.CHECK_YOUR_ANSWERS], {
          relativeTo: this.activatedRoute,
        });
      });
  }
}
