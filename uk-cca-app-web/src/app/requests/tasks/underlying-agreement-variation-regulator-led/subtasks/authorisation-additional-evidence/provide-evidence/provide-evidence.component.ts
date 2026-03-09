import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { RequestTaskStore } from '@netz/common/store';
import { requestTaskQuery } from '@netz/common/store';
import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  ProvideEvidenceFormModel,
  TaskItemStatus,
  TasksApiService,
  UNAVariationRegulatorLedRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementVariationRegulatorLedQuery,
} from '@requests/common';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { fileUtils, generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { UnderlyingAgreementVariationRegulatorLedSavePayload } from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnAVariationRegulatorLedSavePayload } from '../../../transform';
import { PROVIDE_EVIDENCE_FORM, ProvideEvidenceFormProvider } from './provide-evidence-form.provider';

@Component({
  selector: 'cca-provide-evidence',
  templateUrl: './provide-evidence.component.html',
  imports: [MultipleFileInputComponent, ReactiveFormsModule, WizardStepComponent, ReturnToTaskOrActionPageComponent],
  providers: [ProvideEvidenceFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProvideEvidenceComponent {
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);

  protected readonly form = inject<ProvideEvidenceFormModel>(PROVIDE_EVIDENCE_FORM);

  private readonly taskId = this.route.snapshot.paramMap.get('taskId');

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  onSubmit() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationRegulatorLedRequestTaskPayload;

    const actionPayload = toUnAVariationRegulatorLedSavePayload(payload);
    const updatedPayload = updateAuthorisationAndAdditionalEvidence(actionPayload, this.form);
    const currentSectionsCompleted = this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)();

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK] = TaskItemStatus.IN_PROGRESS;
    });

    const determination = this.requestTaskStore.select(
      underlyingAgreementVariationRegulatorLedQuery.selectDetermination,
    )();

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)() as number;
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted, determination);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.route });
    });
  }
}

function updateAuthorisationAndAdditionalEvidence(
  payload: UnderlyingAgreementVariationRegulatorLedSavePayload,
  form: ProvideEvidenceFormModel,
): UnderlyingAgreementVariationRegulatorLedSavePayload {
  return produce(payload, (draft) => {
    const formValue = form.getRawValue();

    draft.authorisationAndAdditionalEvidence = {
      authorisationAttachmentIds: fileUtils.toUUIDs(formValue.authorisationAttachmentIds),
      additionalEvidenceAttachmentIds: fileUtils.toUUIDs(formValue.additionalEvidenceAttachmentIds),
    };
  });
}
