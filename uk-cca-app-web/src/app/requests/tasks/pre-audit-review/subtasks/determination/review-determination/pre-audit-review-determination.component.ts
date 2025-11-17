import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { DateInputComponent, RadioComponent, RadioOptionComponent, TextareaComponent } from '@netz/govuk-components';
import { TaskItemStatus, TasksApiService } from '@requests/common';
import { WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { PreAuditReviewSubmitRequestTaskPayload } from 'cca-api';

import { preAuditReviewQuery } from '../../../pre-audit-review.selectors';
import { createRequestTaskActionProcessDTO } from '../../../transform';
import { PRE_AUDIT_REVIEW_DETERMINATION_SUBTASK } from '../../../types';
import {
  PRE_AUDIT_REVIEW_DETERMINATION_FORM,
  PreAuditReviewAuditReasonFormProvider,
  PreAuditReviewDeterminationFormModel,
} from './pre-audit-review-determination-form.provider';

@Component({
  selector: 'cca-pre-audit-review-determination',
  templateUrl: './pre-audit-review-determination.component.html',
  imports: [
    ReactiveFormsModule,
    ReturnToTaskOrActionPageComponent,
    WizardStepComponent,
    TextareaComponent,
    DateInputComponent,
    RadioOptionComponent,
    RadioComponent,
  ],
  providers: [PreAuditReviewAuditReasonFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PreAuditReviewDeterminationComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly today = new Date();
  protected readonly form = inject<PreAuditReviewDeterminationFormModel>(PRE_AUDIT_REVIEW_DETERMINATION_FORM);

  onSubmit() {
    const payload = this.requestTaskStore.select(preAuditReviewQuery.selectPayload)();
    const updatedPayload = update(payload, this.form);

    const currentSectionsCompleted = this.requestTaskStore.select(preAuditReviewQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[PRE_AUDIT_REVIEW_DETERMINATION_SUBTASK] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.activatedRoute });
    });
  }
}

function update(
  payload: PreAuditReviewSubmitRequestTaskPayload,
  form: PreAuditReviewDeterminationFormModel,
): PreAuditReviewSubmitRequestTaskPayload {
  return produce(payload, (draft) => {
    draft.preAuditReviewDetails = {
      ...draft?.preAuditReviewDetails,
      auditDetermination: {
        reviewCompletionDate: form.controls.reviewCompletionDate.value?.toISOString(),
        furtherAuditNeeded: form.controls.furtherAuditNeeded.value,
        reviewComments: form.controls.reviewComments.value,
      },
    };
  });
}
