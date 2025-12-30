import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { TextareaComponent } from '@netz/govuk-components';
import { PROVIDE_EVIDENCE_SUBTASK, TaskItemStatus, TasksApiService } from '@requests/common';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { fileUtils, generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import {
  UnderlyingAgreementActivationRequestTaskPayload,
  UnderlyingAgreementActivationSaveRequestTaskActionPayload,
} from 'cca-api';

import { createRequestTaskActionProcessDTO } from '../../../transform';
import { underlyingAgreementActivationQuery } from '../../../una-activation.selectors';
import {
  PROVIDE_EVIDENCE_DETAILS_FORM,
  ProvideEvidenceDetailsFormProvider,
  UnderlyingAgreementActivationDetailsFormModel,
} from './provide-evidence-details-form.provider';

@Component({
  selector: 'cca-provide-evidence-details',
  templateUrl: './provide-evidence-details.component.html',
  imports: [
    WizardStepComponent,
    ReactiveFormsModule,
    TextareaComponent,
    MultipleFileInputComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [ProvideEvidenceDetailsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ProvideEvidenceDetailsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly form = inject<UnderlyingAgreementActivationDetailsFormModel>(PROVIDE_EVIDENCE_DETAILS_FORM);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');
  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  onSubmit() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementActivationRequestTaskPayload;

    const updatedPayload = update(payload, this.form);
    const currentSectionsCompleted = this.requestTaskStore.select(
      underlyingAgreementActivationQuery.selectSectionsCompleted,
    )();

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[PROVIDE_EVIDENCE_SUBTASK] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.activatedRoute });
    });
  }
}

function update(
  payload: UnderlyingAgreementActivationSaveRequestTaskActionPayload,
  form: UnderlyingAgreementActivationDetailsFormModel,
): UnderlyingAgreementActivationSaveRequestTaskActionPayload {
  return produce(payload, (draft) => {
    draft.underlyingAgreementActivationDetails = {
      evidenceFiles: fileUtils.toUUIDs(form.controls.evidenceFiles.value),
      comments: form.controls.comments.value,
    };
  });
}
