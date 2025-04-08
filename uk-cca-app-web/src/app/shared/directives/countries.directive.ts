import { ChangeDetectorRef, Directive, OnInit } from '@angular/core';

import { map } from 'rxjs';

import { SelectComponent } from '@netz/govuk-components';
import { CountryService } from '@shared/services';
import { Country } from '@shared/types';

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
            .sort((a: Country, b: Country) => {
              if (this.isUkCountry(a.code) && !this.isUkCountry(b.code)) return -1;
              if (!this.isUkCountry(a.code) && this.isUkCountry(b.code)) return 1;

              return a.name > b.name ? 1 : -1;
            })
            .map((country) => ({
              text: country.name,
              value: country.code,
            })),
        ),
      )
      .subscribe((res) => {
        // Insert empty selection after Wales
        const walesIndex = res.findIndex((c) => c.text === 'Wales');
        res.splice(walesIndex + 1, 0, { text: '--', value: '' });

        this.selectComponent.options = res;
        this.changeDetectorRef.markForCheck();
      });
  }

  private isUkCountry = (countryCode: string) => {
    const ukCountryCodes = ['GB-ENG', 'GB-NIR', 'GB-SCT', 'GB-WLS'];
    return ukCountryCodes.includes(countryCode);
  };
}
