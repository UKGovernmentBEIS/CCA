import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { RadioComponent, RadioOptionComponent, TextareaComponent } from '@netz/govuk-components';
import { TaskItemStatus, TasksApiService, transformAdminTerminationReason } from '@requests/common';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { fileUtils, generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import {
  AdminTerminationReasonDetails,
  AdminTerminationSaveRequestTaskActionPayload,
  AdminTerminationSubmitRequestTaskPayload,
} from 'cca-api';

import { adminTerminationQuery } from '../../../admin-termination.selectors';
import { createRequestTaskActionProcessDTO } from '../../../transform';
import { REASON_FOR_ADMIN_TERMINATION_SUBTASK } from '../../../types';
import {
  REASON_FOR_ADMIN_TERMINATION_FORM,
  ReasonForAdminTerminationFormModel,
  ReasonForAdminTerminationFormProvider,
} from './reason-for-admin-termination-form.provider';

@Component({
  selector: 'cca-reason-for-admin-termination',
  templateUrl: './reason-for-admin-termination.component.html',
  imports: [
    WizardStepComponent,
    TextareaComponent,
    RadioComponent,
    RadioOptionComponent,
    ReactiveFormsModule,
    MultipleFileInputComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [ReasonForAdminTerminationFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ReasonForAdminTerminationComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly form = inject<ReasonForAdminTerminationFormModel>(REASON_FOR_ADMIN_TERMINATION_FORM);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly reasonForTerminationOptions: {
    text: string;
    hint?: string;
    value: AdminTerminationReasonDetails['reason'];
  }[] = [
    {
      text: transformAdminTerminationReason('DATA_NOT_PROVIDED'),
      value: 'DATA_NOT_PROVIDED',
    },
    { text: transformAdminTerminationReason('NOT_SIGN_AGREEMENT'), value: 'NOT_SIGN_AGREEMENT' },
    { text: transformAdminTerminationReason('SITE_CLOSURE_SCHEME'), value: 'SITE_CLOSURE_SCHEME' },
    { text: transformAdminTerminationReason('TRANSFER_OF_OWNERSHIP'), value: 'TRANSFER_OF_OWNERSHIP' },
    {
      text: transformAdminTerminationReason('FAILURE_TO_COMPLY'),
      hint: '28 day appeal window',
      value: 'FAILURE_TO_COMPLY',
    },
    {
      text: transformAdminTerminationReason('FAILURE_TO_AGREE'),
      hint: '28 day appeal window',
      value: 'FAILURE_TO_AGREE',
    },
    {
      text: transformAdminTerminationReason('FAILURE_TO_PAY'),
      hint: '28 day appeal window',
      value: 'FAILURE_TO_PAY',
    },
  ];

  onSubmit() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as AdminTerminationSubmitRequestTaskPayload;

    const updatedPayload = update(payload, this.form);
    const currentSectionsCompleted = this.requestTaskStore.select(adminTerminationQuery.selectSectionsCompleted)();

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[REASON_FOR_ADMIN_TERMINATION_SUBTASK] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.activatedRoute });
    });
  }
}

function update(
  payload: AdminTerminationSaveRequestTaskActionPayload,
  form: ReasonForAdminTerminationFormModel,
): AdminTerminationSaveRequestTaskActionPayload {
  return produce(payload, (draft) => {
    draft.adminTerminationReasonDetails = {
      explanation: form.controls.explanation.value,
      reason: form.controls.reason.value,
      relevantFiles: fileUtils.toUUIDs(form.controls.relevantFiles.value),
    };
  });
}
