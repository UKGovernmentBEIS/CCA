import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { TextareaComponent } from '@netz/govuk-components';
import { TaskItemStatus, TasksApiService } from '@requests/common';
import { FileInputComponent, WizardStepComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import {
  NonComplianceEnforcementResponseNoticeSaveRequestTaskActionPayload,
  NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload,
} from 'cca-api';

import { enforcementResponseNoticeQuery } from '../../../enforcement-response-notice.selectors';
import { UPLOAD_ENFORCEMENT_RESPONSE_NOTICE_SUBTASK } from '../../../enforcement-response-notice.types';
import { createRequestTaskActionProcessDTO } from '../../../transform';
import {
  UPLOAD_ENFORCEMENT_RESPONSE_NOTICE_FORM,
  UploadEnforcementResponseNoticeFormModel,
  UploadEnforcementResponseNoticeFormProvider,
} from './upload-notice-form.provider';

@Component({
  selector: 'cca-upload-enforcement-response-notice',
  templateUrl: './upload-notice.component.html',
  imports: [
    ReactiveFormsModule,
    WizardStepComponent,
    FileInputComponent,
    TextareaComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [UploadEnforcementResponseNoticeFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class UploadNoticeComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly form = inject<UploadEnforcementResponseNoticeFormModel>(UPLOAD_ENFORCEMENT_RESPONSE_NOTICE_FORM);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  private readonly baseDownloadUrl = generateDownloadUrl(this.taskId);

  protected readonly getDownloadUrl = (uuid: string) => `${this.baseDownloadUrl}${uuid}`;

  private readonly enforcementResponseNotice = this.requestTaskStore.select(
    enforcementResponseNoticeQuery.selectEnforcementResponseNotice,
  );

  protected readonly heading = computed(() =>
    this.enforcementResponseNotice()?.type === 'PENALTY_WAIVER'
      ? 'Upload penalty waiver notice'
      : 'Upload penalty notice',
  );

  private readonly isPenaltyReissue = this.requestTaskStore.select(
    enforcementResponseNoticeQuery.selectIsPenaltyReissue,
  );

  protected readonly showReminder = computed(
    () => this.isPenaltyReissue() || this.enforcementResponseNotice()?.type === 'PENALTY',
  );

  onSubmit() {
    const payload = this.requestTaskStore.select(enforcementResponseNoticeQuery.selectPayload)();
    const updatedPayload = update(payload, this.form);
    const currentSectionsCompleted = this.requestTaskStore.select(
      enforcementResponseNoticeQuery.selectSectionsCompleted,
    )();

    const sectionsCompleted = produce(currentSectionsCompleted ?? {}, (draft) => {
      draft[UPLOAD_ENFORCEMENT_RESPONSE_NOTICE_SUBTASK] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.activatedRoute });
    });
  }
}

function update(
  payload: NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload,
  form: UploadEnforcementResponseNoticeFormModel,
): NonComplianceEnforcementResponseNoticeSaveRequestTaskActionPayload {
  return produce({ enforcementResponseNotice: payload?.enforcementResponseNotice }, (draft) => {
    draft.enforcementResponseNotice = {
      ...draft.enforcementResponseNotice,
      file: form.controls.file.value?.uuid ?? null,
      comments: form.controls.comments.value ?? null,
    };

    if (payload?.penaltyReissue) {
      draft.enforcementResponseNotice.type = 'PENALTY';
    }
  });
}
