import { ChangeDetectorRef, Directive, inject, OnInit } from '@angular/core';

import { map, of } from 'rxjs';

import { SelectComponent } from '@netz/govuk-components';
import { COUNTRIES, UK_COUNTRY_CODES } from '@shared/services';
import { Country } from '@shared/types';

@Directive({
  selector: 'govuk-select[ccaCountries],[govuk-select][ccaCountries]',
})
export class CountriesDirective implements OnInit {
  private readonly selectComponent = inject(SelectComponent);
  private readonly changeDetectorRef = inject(ChangeDetectorRef);

  ngOnInit() {
    of(COUNTRIES)
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

        this.selectComponent.options.set(res);
        this.changeDetectorRef.markForCheck();
      });
  }

  private isUkCountry = (countryCode: string) => UK_COUNTRY_CODES.includes(countryCode);
}
