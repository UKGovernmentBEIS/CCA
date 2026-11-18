import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ResponsiblePersonFormModel, ResponsiblePersonInputComponent, WizardStepComponent } from '@shared/components';

import { CreateTargetUnitStore } from '../create-target-unit.store';
import {
  TARGET_UNIT_RESPONSIBLE_PERSON_FORM,
  TargetUnitResponsiblePersonFormProvider,
} from './responsible-person-form.provider';

@Component({
  selector: 'cca-responsible-person',
  templateUrl: './responsible-person.component.html',
  imports: [ReactiveFormsModule, WizardStepComponent, ResponsiblePersonInputComponent],
  providers: [TargetUnitResponsiblePersonFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ResponsiblePersonComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly createTargetUnitStore = inject(CreateTargetUnitStore);

  protected readonly form = inject<FormGroup<ResponsiblePersonFormModel>>(TARGET_UNIT_RESPONSIBLE_PERSON_FORM);

  onSubmitResponsiblePerson() {
    const payload = this.form.getRawValue();

    this.createTargetUnitStore.sameAddressWithOperator = payload.sameAddress[0];

    delete payload.sameAddress;

    this.createTargetUnitStore.updateState({ responsiblePerson: { ...payload } });

    if (this.createTargetUnitStore.sameAddressWithResponsiblePerson) {
      this.createTargetUnitStore.sameAddressWithResponsiblePerson = false;

      this.createTargetUnitStore.updateState({
        administrativeContactDetails: {
          ...this.createTargetUnitStore.state.administrativeContactDetails,
          address: null,
        },
      });
    }

    const path = this.createTargetUnitStore.sameAddressWithResponsiblePerson
      ? '../check-your-answers'
      : '../administrative-contact';

    this.router.navigate([path], { relativeTo: this.activatedRoute });
  }
}
