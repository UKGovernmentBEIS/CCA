import { NgTemplateOutlet } from '@angular/common';
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
  underlyingAgreementVariationQuery,
} from '@requests/common';
import { underlyingAgreementVariationReviewQuery } from '@requests/common';
import { HighlightDiffComponent, SummaryComponent, WizardStepComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

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

      <ng-template #contentTpl let-showOriginal="showOriginal">
        <cca-summary [data]="showOriginal ? summaryDataOriginal : summaryDataCurrent" />
      </ng-template>

      <cca-highlight-diff>
        <ng-container slot="previous" *ngTemplateOutlet="contentTpl; context: { showOriginal: true }" />
        <ng-container slot="current" *ngTemplateOutlet="contentTpl; context: { showOriginal: false }" />
      </cca-highlight-diff>

      <cca-wizard-step [formGroup]="form" (formSubmit)="submit()">
        <cca-decision />
      </cca-wizard-step>
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
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
  providers: [decisionFormProvider('AUTHORISATION_AND_ADDITIONAL_EVIDENCE')],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AuthorisationAdditionalEvidenceDecisionComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly store = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);

  protected readonly form = inject<DecisionFormModel>(DECISION_FORM_PROVIDER);
  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryDataOriginal = toAuthorisationAdditionalEvidenceSummaryData(
    this.store.select(underlyingAgreementVariationQuery.selectOriginalAuthorisationAndAdditionalEvidence)(),
    this.store.select(underlyingAgreementVariationQuery.selectOriginalUnderlyingAgreementAttachments)(),
    this.store.select(requestTaskQuery.selectIsEditable)(),
    this.downloadUrl,
  );

  protected readonly summaryDataCurrent = toAuthorisationAdditionalEvidenceSummaryData(
    this.store.select(underlyingAgreementQuery.selectAuthorisationAndAdditionalEvidence)(),
    this.store.select(underlyingAgreementQuery.selectUnderlyingAgreementSubmitAttachments)(),
    this.store.select(requestTaskQuery.selectIsEditable)(),
    this.downloadUrl,
  );

  submit() {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const currentReviewSectionsCompleted = this.store.select(
      underlyingAgreementReviewQuery.selectReviewSectionsCompleted,
    )();

    const reviewSectionsCompleted = produce(currentReviewSectionsCompleted, (draft) => {
      draft[AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK] = TaskItemStatus.UNDECIDED;
      draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
    });

    const determination = resetDetermination(
      this.store.select(underlyingAgreementVariationReviewQuery.selectDetermination)(),
    );

    const decision = {
      type: this.form.value.type,
      details: {
        notes: this.form.value.notes,
        files: this.form.value.files?.map((f: any) => f.uuid) || [],
      },
    };

    const dto = createSaveDecisionActionDTO(
      requestTaskId,
      'AUTHORISATION_AND_ADDITIONAL_EVIDENCE',
      reviewSectionsCompleted,
      decision,
      determination,
    );

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.activatedRoute });
    });
  }
}
