import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { TextInputComponent, WizardStepComponent } from '@shared/components';
import { transformOperatorType } from '@shared/pipes';

import {
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  ReviewTargetUnitDetailsWizardStep,
} from '../../../underlying-agreement.types';
import {
  TARGET_UNIT_DETAILS_SUBMIT_FORM,
  TargetUnitDetailsSubmitFormModel,
  TargetUnitDetailsSubmitFormProvider,
} from './target-unit-details-submit-form.provider';
@Component({
  selector: 'cca-target-unit-details-submit',
  standalone: true,
  imports: [ReactiveFormsModule, WizardStepComponent, TextInputComponent, ReturnToTaskOrActionPageComponent],
  templateUrl: './target-unit-details-submit.component.html',
  providers: [TargetUnitDetailsSubmitFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TargetUnitDetailsSubmitComponent {
  private readonly taskService = inject(TaskService);
  private readonly activatedRoute: ActivatedRoute = inject(ActivatedRoute);

  protected readonly form = inject<FormGroup<TargetUnitDetailsSubmitFormModel>>(TARGET_UNIT_DETAILS_SUBMIT_FORM);
  protected readonly transformOperatorType = transformOperatorType;

  onSubmit() {
    this.taskService
      .saveSubtask(
        REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
        ReviewTargetUnitDetailsWizardStep.TARGET_UNIT_DETAILS,
        this.activatedRoute,
        this.form.getRawValue(),
      )
      .subscribe();
  }
}
