import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { DateInputComponent, RadioComponent, RadioOptionComponent, TextareaComponent } from '@netz/govuk-components';
import { TaskItemStatus, TasksApiService } from '@requests/common';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { fileUtils, generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { AuditDetailsCorrectiveActionsSubmitRequestTaskPayload } from 'cca-api';

import { auditDetailsCorrectiveActionsQuery } from '../../../audit-details-corrective-actions.selectors';
import { createRequestTaskActionProcessDTO } from '../../../transform';
import { AUDIT_DETAILS_SUBTASK } from '../../../types';
import {
  AUDIT_DETAILS_FORM,
  AuditDetailsCorrectiveActionsFormModel,
  PreAuditReviewAuditReasonFormProvider,
} from './audit-details-form.provider';

@Component({
  selector: 'cca-audit-details',
  templateUrl: './audit-details.component.html',
  imports: [
    ReactiveFormsModule,
    ReturnToTaskOrActionPageComponent,
    WizardStepComponent,
    TextareaComponent,
    DateInputComponent,
    RadioOptionComponent,
    RadioComponent,
    MultipleFileInputComponent,
  ],
  providers: [PreAuditReviewAuditReasonFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AuditDetailsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly taskId = this.activatedRoute.snapshot.params.taskId;
  protected readonly today = new Date();

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly form = inject<AuditDetailsCorrectiveActionsFormModel>(AUDIT_DETAILS_FORM);

  onSubmit() {
    const payload = this.requestTaskStore.select(auditDetailsCorrectiveActionsQuery.selectPayload)();
    const updatedPayload = update(payload, this.form);

    const currentSectionsCompleted = this.requestTaskStore.select(
      auditDetailsCorrectiveActionsQuery.selectSectionsCompleted,
    )();

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[AUDIT_DETAILS_SUBTASK] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.activatedRoute });
    });
  }
}

function update(
  payload: AuditDetailsCorrectiveActionsSubmitRequestTaskPayload,
  form: AuditDetailsCorrectiveActionsFormModel,
): AuditDetailsCorrectiveActionsSubmitRequestTaskPayload {
  return produce(payload, (draft) => {
    draft.auditDetailsAndCorrectiveActions = {
      ...draft?.auditDetailsAndCorrectiveActions,
      auditDetails: {
        auditTechnique: form.value.auditTechnique,
        auditDate: form.value.auditDate?.toISOString(),
        comments: form.value.comments,
        finalAuditReportDate: form.value.finalAuditReportDate?.toISOString(),
        auditDocuments: fileUtils.toUUIDs(form.value.auditDocuments),
      },
    };
  });
}
