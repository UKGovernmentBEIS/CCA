import { inject, Injectable } from '@angular/core';

import { Observable, of, reduce, zip } from 'rxjs';

import { RequestTaskPayload } from 'cca-api';

import { SideEffect, SubtaskOperation } from './side-effect';
import { SIDE_EFFECTS } from './side-effects.providers';

@Injectable()
export class SideEffectsHandler {
  private sideEffects: SideEffect[] = inject(SIDE_EFFECTS, { optional: false });

  apply<T extends RequestTaskPayload>(
    subtask: string,
    step: string,
    payload: T,
    operation: SubtaskOperation,
  ): Observable<T> {
    const sideEffectsToApply = this.sideEffects.filter(
      (se) =>
        se.subtask === subtask &&
        se.on.includes(operation) &&
        (step === null ? !se.step : !se.step || se.step === step),
    );

    if (!sideEffectsToApply || sideEffectsToApply.length === 0) {
      return of(payload);
    } else {
      const sideEffectResults: Observable<T>[] = sideEffectsToApply.map((se) => se.apply(payload));
      return zip(...sideEffectResults).pipe(reduce((acc, ser) => ({ ...acc, ...ser }), payload));
    }
  }
}
