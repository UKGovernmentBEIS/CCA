import { Component } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { SelectComponent, TextInputComponent } from '@netz/govuk-components';
import { CountiesDirective } from '@shared/directives/counties.directive';
import { CountriesDirective } from '@shared/directives/countries.directive';
import { existingControlContainer } from '@shared/providers/control-container.factory';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'cca-account-address-input',
  templateUrl: './account-address-input.component.html',
  standalone: true,
  imports: [TextInputComponent, ReactiveFormsModule, SelectComponent, CountiesDirective, CountriesDirective],
  viewProviders: [existingControlContainer],
})
export class AccountAddressInputComponent {}
