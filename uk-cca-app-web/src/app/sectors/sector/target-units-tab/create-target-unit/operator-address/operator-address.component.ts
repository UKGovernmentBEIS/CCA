import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { SelectComponent } from '@netz/govuk-components';
import { AccountAddressFormModel, AccountAddressInputComponent, WizardStepComponent } from '@shared/components';
import { CountriesDirective } from '@shared/directives';

import { CreateTargetUnitStore } from '../create-target-unit.store';
import {
  TARGET_UNIT_OPERATOR_ADDRESS_FORM,
  TargetUnitOperatorAddressFormProvider,
} from './operator-address-form.provider';

@Component({
  selector: 'cca-operator-address',
  templateUrl: './operator-address.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    SelectComponent,
    AccountAddressInputComponent,
    CountriesDirective,
    WizardStepComponent,
  ],
  providers: [TargetUnitOperatorAddressFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorAddressComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly store = inject(CreateTargetUnitStore);

  readonly form = inject<FormGroup<AccountAddressFormModel>>(TARGET_UNIT_OPERATOR_ADDRESS_FORM);

  onSubmitOperatorAddress() {
    this.store.updateState({ address: { ...this.form.getRawValue() } });

    if (this.store.sameAddressWithOperator) {
      this.store.sameAddressWithOperator = false;
      this.store.updateState({
        responsiblePerson: { ...this.store.state.responsiblePerson, address: null },
      });
      if (this.store.sameAddressWithResponsiblePerson) {
        this.store.sameAddressWithResponsiblePerson = false;
        this.store.updateState({
          administrativeContactDetails: {
            ...this.store.state.administrativeContactDetails,
            address: null,
          },
        });
      }
    }

    this.router.navigate(['..', 'responsible-person'], { relativeTo: this.route });
  }
}
