import { Component } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { SelectComponent, TextInputComponent } from '@netz/govuk-components';
import { existingControlContainer } from '@shared/providers';
import { UK_COUNTRIES } from '@shared/services';

@Component({
  selector: 'cca-facility-address-input',
  templateUrl: './facility-address-input.component.html',
  imports: [TextInputComponent, ReactiveFormsModule, SelectComponent],
  viewProviders: [existingControlContainer],
})
export class FacilityAddressInputComponent {
  protected readonly ukCountriesOptions = UK_COUNTRIES.map((c) => ({ text: c.name, value: c.code }));
}
