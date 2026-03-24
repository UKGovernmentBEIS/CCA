import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  ConditionalContentDirective,
  RadioComponent,
  RadioOptionComponent,
  TextareaComponent,
} from '@netz/govuk-components';
import { TaskItemStatus, TasksApiService } from '@requests/common';
import { WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { NonComplianceDetails, RequestTaskActionPayload } from 'cca-api';

import { nonComplianceDetailsQuery } from '../non-compliance-details.selectors';
import { NON_COMPLIANCE_DETAILS_SUBTASK, NonComplianceDetailsPayload } from '../types';
import {
  ISSUE_ENFORCEMENT_FORM,
  IssueEnforcementFormModel,
  IssueEnforcementFormProvider,
} from './issue-enforcement-form.provider';

@Component({
  selector: 'cca-issue-enforcement',
  imports: [
    ReactiveFormsModule,
    WizardStepComponent,
    RadioComponent,
    RadioOptionComponent,
    ConditionalContentDirective,
    TextareaComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [IssueEnforcementFormProvider],
  templateUrl: './issue-enforcement.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class IssueEnforcementComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly form = inject<IssueEnforcementFormModel>(ISSUE_ENFORCEMENT_FORM);

  onSubmit() {
    const payload = this.requestTaskStore.select(
      nonComplianceDetailsQuery.selectPayload,
    )() as NonComplianceDetailsPayload;
    const isEnforcementResponseNoticeRequired = this.form.value.isEnforcementResponseNoticeRequired as boolean;

    const nonComplianceDetails: NonComplianceDetails = {
      ...payload.nonComplianceDetails,
      isEnforcementResponseNoticeRequired,
      explanation: isEnforcementResponseNoticeRequired ? null : this.form.value.explanation,
    };

    const currentSectionsCompleted = this.requestTaskStore.select(nonComplianceDetailsQuery.selectSectionsCompleted)();

    const sectionsCompleted = produce(currentSectionsCompleted ?? {}, (draft) => {
      draft[NON_COMPLIANCE_DETAILS_SUBTASK] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();

    const dto = {
      requestTaskId,
      requestTaskActionType: 'NON_COMPLIANCE_DETAILS_SAVE_APPLICATION',
      requestTaskActionPayload: {
        payloadType: 'NON_COMPLIANCE_DETAILS_SAVE_PAYLOAD',
        nonComplianceDetails,
        sectionsCompleted,
      } as RequestTaskActionPayload,
    };

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.activatedRoute });
    });
  }
}
