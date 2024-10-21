import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { TaskService } from '@netz/common/forms';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
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
import { PageHeadingComponent, SummaryComponent, WizardStepComponent } from '@shared/components';

import { UnderlyingAgreementVariationReviewTaskService } from '../../../services/underlying-agreement-variation-review-task.service';

@Component({
  selector: 'cca-una-summary-target-unit-details',
  standalone: true,
  imports: [
    PageHeadingComponent,
    SummaryComponent,
    DecisionComponent,
    ReactiveFormsModule,
    ButtonDirective,
    PendingButtonDirective,
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
  private readonly route = inject(ActivatedRoute);
  readonly form = inject<DecisionFormModel>(DECISION_FORM_PROVIDER);

  protected readonly summaryData = toReviewTargetUnitDetailsUNAReviewSummaryData(
    this.requestTaskStore.select(underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
  );

  submit() {
    (this.taskService as UnderlyingAgreementVariationReviewTaskService)
      .saveDecision(this.form.value, 'TARGET_UNIT_DETAILS', REVIEW_TARGET_UNIT_DETAILS_SUBTASK)
      .subscribe(() => {
        this.router.navigate(['../', ReviewTargetUnitDetailsReviewWizardStep.CHECK_YOUR_ANSWERS], {
          relativeTo: this.route,
        });
      });
  }
}
