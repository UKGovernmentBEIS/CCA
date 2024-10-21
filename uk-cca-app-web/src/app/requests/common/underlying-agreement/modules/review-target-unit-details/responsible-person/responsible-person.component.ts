import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { TaskService } from '@netz/common/forms';
import { WizardStepComponent } from '@shared/components';
import { ResponsiblePersonInputComponent } from '@shared/components/responsible-person-input/responsible-person-input.component';
import { ResponsiblePersonFormModel } from '@shared/components/responsible-person-input/responsible-person-input.controls';

import {
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  ReviewTargetUnitDetailsWizardStep,
} from '../../../underlying-agreement.types';
import {
  UNA_RESPONSIBLE_PERSON_FORM,
  UnaTargetUnitResponsiblePersonFormProvider,
} from './responsible-person-form.provider';
@Component({
  selector: 'cca-responsible-person',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    WizardStepComponent,
    ResponsiblePersonInputComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  templateUrl: './responsible-person.component.html',
  providers: [UnaTargetUnitResponsiblePersonFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ResponsiblePersonComponent {
  protected readonly form = inject<FormGroup<ResponsiblePersonFormModel>>(UNA_RESPONSIBLE_PERSON_FORM);
  private readonly taskService = inject(TaskService);
  private readonly activatedRoute: ActivatedRoute = inject(ActivatedRoute);

  onSubmit() {
    this.taskService
      .saveSubtask(
        REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
        ReviewTargetUnitDetailsWizardStep.RESPONSIBLE_PERSON,
        this.activatedRoute,
        {
          responsiblePersonDetails: {
            address: this.form.getRawValue().address,
            firstName: this.form.value.firstName,
            lastName: this.form.value.lastName,
            email: this.form.value.email,
          },
        },
      )
      .subscribe();
  }
}
