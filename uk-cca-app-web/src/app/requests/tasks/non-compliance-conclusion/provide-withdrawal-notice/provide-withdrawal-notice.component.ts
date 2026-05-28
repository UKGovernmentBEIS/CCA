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

import { NonComplianceConclusion, RequestTaskActionPayload } from 'cca-api';

import { nonComplianceConclusionQuery } from '../non-compliance-conclusion.selectors';
import { NON_COMPLIANCE_CONCLUSION_SUBTASK } from '../types';
import {
  PROVIDE_WITHDRAWAL_NOTICE_FORM,
  ProvideWithdrawalNoticeFormModel,
  ProvideWithdrawalNoticeFormProvider,
} from './provide-withdrawal-notice-form.provider';

@Component({
  selector: 'cca-provide-withdrawal-notice',
  imports: [
    ReactiveFormsModule,
    WizardStepComponent,
    FileInputComponent,
    TextareaComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [ProvideWithdrawalNoticeFormProvider],
  templateUrl: './provide-withdrawal-notice.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProvideWithdrawalNoticeComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly form = inject<ProvideWithdrawalNoticeFormModel>(PROVIDE_WITHDRAWAL_NOTICE_FORM);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');
  private readonly baseDownloadUrl = generateDownloadUrl(this.taskId);
  protected readonly getDownloadUrl = (uuid: string) => `${this.baseDownloadUrl}${uuid}`;

  onSubmit() {
    const payload = this.requestTaskStore.select(nonComplianceConclusionQuery.selectPayload)();

    const conclusion: NonComplianceConclusion = {
      ...payload.nonComplianceConclusion,
      withdrawNotice: {
        file: this.form.controls.file.value?.uuid ?? null,
        comments: this.form.controls.comments.value ?? null,
      },
    };

    const sectionsCompleted = produce(
      this.requestTaskStore.select(nonComplianceConclusionQuery.selectSectionsCompleted)() ?? {},
      (draft) => {
        draft[NON_COMPLIANCE_CONCLUSION_SUBTASK] = TaskItemStatus.IN_PROGRESS;
      },
    );

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();

    const dto = {
      requestTaskId,
      requestTaskActionType: 'NON_COMPLIANCE_CONCLUSION_SAVE_APPLICATION',
      requestTaskActionPayload: {
        payloadType: 'NON_COMPLIANCE_CONCLUSION_SAVE_PAYLOAD',
        nonComplianceConclusion: conclusion,
        sectionsCompleted,
      } as RequestTaskActionPayload,
    };

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.activatedRoute });
    });
  }
}
