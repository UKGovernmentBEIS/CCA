import { Component, input } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { CheckboxComponent, CheckboxesComponent, TextInputComponent } from '@netz/govuk-components';
import { AccountAddressInputComponent } from '@shared/components';
import { PhoneInputComponent } from '@shared/components';
import { existingControlContainer } from '@shared/providers';

@Component({
  selector: 'cca-administrative-contact-input',
  templateUrl: './administrative-contact-input.component.html',
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
export class AdministrativeContactInputComponent {
  sameAddressExists = input(true);
}
