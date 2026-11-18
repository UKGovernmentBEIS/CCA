import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  ConditionalContentDirective,
  DateInputComponent,
  RadioComponent,
  RadioOptionComponent,
  TextareaComponent,
} from '@netz/govuk-components';
import { TaskItemStatus, TasksApiService } from '@requests/common';
import { WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { NonComplianceConclusion, NonComplianceConclusionDetails, RequestTaskActionPayload } from 'cca-api';

import { nonComplianceConclusionQuery } from '../non-compliance-conclusion.selectors';
import { NON_COMPLIANCE_CONCLUSION_SUBTASK } from '../types';
import {
  PROVIDE_DETAILS_FORM,
  ProvideDetailsFormModel,
  ProvideDetailsFormProvider,
} from './provide-details-form.provider';

@Component({
  selector: 'cca-provide-conclusion-details',
  imports: [
    ReactiveFormsModule,
    WizardStepComponent,
    RadioComponent,
    RadioOptionComponent,
    ConditionalContentDirective,
    DateInputComponent,
    TextareaComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [ProvideDetailsFormProvider],
  templateUrl: './provide-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProvideDetailsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly today = new Date();
  protected readonly form = inject<ProvideDetailsFormModel>(PROVIDE_DETAILS_FORM);

  onSubmit() {
    const payload = this.requestTaskStore.select(nonComplianceConclusionQuery.selectPayload)();

    const { complianceRestored, complianceRestoredDate, penaltyPaid, penaltyPaymentDate, comment, penaltyOutcome } =
      this.form.value;

    const details: NonComplianceConclusionDetails = {
      complianceRestored,
      complianceRestoredDate: complianceRestored ? toIsoString(complianceRestoredDate) : null,
      penaltyPaid,
      penaltyPaymentDate: penaltyPaid ? toIsoString(penaltyPaymentDate) : null,
      comment,
      penaltyOutcome: penaltyOutcome as NonComplianceConclusionDetails['penaltyOutcome'],
    };

    const existingConclusion = payload.nonComplianceConclusion;
    const conclusion: NonComplianceConclusion = {
      ...existingConclusion,
      details,
      withdrawNotice: penaltyOutcome === 'WITHDRAW' ? existingConclusion?.withdrawNotice : null,
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
      if (penaltyOutcome === 'WITHDRAW' && !conclusion.withdrawNotice?.file) {
        this.router.navigate(['../provide-withdrawal-notice'], { relativeTo: this.activatedRoute });
      } else {
        this.router.navigate(['../check-your-answers'], { relativeTo: this.activatedRoute });
      }
    });
  }
}

function toIsoString(date: Date | string | null | undefined): string | null {
  if (!date) return null;
  return typeof date === 'string' ? date : date.toISOString();
}
