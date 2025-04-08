import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  DECISION_FORM_PROVIDER,
  DecisionComponent,
  DecisionFormModel,
  decisionFormProvider,
  ReviewTargetUnitDetailsReviewWizardStep,
  toAuthorisationAdditionalEvidenceSummaryData,
  underlyingAgreementQuery,
  underlyingAgreementVariationQuery,
} from '@requests/common';
import { HighlightDiffComponent, SummaryComponent, WizardStepComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

import { UnderlyingAgreementVariationReviewTaskService } from '../../../services/underlying-agreement-variation-review-task.service';

@Component({
  selector: 'cca-authorization-additional-evidence-decision',
  templateUrl: './authorisation-additional-evidence-decision.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  providers: [decisionFormProvider('AUTHORISATION_AND_ADDITIONAL_EVIDENCE')],
  imports: [
    PageHeadingComponent,
    SummaryComponent,
    ReactiveFormsModule,
    DecisionComponent,
    WizardStepComponent,
    ReturnToTaskOrActionPageComponent,
    HighlightDiffComponent,
    NgTemplateOutlet,
  ],
})
export class AuthorisationAdditionalEvidenceDecisionComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly taskService = inject(TaskService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  readonly form = inject<DecisionFormModel>(DECISION_FORM_PROVIDER);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryDataOriginal = toAuthorisationAdditionalEvidenceSummaryData(
    this.requestTaskStore.select(underlyingAgreementVariationQuery.selectOriginalAuthorisationAndAdditionalEvidence)(),
    this.requestTaskStore.select(underlyingAgreementVariationQuery.selectOriginalUnderlyingAgreementAttachments)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
    this.downloadUrl,
  );

  protected readonly summaryDataCurrent = toAuthorisationAdditionalEvidenceSummaryData(
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
