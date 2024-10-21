import { Component, input } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { CheckboxComponent, CheckboxesComponent } from '@netz/govuk-components';
import { AccountAddressInputComponent } from '@shared/components';
import { PhoneInputComponent } from '@shared/components';
import { TextInputComponent } from '@shared/components/text-input/text-input.component';
import { transformPhoneInput } from '@shared/pipes/phone-number-input.pipe';
import { existingControlContainer } from '@shared/providers';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
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
