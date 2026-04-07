import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
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
  NonComplianceNoticeOfIntentSubmitRequestTaskPayload,
  NonComplianceNoticeOfIntentSubmitSaveRequestTaskActionPayload,
} from 'cca-api';

import { noticeOfIntentQuery } from '../../../notice-of-intent.selectors';
import { UPLOAD_NOTICE_OF_INTENT_SUBTASK } from '../../../notice-of-intent.types';
import { createRequestTaskActionProcessDTO } from '../../../transform';
import {
  UPLOAD_NOTICE_OF_INTENT_FORM,
  UploadNoticeOfIntentFormModel,
  UploadNoticeOfIntentFormProvider,
} from './upload-notice-form.provider';

@Component({
  selector: 'cca-upload-notice-of-intent',
  templateUrl: './upload-notice.component.html',
  imports: [
    ReactiveFormsModule,
    WizardStepComponent,
    FileInputComponent,
    TextareaComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [UploadNoticeOfIntentFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class UploadNoticeComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly router = inject(Router);
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly form = inject<UploadNoticeOfIntentFormModel>(UPLOAD_NOTICE_OF_INTENT_FORM);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  private readonly baseDownloadUrl = generateDownloadUrl(this.taskId);

  protected readonly getDownloadUrl = (uuid: string) => `${this.baseDownloadUrl}${uuid}`;

  onSubmit() {
    const payload = this.requestTaskStore.select(noticeOfIntentQuery.selectPayload)();

    const updatedPayload = update({ noticeOfIntent: payload?.noticeOfIntent }, this.form);
    const currentSectionsCompleted = this.requestTaskStore.select(noticeOfIntentQuery.selectSectionsCompleted)();

    const sectionsCompleted = produce(currentSectionsCompleted ?? {}, (draft) => {
      draft[UPLOAD_NOTICE_OF_INTENT_SUBTASK] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.activatedRoute });
    });
  }
}

function update(
  payload: Pick<NonComplianceNoticeOfIntentSubmitRequestTaskPayload, 'noticeOfIntent'>,
  form: UploadNoticeOfIntentFormModel,
): NonComplianceNoticeOfIntentSubmitSaveRequestTaskActionPayload {
  return produce(payload, (draft) => {
    draft.noticeOfIntent = {
      noticeOfIntentFile: form.controls.noticeOfIntentFile.value?.uuid ?? null,
      comments: form.controls.comments.value ?? null,
    };
  });
}
