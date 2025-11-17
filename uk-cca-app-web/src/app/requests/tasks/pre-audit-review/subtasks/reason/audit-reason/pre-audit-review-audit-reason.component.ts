import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { CheckboxComponent, CheckboxesComponent, TextareaComponent } from '@netz/govuk-components';
import { TaskItemStatus, TasksApiService } from '@requests/common';
import { WizardStepComponent } from '@shared/components';
import { FacilityAuditReasonPipe } from '@shared/pipes';
import { produce } from 'immer';

import { AuditReasonDetails, PreAuditReviewSubmitRequestTaskPayload } from 'cca-api';

import { preAuditReviewQuery } from '../../../pre-audit-review.selectors';
import { createRequestTaskActionProcessDTO } from '../../../transform';
import { PRE_AUDIT_REVIEW_AUDIT_REASON_SUBTASK } from '../../../types';
import {
  PRE_AUDIT_REVIEW_AUDIT_REASON_FORM,
  PreAuditReviewAuditReasonFormModel,
  PreAuditReviewAuditReasonFormProvider,
} from './pre-audit-review-audit-reason-form.provider';

@Component({
  selector: 'cca-pre-audit-review-audit-reason',
  templateUrl: './pre-audit-review-audit-reason.component.html',
  imports: [
    ReactiveFormsModule,
    ReturnToTaskOrActionPageComponent,
    WizardStepComponent,
    CheckboxComponent,
    CheckboxesComponent,
    TextareaComponent,
    FacilityAuditReasonPipe,
  ],
  providers: [PreAuditReviewAuditReasonFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PreAuditReviewAuditReasonComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly requestTaskStore = inject(RequestTaskStore);
  protected readonly tasksApiService = inject(TasksApiService);

  protected readonly reasonOptions: AuditReasonDetails['reasonsForAudit'] = [
    'ELIGIBILITY',
    'SEVENTY_RULE_EVALUATION',
    'BASE_YEAR_DATA',
    'REPORTING_DATA',
    'NON_COMPLIANCE',
    'OTHER',
  ];

  protected readonly form = inject<FormGroup<PreAuditReviewAuditReasonFormModel>>(PRE_AUDIT_REVIEW_AUDIT_REASON_FORM);

  onSubmit() {
    const payload = this.requestTaskStore.select(preAuditReviewQuery.selectPayload)();
    const updatedPayload = update(payload, this.form);

    const currentSectionsCompleted = this.requestTaskStore.select(preAuditReviewQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[PRE_AUDIT_REVIEW_AUDIT_REASON_SUBTASK] = TaskItemStatus.IN_PROGRESS;
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
  form: FormGroup<PreAuditReviewAuditReasonFormModel>,
): PreAuditReviewSubmitRequestTaskPayload {
  return produce(payload, (draft) => {
    draft.preAuditReviewDetails = {
      ...draft?.preAuditReviewDetails,
      auditReasonDetails: {
        ...draft?.preAuditReviewDetails?.auditReasonDetails,
        reasonsForAudit: form.value.reasonsForAudit,
        comment: form.value.comment,
      },
    };
  });
}
