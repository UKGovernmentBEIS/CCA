import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  OVERALL_DECISION_SUBTASK,
  PROVIDE_EVIDENCE_FORM,
  ProvideEvidenceFormModel,
  ProvideEvidenceFormProvider,
  TaskItemStatus,
  TasksApiService,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { fileUtils, generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { UnderlyingAgreementApplySavePayload, UnderlyingAgreementSubmitRequestTaskPayload } from 'cca-api';

import { createSaveActionDTO, toUnderlyingAgreementSaveReviewPayload } from '../../../transform';
import { resetDetermination } from '../../../utils';

@Component({
  selector: 'cca-provide-evidence',
  templateUrl: './provide-evidence.component.html',
  standalone: true,
  imports: [MultipleFileInputComponent, ReactiveFormsModule, WizardStepComponent, ReturnToTaskOrActionPageComponent],
  providers: [ProvideEvidenceFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProvideEvidenceComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');
  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly form = inject<ProvideEvidenceFormModel>(PROVIDE_EVIDENCE_FORM);

  onSubmit() {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementSaveReviewPayload(payload);

    const updatedPayload = update(actionPayload, this.form);
    const determination = resetDetermination(this.store.select(underlyingAgreementReviewQuery.selectDetermination)());

    const reviewSectionsCompleted = produce(
      this.store.select(underlyingAgreementReviewQuery.selectReviewSectionsCompleted)(),
      (draft) => {
        draft[AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK] = TaskItemStatus.UNDECIDED;
        draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
      },
    );

    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const dto = createSaveActionDTO(requestTaskId, updatedPayload, {
      determination,
      reviewSectionsCompleted,
      sectionsCompleted: this.store.select(underlyingAgreementQuery.selectSectionsCompleted)(),
    });

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../decision'], { relativeTo: this.activatedRoute });
    });
  }
}

function update(
  payload: UnderlyingAgreementApplySavePayload,
  form: ProvideEvidenceFormModel,
): UnderlyingAgreementApplySavePayload {
  return produce(payload, (draft) => {
    draft.authorisationAndAdditionalEvidence = {
      authorisationAttachmentIds: fileUtils.toUUIDs(form.get('authorisationAttachmentIds')?.value) as string[],
      additionalEvidenceAttachmentIds: fileUtils.toUUIDs(
        form.get('additionalEvidenceAttachmentIds')?.value,
      ) as string[],
    };
  });
}
