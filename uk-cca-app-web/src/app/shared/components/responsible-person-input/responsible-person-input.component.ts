import { Component, input } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { CheckboxComponent, CheckboxesComponent } from '@netz/govuk-components';
import { transformPhoneInput } from '@shared/pipes';
import { existingControlContainer } from '@shared/providers';

import { AccountAddressInputComponent } from '../account-address-input';
import { PhoneInputComponent } from '../phone-input/phone-input.component';
import { TextInputComponent } from '../text-input/text-input.component';

@Component({
  selector: 'cca-responsible-person-input',
  templateUrl: './responsible-person-input.component.html',
  standalone: true,
  imports: [
    TextInputComponent,
    ReactiveFormsModule,
    AccountAddressInputComponent,
    PhoneInputComponent,
    CheckboxComponent,
    CheckboxesComponent,
  ],
  viewProviders: [existingControlContainer],
})
export class ResponsiblePersonInputComponent {
  sameAddressExists = input(true);
  readonly transformPhoneInput = transformPhoneInput;
}
