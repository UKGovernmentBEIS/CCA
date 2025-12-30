import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { TextareaComponent } from '@netz/govuk-components';
import { TaskItemStatus, TasksApiService } from '@requests/common';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { fileUtils, generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import {
  AdminTerminationWithdrawSaveRequestTaskActionPayload,
  AdminTerminationWithdrawSubmittedRequestActionPayload,
} from 'cca-api';

import { createRequestTaskActionProcessDTO } from '../../../transform';
import { REASON_FOR_WITHDRAW_ADMIN_TERMINATION_SUBTASK } from '../../../types';
import { adminTerminationWithdrawQuery } from '../../../withdraw-admin-termination.selectors';
import {
  REASON_FOR_WITHDRAW_ADMIN_TERMINATION_FORM,
  ReasonForWithdrawAdminTerminationFormModel,
  ReasonForWithdrawAdminTerminationFormProvider,
} from './reason-for-withdraw-admin-termination-form.provider';

@Component({
  selector: 'cca-reason-for-withdraw-admin-termination',
  templateUrl: './reason-for-withdraw-admin-termination.component.html',
  imports: [
    WizardStepComponent,
    TextareaComponent,
    ReactiveFormsModule,
    MultipleFileInputComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [ReasonForWithdrawAdminTerminationFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ReasonForWithdrawAdminTerminationComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly form = inject<ReasonForWithdrawAdminTerminationFormModel>(
    REASON_FOR_WITHDRAW_ADMIN_TERMINATION_FORM,
  );

  onSubmit() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as AdminTerminationWithdrawSubmittedRequestActionPayload;

    const updatedPayload = update(payload, this.form);
    const currentSectionsCompleted = this.requestTaskStore.select(
      adminTerminationWithdrawQuery.selectSectionsCompleted,
    )();

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[REASON_FOR_WITHDRAW_ADMIN_TERMINATION_SUBTASK] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.activatedRoute });
    });
  }
}

function update(
  payload: AdminTerminationWithdrawSaveRequestTaskActionPayload,
  form: ReasonForWithdrawAdminTerminationFormModel,
): AdminTerminationWithdrawSaveRequestTaskActionPayload {
  return produce(payload, (draft) => {
    draft.adminTerminationWithdrawReasonDetails = {
      explanation: form.controls.explanation.value,
      relevantFiles: fileUtils.toUUIDs(form.controls.relevantFiles.value),
    };
  });
}
