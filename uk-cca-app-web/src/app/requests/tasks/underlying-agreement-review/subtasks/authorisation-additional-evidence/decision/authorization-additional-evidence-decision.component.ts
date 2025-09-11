import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  DECISION_FORM_PROVIDER,
  DecisionComponent,
  DecisionFormModel,
  decisionFormProvider,
  OVERALL_DECISION_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  toAuthorisationAdditionalEvidenceSummaryData,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { SummaryComponent, WizardStepComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { UnderlyingAgreementReviewDecision } from 'cca-api';

import { createSaveDecisionActionDTO } from '../../../transform';
import { resetDetermination } from '../../../utils';

@Component({
  selector: 'cca-authorization-additional-evidence-decision',
  template: `
    <div>
      <netz-page-heading>Authorisation and additional evidence</netz-page-heading>
      <p>
        Review the evidence that the facilities that make up this target unit have authorised the submission of
        information.
      </p>
      <cca-summary [data]="summaryData" />
      <cca-wizard-step [formGroup]="form" (formSubmit)="submit()">
        <cca-decision />
      </cca-wizard-step>
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
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
  ],
})
export class AuthorizationAdditionalEvidenceDecisionComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly store = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);

  protected readonly form = inject<DecisionFormModel>(DECISION_FORM_PROVIDER);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  private readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryData = toAuthorisationAdditionalEvidenceSummaryData(
    this.store.select(underlyingAgreementQuery.selectAuthorisationAndAdditionalEvidence)(),
    this.store.select(underlyingAgreementQuery.selectUnderlyingAgreementSubmitAttachments)(),
    this.store.select(requestTaskQuery.selectIsEditable)(),
    this.downloadUrl,
  );

  submit() {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const decision: UnderlyingAgreementReviewDecision = {
      type: this.form.value.type,
      details: {
        notes: this.form.value.notes,
        files: this.form.value.files.map((file) => file.uuid),
      },
    };

    const currDetermination = this.store.select(underlyingAgreementReviewQuery.selectDetermination)();
    const determination = resetDetermination(currDetermination);

    const currentReviewSectionsCompleted = this.store.select(
      underlyingAgreementReviewQuery.selectReviewSectionsCompleted,
    )();

    const reviewSectionsCompleted = produce(currentReviewSectionsCompleted, (draft) => {
      draft[AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK] = TaskItemStatus.UNDECIDED;
      draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
    });

    const payload = createSaveDecisionActionDTO(
      requestTaskId,
      'AUTHORISATION_AND_ADDITIONAL_EVIDENCE',
      reviewSectionsCompleted,
      decision,
      determination,
    );

    this.tasksApiService.saveRequestTaskAction(payload).subscribe(() => {
      this.router.navigate(['../', 'check-your-answers'], { relativeTo: this.activatedRoute });
    });
  }
}
