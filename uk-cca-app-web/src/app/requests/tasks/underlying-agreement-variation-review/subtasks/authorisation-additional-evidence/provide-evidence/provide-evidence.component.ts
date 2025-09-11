import { Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  PROVIDE_EVIDENCE_FORM,
  ProvideEvidenceFormModel,
  ProvideEvidenceFormProvider,
  TasksApiService,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { fileUtils, generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { UnderlyingAgreementVariationReviewSavePayload } from 'cca-api';
import { UnderlyingAgreementVariationReviewRequestTaskPayload } from 'cca-api';

import { createSaveActionDTO, toUnderlyingAgreementVariationReviewSavePayload } from '../../../transform';
import { applySaveActionSideEffects } from '../../../utils';

@Component({
  selector: 'cca-provide-evidence',
  template: `
    <cca-wizard-step
      (formSubmit)="onSubmit()"
      [formGroup]="form"
      caption="Authorisation and additional evidence"
      heading="Provide evidence"
      data-testid="provide-additional-evidence-form"
    >
      <div class="govuk-!-width-two-thirds">
        <cca-multiple-file-input
          [baseDownloadUrl]="downloadUrl"
          [listTitle]="'Uploaded files'"
          label="Authorisation"
          hint="Upload evidence that the operator of all the facilities in the target unit has authorised the sector to
      submit this application on their behalf."
          formControlName="authorisationAttachmentIds"
        />

        <cca-multiple-file-input
          [baseDownloadUrl]="downloadUrl"
          [listTitle]="'Uploaded files'"
          label="Additional evidence (optional)"
          hint="Upload any additional evidence or correspondence not specifically requested
      in other parts of the application that will assist the Environment Agency in
      determining the application."
          formControlName="additionalEvidenceAttachmentIds"
        />
      </div>
    </cca-wizard-step>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  standalone: true,
  imports: [WizardStepComponent, ReactiveFormsModule, ReturnToTaskOrActionPageComponent, MultipleFileInputComponent],
  providers: [ProvideEvidenceFormProvider],
})
export class ProvideEvidenceComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly taskId = this.activatedRoute.snapshot.parent?.parent?.parent?.paramMap.get('taskId');

  protected readonly payload = this.store.select(
    requestTaskQuery.selectRequestTaskPayload,
  )() as UnderlyingAgreementVariationReviewRequestTaskPayload;

  protected readonly form = inject<ProvideEvidenceFormModel>(PROVIDE_EVIDENCE_FORM);

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  onSubmit() {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const currentPayload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementVariationReviewRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationReviewSavePayload(currentPayload);

    // Update the payload with form values
    const updatedPayload = update(actionPayload, this.form);

    const { determination, reviewSectionsCompleted, sectionsCompleted } = applySaveActionSideEffects(
      this.store.select(underlyingAgreementReviewQuery.selectDetermination)(),
      this.store.select(underlyingAgreementReviewQuery.selectReviewSectionsCompleted)(),
      this.store.select(underlyingAgreementQuery.selectSectionsCompleted)(),
      AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
    );

    const dto = createSaveActionDTO(requestTaskId, updatedPayload, {
      sectionsCompleted,
      reviewSectionsCompleted,
      determination,
    });

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../decision'], { relativeTo: this.activatedRoute });
    });
  }
}

function update(
  payload: UnderlyingAgreementVariationReviewSavePayload,
  form: ProvideEvidenceFormModel,
): UnderlyingAgreementVariationReviewSavePayload {
  return produce(payload, (draft) => {
    draft.authorisationAndAdditionalEvidence = {
      authorisationAttachmentIds: fileUtils.toUUIDs(form.get('authorisationAttachmentIds')?.value),
      additionalEvidenceAttachmentIds: fileUtils.toUUIDs(form.get('additionalEvidenceAttachmentIds')?.value),
    };
  });
}
