import { ChangeDetectorRef, Directive, OnInit } from '@angular/core';

import { map } from 'rxjs';

import { Country } from '@core/models/country';
import { CountryService } from '@core/services/country.service';

import { SelectComponent } from 'govuk-components';

@Directive({
  selector: 'govuk-select[ccaCountries],[govuk-select][ccaCountries]',
  standalone: true,
})
export class CountriesDirective implements OnInit {
  constructor(
    private readonly apiService: CountryService,
    private readonly selectComponent: SelectComponent,
    private readonly changeDetectorRef: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.apiService
      .getUkCountries()
      .pipe(
        map((countries: Country[]) =>
          countries
            .sort((a: Country, b: Country) => (a.name > b.name ? 1 : -1))
            .map((country) => ({
              text: country.name,
              value: country.code,
            })),
        ),
      )
      .subscribe((res) => {
        this.selectComponent.options = res;
        this.changeDetectorRef.markForCheck();
      });
  }
}
