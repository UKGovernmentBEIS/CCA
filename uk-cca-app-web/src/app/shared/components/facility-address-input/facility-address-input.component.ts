import { Component, computed, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

import { SelectComponent, TextInputComponent } from '@netz/govuk-components';
import { existingControlContainer } from '@shared/providers';
import { CountryService } from '@shared/services';

@Component({
  selector: 'cca-facility-address-input',
  templateUrl: './facility-address-input.component.html',
  imports: [TextInputComponent, ReactiveFormsModule, SelectComponent],
  viewProviders: [existingControlContainer],
})
export class FacilityAddressInputComponent {
  private readonly countryService = inject(CountryService);

  protected readonly ukCountriesOptions = computed(() =>
    this.countryService.ukCountries().map((c) => ({ text: c.name, value: c.code })),
  );
}
