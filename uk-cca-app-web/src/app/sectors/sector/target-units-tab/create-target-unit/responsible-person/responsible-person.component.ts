import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { WizardStepComponent } from '@shared/components';
import { ResponsiblePersonInputComponent } from '@shared/components/responsible-person-input/responsible-person-input.component';
import { ResponsiblePersonFormModel } from '@shared/components/responsible-person-input/responsible-person-input.controls';

import { CreateTargetUnitStore } from '../create-target-unit.store';
import {
  TARGET_UNIT_RESPONSIBLE_PERSON_FORM,
  TargetUnitResponsiblePersonFormProvider,
} from './responsible-person-form.provider';

@Component({
  selector: 'cca-responsible-person',
  templateUrl: './responsible-person.component.html',
  standalone: true,
  imports: [ReactiveFormsModule, WizardStepComponent, ResponsiblePersonInputComponent],
  providers: [TargetUnitResponsiblePersonFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ResponsiblePersonComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly store = inject(CreateTargetUnitStore);

  readonly form = inject<FormGroup<ResponsiblePersonFormModel>>(TARGET_UNIT_RESPONSIBLE_PERSON_FORM);

  onSubmitResponsiblePerson() {
    const payload = this.form.getRawValue();
    this.store.sameAddressWithOperator = payload.sameAddress[0];
    delete payload.sameAddress;

    this.store.updateState({ responsiblePerson: { ...payload } });

    if (this.store.sameAddressWithResponsiblePerson) {
      this.store.sameAddressWithResponsiblePerson = false;
      this.store.updateState({
        administrativeContactDetails: {
          ...this.store.state.administrativeContactDetails,
          address: null,
        },
      });
    }

    this.router.navigate(['..', 'administrative-contact'], { relativeTo: this.route });
  }
}
