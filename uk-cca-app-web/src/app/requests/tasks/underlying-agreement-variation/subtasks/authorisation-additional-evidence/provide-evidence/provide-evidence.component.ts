import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { RequestTaskStore } from '@netz/common/store';
import { requestTaskQuery } from '@netz/common/store';
import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  PROVIDE_EVIDENCE_FORM,
  ProvideEvidenceFormModel,
  ProvideEvidenceFormProvider,
  TaskItemStatus,
  TasksApiService,
  underlyingAgreementQuery,
} from '@requests/common';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { fileUtils, generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { UnderlyingAgreementVariationSubmitRequestTaskPayload } from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementVariationSavePayload } from '../../../transform';
import { extractReviewProps } from '../../../utils';

@Component({
  selector: 'cca-provide-evidence',
  templateUrl: './provide-evidence.component.html',
  standalone: true,
  imports: [MultipleFileInputComponent, ReactiveFormsModule, WizardStepComponent, ReturnToTaskOrActionPageComponent],
  providers: [ProvideEvidenceFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProvideEvidenceComponent {
  protected readonly form = inject<ProvideEvidenceFormModel>(PROVIDE_EVIDENCE_FORM);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);

  private readonly taskId = this.route.snapshot.paramMap.get('taskId');

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  onSubmit() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementVariationSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationSavePayload(payload);

    // Apply form data to the action payload
    const updatedPayload = produce(actionPayload, (draft) => {
      const formValue = this.form.getRawValue();
      draft.authorisationAndAdditionalEvidence = {
        authorisationAttachmentIds: fileUtils.toUUIDs(formValue.authorisationAttachmentIds) as string[],
        additionalEvidenceAttachmentIds: fileUtils.toUUIDs(formValue.additionalEvidenceAttachmentIds) as string[],
      };
    });

    const currentSectionsCompleted = this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)();

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)() as number;
    const reviewProps = extractReviewProps(this.requestTaskStore);
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted, reviewProps);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.route });
    });
  }
}
