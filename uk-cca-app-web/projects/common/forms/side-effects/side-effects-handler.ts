import { inject, Injectable } from '@angular/core';

import { map, Observable, of, zip } from 'rxjs';

import { GenericRequestTaskPayload } from '../types';
import { SideEffect, SubtaskOperation } from './side-effect';
import { SIDE_EFFECTS } from './side-effects.providers';

@Injectable()
export class SideEffectsHandler {
  private sideEffects: SideEffect[] = inject(SIDE_EFFECTS, { optional: true });

  apply(
    subtask: string,
    step: string,
    payload: GenericRequestTaskPayload,
    operation: SubtaskOperation,
  ): Observable<GenericRequestTaskPayload> {
    const sideEffectsToApply = (this.sideEffects ?? []).filter(
      (se) =>
        se.subtask === subtask &&
        se.on.includes(operation) &&
        (step === null ? !se.step : !se.step || se.step === step),
    );

    if (!sideEffectsToApply || sideEffectsToApply.length === 0) {
      return of(payload);
    } else {
      const sideEffectResults: Observable<GenericRequestTaskPayload>[] = sideEffectsToApply.map((se) =>
        se.apply(payload, subtask),
      );
      return zip(...sideEffectResults).pipe(
        map((zipped) => zipped.reduce((acc, ser) => ({ ...acc, ...ser }), payload)),
      );
    }
  }
}
