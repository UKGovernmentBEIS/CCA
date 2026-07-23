import { inject, Injectable, signal } from '@angular/core';

import { catchError, map, Observable, of, tap } from 'rxjs';

import { County } from '@shared/types';

import { ReferenceDataService } from 'cca-api';

import { logger } from '../utils/logger';

@Injectable({ providedIn: 'root' })
export class CountyService {
  private readonly referenceDataService = inject(ReferenceDataService);

  private readonly _counties = signal<County[]>([]);
  readonly counties = this._counties.asReadonly();

  load(): Observable<County[]> {
    return this.referenceDataService.getReferenceData(['COUNTIES']).pipe(
      catchError(() => {
        logger.error('There was an error fetching the counties.');
        return of({ COUNTIES: [] as County[] });
      }),
      map(({ COUNTIES }) => COUNTIES as County[]),
      tap((counties) => this._counties.set(counties)),
    );
  }
}
