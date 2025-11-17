import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { AccountAddressFormModel, AccountAddressInputComponent, WizardStepComponent } from '@shared/components';

import { CreateTargetUnitStore } from '../create-target-unit.store';
import {
  TARGET_UNIT_OPERATOR_ADDRESS_FORM,
  TargetUnitOperatorAddressFormProvider,
} from './operator-address-form.provider';

@Component({
  selector: 'cca-operator-address',
  templateUrl: './operator-address.component.html',
  imports: [ReactiveFormsModule, AccountAddressInputComponent, WizardStepComponent],
  providers: [TargetUnitOperatorAddressFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorAddressComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly createTargetUnitStore = inject(CreateTargetUnitStore);

  protected readonly form = inject<FormGroup<AccountAddressFormModel>>(TARGET_UNIT_OPERATOR_ADDRESS_FORM);

  onSubmitOperatorAddress() {
    this.createTargetUnitStore.updateState({ address: { ...this.form.getRawValue() } });

    if (this.createTargetUnitStore.sameAddressWithOperator) {
      this.createTargetUnitStore.sameAddressWithOperator = false;

      this.createTargetUnitStore.updateState({
        responsiblePerson: { ...this.createTargetUnitStore.state.responsiblePerson, address: null },
      });

      if (this.createTargetUnitStore.sameAddressWithResponsiblePerson) {
        this.createTargetUnitStore.sameAddressWithResponsiblePerson = false;

        this.createTargetUnitStore.updateState({
          administrativeContactDetails: {
            ...this.createTargetUnitStore.state.administrativeContactDetails,
            address: null,
          },
        });
      }
    }

    this.router.navigate(['..', 'responsible-person'], { relativeTo: this.activatedRoute });
  }
}
