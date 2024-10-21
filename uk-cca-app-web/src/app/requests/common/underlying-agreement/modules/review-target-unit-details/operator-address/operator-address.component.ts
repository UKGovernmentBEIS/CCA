import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { AccountAddressFormModel, AccountAddressInputComponent, WizardStepComponent } from '@shared/components';

import {
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  ReviewTargetUnitDetailsWizardStep,
} from '../../../underlying-agreement.types';
import { UNA_OPERATOR_ADDRESS_FORM, UnaOperatorAddressFormProvider } from './una-operator-address-form.provider';

@Component({
  selector: 'cca-operator-address',
  standalone: true,
  imports: [ReactiveFormsModule, WizardStepComponent, AccountAddressInputComponent, ReturnToTaskOrActionPageComponent],
  templateUrl: './operator-address.component.html',
  providers: [UnaOperatorAddressFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorAddressComponent {
  protected readonly form = inject<FormGroup<AccountAddressFormModel>>(UNA_OPERATOR_ADDRESS_FORM);
  private readonly taskService = inject(TaskService);
  private readonly activatedRoute: ActivatedRoute = inject(ActivatedRoute);

  onSubmit() {
    this.taskService
      .saveSubtask(
        REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
        ReviewTargetUnitDetailsWizardStep.OPERATOR_ADDRESS,
        this.activatedRoute,
        {
          operatorAddress: this.form.value,
        },
      )
      .subscribe();
  }
}
