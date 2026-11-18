import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { TextareaComponent, WarningTextComponent } from '@netz/govuk-components';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { fileUtils, generateDownloadUrl } from '@shared/utils';

import { NonComplianceCloseRequestTaskActionPayload } from 'cca-api';

import { TasksApiService } from '../tasks-api.service';
import { CLOSE_TASK_FORM, CloseTaskFormModel, CloseTaskFormProvider } from './close-task-form.provider';

@Component({
  selector: 'cca-non-compliance-conclusion-close-task',
  templateUrl: './close-task.component.html',
  imports: [
    ReactiveFormsModule,
    WizardStepComponent,
    TextareaComponent,
    MultipleFileInputComponent,
    WarningTextComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [CloseTaskFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CloseTaskComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);

  protected readonly form = inject<CloseTaskFormModel>(CLOSE_TASK_FORM);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');
  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  onSubmit() {
    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();

    this.tasksApiService
      .saveRequestTaskAction({
        requestTaskId,
        requestTaskActionType: 'NON_COMPLIANCE_CLOSE_APPLICATION',
        requestTaskActionPayload: {
          payloadType: 'NON_COMPLIANCE_CLOSE_TASK_PAYLOAD',
          closeJustification: {
            reason: this.form.controls.reason.value,
            files: fileUtils.toUUIDs(this.form.value.files),
          },
        } as NonComplianceCloseRequestTaskActionPayload,
      })
      .subscribe(() => {
        this.router.navigate(['../close-task-confirmation'], {
          relativeTo: this.activatedRoute,
          replaceUrl: true,
        });
      });
  }
}
