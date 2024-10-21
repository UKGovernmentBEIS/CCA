import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { RadioComponent, RadioOptionComponent, TextareaComponent } from '@netz/govuk-components';
import { transformAdminTerminationReason } from '@requests/common';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils/download-url-generator';

import { AdminTerminationReasonDetails } from 'cca-api';

import {
  REASON_FOR_ADMIN_TERMINATION_SUBTASK,
  ReasonForAdminTerminationWizardStep,
} from '../../admin-termination.types';
import {
  REASON_FOR_ADMIN_TERMINATION_FORM,
  ReasonForAdminTerminationFormModel,
  ReasonForAdminTerminationFormProvider,
} from './reason-for-admin-termination-form.provider';

@Component({
  selector: 'cca-reason-for-admin-termination',
  templateUrl: './reason-for-admin-termination.component.html',
  standalone: true,
  imports: [
    WizardStepComponent,
    TextareaComponent,
    RadioComponent,
    RadioOptionComponent,
    ReactiveFormsModule,
    MultipleFileInputComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [ReasonForAdminTerminationFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ReasonForAdminTerminationComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly adminTerminationTaskService = inject(TaskService);

  protected readonly form = inject<ReasonForAdminTerminationFormModel>(REASON_FOR_ADMIN_TERMINATION_FORM);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly reasonForTerminationOptions: {
    text: string;
    hint?: string;
    value: AdminTerminationReasonDetails['reason'];
  }[] = [
    {
      text: transformAdminTerminationReason('DATA_NOT_PROVIDED'),
      value: 'DATA_NOT_PROVIDED',
    },
    { text: transformAdminTerminationReason('NOT_SIGN_AGREEMENT'), value: 'NOT_SIGN_AGREEMENT' },
    { text: transformAdminTerminationReason('SITE_CLOSURE_SCHEME'), value: 'SITE_CLOSURE_SCHEME' },
    { text: transformAdminTerminationReason('TRANSFER_OF_OWNERSHIP'), value: 'TRANSFER_OF_OWNERSHIP' },
    {
      text: transformAdminTerminationReason('FAILURE_TO_COMPLY'),
      hint: '28 day appeal window',
      value: 'FAILURE_TO_COMPLY',
    },
    {
      text: transformAdminTerminationReason('FAILURE_TO_AGREE'),
      hint: '28 day appeal window',
      value: 'FAILURE_TO_AGREE',
    },
    {
      text: transformAdminTerminationReason('FAILURE_TO_PAY'),
      hint: '28 day appeal window',
      value: 'FAILURE_TO_PAY',
    },
  ];

  onSubmit() {
    this.adminTerminationTaskService
      .saveSubtask(
        REASON_FOR_ADMIN_TERMINATION_SUBTASK,
        ReasonForAdminTerminationWizardStep.REASON_DETAILS,
        this.activatedRoute,
        this.form.getRawValue(),
      )
      .subscribe();
  }
}
