import { computed, inject, Injectable, signal } from '@angular/core';

import { catchError, map, Observable, of, tap } from 'rxjs';

import { ReferenceDataService } from 'cca-api';

import { Country } from '../types/country';

export const UK_COUNTRY_CODES = ['GB-ENG', 'GB-NIR', 'GB-SCT', 'GB-WLS'];

@Injectable({ providedIn: 'root' })
export class CountryService {
  private readonly referenceDataService = inject(ReferenceDataService);

  private readonly _countries = signal<Country[]>([]);
  readonly countries = this._countries.asReadonly();
  readonly ukCountries = computed(() => this._countries().filter((c) => UK_COUNTRY_CODES.includes(c.code)));

  load(): Observable<Country[]> {
    return this.referenceDataService.getReferenceData(['COUNTRIES']).pipe(
      catchError(() => {
        console.error('There was an error fetching the countries.');
        return of({ COUNTRIES: [] as Country[] });
      }),
      map(({ COUNTRIES }) => COUNTRIES as Country[]),
      tap((countries) => this._countries.set(countries)),
    );
  }
}
