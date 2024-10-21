import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { RadioComponent, RadioOptionComponent } from '@netz/govuk-components';
import { AccountAddressInputComponent, WizardStepComponent } from '@shared/components';

import { BaselineAndTargetPeriodsSubtasks, BaseLineAndTargetsStep } from '../../../underlying-agreement.types';
import {
  BASELINE_EXISTS_FORM,
  BaselineExistsFormModel,
  BaselineExistsFormProvider,
} from './baseline-exists-form.provider';

@Component({
  selector: 'cca-baseline-exists',
  standalone: true,
  imports: [
    AccountAddressInputComponent,
    WizardStepComponent,
    ReactiveFormsModule,
    RadioOptionComponent,
    RadioComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  templateUrl: './baseline-exists.component.html',
  providers: [BaselineExistsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BaselineExistsComponent {
  protected readonly form = inject<BaselineExistsFormModel>(BASELINE_EXISTS_FORM);
  private readonly taskService = inject(TaskService);
  private readonly activatedRoute = inject(ActivatedRoute);

  onSubmit() {
    this.taskService
      .saveSubtask(
        BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS,
        BaseLineAndTargetsStep.BASELINE_EXISTS,
        this.activatedRoute,
        this.form.value,
      )
      .subscribe();
  }
}
