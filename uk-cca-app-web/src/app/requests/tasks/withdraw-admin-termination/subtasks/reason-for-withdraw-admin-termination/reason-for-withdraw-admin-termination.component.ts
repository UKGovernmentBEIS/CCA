import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { TextareaComponent } from '@netz/govuk-components';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

import {
  REASON_FOR_WITHDRAW_ADMIN_TERMINATION_SUBTASK,
  ReasonForWithdrawAdminTerminationWizardStep,
} from '../../withdraw-admin-termination.types';
import {
  REASON_FOR_WITHDRAW_ADMIN_TERMINATION_FORM,
  ReasonForWithdrawAdminTerminationFormModel,
  ReasonForWithdrawAdminTerminationFormProvider,
} from './reason-for-withdraw-admin-termination-form.provider';

@Component({
  selector: 'cca-reason-for-withdraw-admin-termination',
  templateUrl: './reason-for-withdraw-admin-termination.component.html',
  imports: [
    WizardStepComponent,
    TextareaComponent,
    ReactiveFormsModule,
    MultipleFileInputComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [ReasonForWithdrawAdminTerminationFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ReasonForWithdrawAdminTerminationComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly withdrawAdminTerminationTaskService = inject(TaskService);

  protected readonly form = inject<ReasonForWithdrawAdminTerminationFormModel>(
    REASON_FOR_WITHDRAW_ADMIN_TERMINATION_FORM,
  );

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  onSubmit() {
    this.withdrawAdminTerminationTaskService
      .saveSubtask(
        REASON_FOR_WITHDRAW_ADMIN_TERMINATION_SUBTASK,
        ReasonForWithdrawAdminTerminationWizardStep.REASON_DETAILS,
        this.activatedRoute,
        this.form.getRawValue(),
      )
      .subscribe();
  }
}
