import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  DECISION_FORM_PROVIDER,
  DecisionComponent,
  DecisionFormModel,
  decisionFormProvider,
  ReviewTargetUnitDetailsReviewWizardStep,
  toAuthorisationAdditionalEvidenceSummaryData,
  underlyingAgreementQuery,
} from '@requests/common';
import { SummaryComponent, WizardStepComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils/download-url-generator';

import { UnderlyingAgreementVariationReviewTaskService } from '../../../services/underlying-agreement-variation-review-task.service';

@Component({
  selector: 'cca-authorization-additional-evidence-decision',
  template: `
    <div>
      <netz-page-heading>Authorisation and additional evidence</netz-page-heading>
      <p class="govuk-body">
        Review the evidence that the facilities that make up this target unit have authorised the submission of
        information.
      </p>
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
  providers: [decisionFormProvider('AUTHORISATION_AND_ADDITIONAL_EVIDENCE')],
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
export class AUthorizationAdditionalEvidenceDecisionComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly taskService = inject(TaskService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  readonly form = inject<DecisionFormModel>(DECISION_FORM_PROVIDER);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryData = toAuthorisationAdditionalEvidenceSummaryData(
    this.requestTaskStore.select(underlyingAgreementQuery.selectAuthorisationAndAdditionalEvidence)(),
    this.requestTaskStore.select(underlyingAgreementQuery.selectUnderlyingAgreementSubmitAttachments)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
    this.downloadUrl,
  );

  submit() {
    (this.taskService as UnderlyingAgreementVariationReviewTaskService)
      .saveDecision(this.form.value, 'AUTHORISATION_AND_ADDITIONAL_EVIDENCE', AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK)
      .subscribe(() => {
        this.router.navigate(['../', ReviewTargetUnitDetailsReviewWizardStep.CHECK_YOUR_ANSWERS], {
          relativeTo: this.route,
        });
      });
  }
}
