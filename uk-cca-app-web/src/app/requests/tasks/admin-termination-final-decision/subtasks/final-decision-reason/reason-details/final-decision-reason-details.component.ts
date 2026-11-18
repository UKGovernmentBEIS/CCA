import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { TextareaComponent } from '@netz/govuk-components';
import { TaskItemStatus, TasksApiService } from '@requests/common';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { FinalDecisionTypePipe } from '@shared/pipes';
import { fileUtils, generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import {
  AdminTerminationFinalDecisionRequestTaskPayload,
  AdminTerminationFinalDecisionSaveRequestTaskActionPayload,
} from 'cca-api';

import { adminTerminationFinalDecisionQuery } from '../../../admin-termination-final-decision.selectors';
import { createRequestTaskActionProcessDTO } from '../../../transform';
import { ADMIN_TERMINATION_FINAL_DECISION_SUBTASK } from '../../../types';
import {
  FINAL_DECISION_REASON_FORM,
  FinalDecisionReasonFormModel,
  FinalDecisionReasonFormProvider,
} from '../final-decision-reason-form.provider';

@Component({
  selector: 'cca-final-decision-reason-details',
  templateUrl: './final-decision-reason-details.component.html',
  imports: [
    WizardStepComponent,
    TextareaComponent,
    ReactiveFormsModule,
    MultipleFileInputComponent,
    FinalDecisionTypePipe,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [FinalDecisionReasonFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class FinalDecisionReasonDetailsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly form = inject<FinalDecisionReasonFormModel>(FINAL_DECISION_REASON_FORM);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly finalDecisionType = this.requestTaskStore.select(
    adminTerminationFinalDecisionQuery.selectReasonDetails,
  )()?.finalDecisionType;

  onSubmit() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as AdminTerminationFinalDecisionRequestTaskPayload;

    const updatedPayload = update(payload, this.form);
    const currentSectionsCompleted = this.requestTaskStore.select(
      adminTerminationFinalDecisionQuery.selectSectionsCompleted,
    )();

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[ADMIN_TERMINATION_FINAL_DECISION_SUBTASK] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.activatedRoute });
    });
  }
}

function update(
  payload: AdminTerminationFinalDecisionSaveRequestTaskActionPayload,
  form: FinalDecisionReasonFormModel,
): AdminTerminationFinalDecisionSaveRequestTaskActionPayload {
  return produce(payload, (draft) => {
    draft.adminTerminationFinalDecisionReasonDetails = {
      ...draft.adminTerminationFinalDecisionReasonDetails,
      explanation: form.value.explanation,
      relevantFiles: fileUtils.toUUIDs(form.value.relevantFiles),
    };
  });
}
