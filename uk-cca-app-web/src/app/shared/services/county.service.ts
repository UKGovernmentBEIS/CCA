import { inject, Injectable } from '@angular/core';

import { catchError, map, tap } from 'rxjs';

import { County } from '@shared/types';

import { ReferenceDataService } from 'cca-api';

export let COUNTIES: County[] = [];

@Injectable({ providedIn: 'root' })
export class CountyService {
  private readonly referenceDataService = inject(ReferenceDataService);

  constructor() {
    this.referenceDataService
      .getReferenceData(['COUNTIES'])
      .pipe(
        catchError((err) => {
          console.error('There was an error fetching the counties.');
          return err;
        }),
        map((response: { COUNTIES: County[] }) => response.COUNTIES as County[]),
        tap((counties) => (COUNTIES = counties)),
      )
      .subscribe();
  }
}
