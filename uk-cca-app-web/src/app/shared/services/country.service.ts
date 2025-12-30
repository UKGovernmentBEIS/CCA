import { inject, Injectable } from '@angular/core';

import { catchError, map, tap } from 'rxjs';

import { ReferenceDataService } from 'cca-api';

import { Country } from '../types/country';

export const UK_COUNTRY_CODES = ['GB-ENG', 'GB-NIR', 'GB-SCT', 'GB-WLS'];
export let COUNTRIES: Country[] = [];
export let UK_COUNTRIES: Country[] = [];

@Injectable({ providedIn: 'root' })
export class CountryService {
  private readonly referenceDataService = inject(ReferenceDataService);

  constructor() {
    this.referenceDataService
      .getReferenceData(['COUNTRIES'])
      .pipe(
        catchError((err) => {
          console.error('There was an error fetching the countries.');
          return err;
        }),
        map((response: { COUNTRIES: Country[] }) => response.COUNTRIES as Country[]),
        tap((countries) => (COUNTRIES = countries)),
        tap((countries) => {
          const ukCountries = countries.filter((c) => UK_COUNTRY_CODES.includes(c.code));
          UK_COUNTRIES = ukCountries;
        }),
      )
      .subscribe();
  }
}
