import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  DECISION_FORM_PROVIDER,
  DecisionComponent,
  DecisionFormModel,
  decisionFormProvider,
  ReviewTargetUnitDetailsReviewWizardStep,
  toVariationDetailsSummaryData,
  underlyingAgreementVariationQuery,
  VARIATION_DETAILS_SUBTASK,
} from '@requests/common';
import { PageHeadingComponent, SummaryComponent, WizardStepComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils/download-url-generator';

import { UnderlyingAgreementVariationReviewTaskService } from '../../../services/underlying-agreement-variation-review-task.service';

@Component({
  selector: 'cca-variation-details-decision',
  template: `
    <div>
      <cca-page-heading>Variation details</cca-page-heading>
      <cca-summary [data]="summaryData" />
      <cca-wizard-step [formGroup]="form" (formSubmit)="submit()">
        <cca-decision></cca-decision>
      </cca-wizard-step>
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page></netz-return-to-task-or-action-page>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  providers: [decisionFormProvider('VARIATION_DETAILS')],
  imports: [
    PageHeadingComponent,
    SummaryComponent,
    ReactiveFormsModule,
    DecisionComponent,
    ButtonDirective,
    WizardStepComponent,
    ReturnToTaskOrActionPageComponent,
  ],
})
export class VariationDetailsDecisionComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly taskService = inject(TaskService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  readonly form = inject<DecisionFormModel>(DECISION_FORM_PROVIDER);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryData = toVariationDetailsSummaryData(
    this.requestTaskStore.select(underlyingAgreementVariationQuery.selectVariationDetails)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
  );

  submit() {
    (this.taskService as UnderlyingAgreementVariationReviewTaskService)
      .saveDecision(this.form.value, 'VARIATION_DETAILS', VARIATION_DETAILS_SUBTASK)
      .subscribe(() => {
        this.router.navigate(['../', ReviewTargetUnitDetailsReviewWizardStep.CHECK_YOUR_ANSWERS], {
          relativeTo: this.route,
        });
      });
  }
}
