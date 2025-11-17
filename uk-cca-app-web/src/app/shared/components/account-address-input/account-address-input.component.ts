import { Component } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { SelectComponent, TextInputComponent } from '@netz/govuk-components';
import { CountriesDirective } from '@shared/directives';
import { existingControlContainer } from '@shared/providers';

@Component({
  selector: 'cca-account-address-input',
  templateUrl: './account-address-input.component.html',
  imports: [TextInputComponent, ReactiveFormsModule, SelectComponent, CountriesDirective],
  viewProviders: [existingControlContainer],
})
export class AccountAddressInputComponent {}
