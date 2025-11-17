import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import { TextareaComponent } from '@netz/govuk-components';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { FinalDecisionTypePipe } from '@shared/pipes';
import { generateDownloadUrl } from '@shared/utils';

import { AdminTerminationFinalDecisionQuery } from '../../../+state/admin-termination-final-decision.selectors';
import {
  ADMIN_TERMINATION_FINAL_DECISION_SUBTASK,
  AdminTerminationFinalDecisionTerminateAgreementWizardStep,
} from '../../../admin-termination-final-decision.helper';
import {
  FINAL_DECISION_REASON_FORM,
  FinalDecisionReasonFormModel,
  FinalDecisionReasonFormProvider,
} from '../final-decision-reason-form.provider';

@Component({
  selector: 'cca-final-decision-reason-details',
  templateUrl: './final-decision-reason-details.component.html',
  imports: [
    WizardStepComponent,
    TextareaComponent,
    ReactiveFormsModule,
    MultipleFileInputComponent,
    FinalDecisionTypePipe,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [FinalDecisionReasonFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class FinalDecisionReasonDetailsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly adminTerminationFinalDecisionTaskService = inject(TaskService);

  protected readonly form = inject<FinalDecisionReasonFormModel>(FINAL_DECISION_REASON_FORM);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly finalDecisionType = this.requestTaskStore.select(
    AdminTerminationFinalDecisionQuery.selectAdminTerminationFinalDecisionReasonDetails,
  )().finalDecisionType;

  onSubmit() {
    this.adminTerminationFinalDecisionTaskService
      .saveSubtask(
        ADMIN_TERMINATION_FINAL_DECISION_SUBTASK,
        AdminTerminationFinalDecisionTerminateAgreementWizardStep.REASON_DETAILS,
        this.activatedRoute,
        {
          ...this.form.getRawValue(),
          finalDecisionType: this.finalDecisionType,
        },
      )
      .subscribe();
  }
}
