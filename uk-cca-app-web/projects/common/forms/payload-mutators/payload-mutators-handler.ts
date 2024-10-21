import { inject, Injectable } from '@angular/core';

import { map, Observable, of, zip } from 'rxjs';

import { GenericRequestTaskPayload } from '../types';
import { PayloadMutator } from './payload-mutator';
import { PAYLOAD_MUTATORS } from './payload-mutators.providers';

@Injectable()
export class PayloadMutatorsHandler {
  private payloadMutators: PayloadMutator[] = inject(PAYLOAD_MUTATORS, { optional: false });

  mutate(
    subtask: string,
    step: string | null,
    payload: GenericRequestTaskPayload,
    userInput: any,
  ): Observable<GenericRequestTaskPayload> {
    const payloadMutatorsToRun = this.payloadMutators.filter((pm) => pm.subtask === subtask);

    if (!payloadMutatorsToRun || payloadMutatorsToRun.length === 0) {
      return of(payload);
    } else {
      const payloadMutatorsResults: Observable<GenericRequestTaskPayload>[] = payloadMutatorsToRun.map((pm) =>
        pm.apply(payload, step, userInput),
      );
      return zip(...payloadMutatorsResults).pipe(
        map((zipped) => zipped.reduce((acc, pmr) => ({ ...acc, ...pmr }), payload)),
      );
    }
  }
}
