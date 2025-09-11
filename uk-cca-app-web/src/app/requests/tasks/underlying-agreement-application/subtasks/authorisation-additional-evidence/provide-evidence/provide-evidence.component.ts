import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
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
} from '@requests/common';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { fileUtils, generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { UnderlyingAgreementApplySavePayload, UnderlyingAgreementSubmitRequestTaskPayload } from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementSavePayload } from '../../../transform';

@Component({
  selector: 'cca-provide-evidence',
  templateUrl: './provide-evidence.component.html',
  standalone: true,
  imports: [MultipleFileInputComponent, ReactiveFormsModule, WizardStepComponent, ReturnToTaskOrActionPageComponent],
  providers: [ProvideEvidenceFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProvideEvidenceComponent {
  protected readonly store = inject(RequestTaskStore);
  protected readonly tasksApiService = inject(TasksApiService);
  protected readonly router = inject(Router);
  protected readonly route = inject(ActivatedRoute);

  protected readonly form = inject<ProvideEvidenceFormModel>(PROVIDE_EVIDENCE_FORM);

  private readonly taskId = this.route.snapshot.paramMap.get('taskId');
  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  onSubmit() {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementSavePayload(payload);

    const updatedPayload = update(actionPayload, this.form);

    const currentSectionsCompleted = this.store.select(underlyingAgreementQuery.selectSectionsCompleted)();

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK] = 'IN_PROGRESS';
    });

    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.route });
    });
  }
}

function update(
  payload: UnderlyingAgreementApplySavePayload,
  form: ProvideEvidenceFormModel,
): UnderlyingAgreementApplySavePayload {
  return produce(payload, (draft) => {
    draft.authorisationAndAdditionalEvidence = {
      authorisationAttachmentIds: fileUtils.toUUIDs(form.get('authorisationAttachmentIds')?.value),
      additionalEvidenceAttachmentIds: fileUtils.toUUIDs(form.get('additionalEvidenceAttachmentIds')?.value),
    };
  });
}
