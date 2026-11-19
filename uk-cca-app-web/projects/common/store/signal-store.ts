import { computed, effect, inject, Injector, Signal, signal, untracked, WritableSignal } from '@angular/core';

import { Observable } from 'rxjs';

import { freeze } from 'immer';

export type StateSelector<S, SL> = (state: S) => SL;

/**
 * Create selector directly from root state
 *
 * @param projector Mapping function to extract state slice
 */
export function createSelector<S, SL>(projector: (state: S) => SL): StateSelector<S, SL> {
  return (state) => projector(state);
}

/**
 * Create selector from state slice
 * This is used to create a selector using a previous selector as base
 *
 * @param selector The base selector
 * @param projector Mapping function to extract state slice
 */
export function createDescendingSelector<S, IM, SL>(
  selector: StateSelector<S, IM>,
  projector: (state: IM) => SL,
): StateSelector<S, SL> {
  return (state) => projector(selector(state));
}

/**
 * Create selector from state slice
 * This is used to create a selector using two root-state selectors (different slices of state)
 *
 * @param s1 The first root-state selector
 * @param s2 The second root-state selector
 * @param projector Mapping function to extract aggregate state
 */
export function createAggregateSelector<S, IM1, IM2, SL>(
  s1: StateSelector<S, IM1>,
  s2: StateSelector<S, IM2>,
  projector: (im1: IM1, im2: IM2) => SL,
): StateSelector<S, SL>;
export function createAggregateSelector<S, IM1, IM2, IM3, SL>(
  s1: StateSelector<S, IM1>,
  s2: StateSelector<S, IM2>,
  s3: StateSelector<S, IM3>,
  projector: (im1: IM1, im2: IM2, im3: IM3) => SL,
): StateSelector<S, SL>;
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export function createAggregateSelector<S, SL>(...args: any[]): StateSelector<S, SL> {
  const projector = args[args.length - 1];
  const selectors = args.slice(0, args.length - 1);

  return (state) => {
    const selectorParts = selectors.map((s) => s(state));
    return projector(...selectorParts);
  };
}

/**
 * A simple implementation of an application store based on [signals](https://angular.io/guide/signals).
 *
 * State is immutable. Every state the store holds is deep-frozen, so mutating it in place, including
 * `Map.set()` / `Map.delete()`, throws at the line that does it. Replace objects, arrays and Maps
 * instead, e.g. with Immer's `produce()` or `new Map(old)`.
 */
export abstract class SignalStore<T> {
  private readonly injector = inject(Injector);
  private readonly _state: WritableSignal<T>;
  private readonly _initialState: T;

  protected constructor(initialState: T) {
    // The store must not share references with the caller's object (often a module-level constant):
    // mutating that object would corrupt the next store instance. The frozen copy is safe to reuse on reset.
    this._initialState = freeze(structuredClone(initialState), true);
    this._state = signal(this._initialState);
  }

  get state(): T {
    return this._state();
  }

  get stateAsSignal(): Signal<T> {
    return this._state.asReadonly();
  }

  /** Shallow-merges a partial state. Unchanged slices keep their references, so their selectors do not recompute. */
  updateState(state: Partial<T>): void {
    this.setState({ ...this._state(), ...state });
  }

  setState(state: T) {
    // Already-frozen subtrees are skipped, so this only walks what is new.
    this._state.set(freeze(state, true));
  }

  select<SL = T[keyof T]>(selector: StateSelector<T, SL>): Signal<SL> {
    return computed(() => selector(this._state()));
  }

  /**
   * Observable view of a selector. Emits the current value, then each change, like `toObservable`.
   * The effect behind it lives only while subscribed, so short-lived subscribers (guards,
   * components) do not leave effects running in the store's injector.
   */
  rxSelect<SL = T[keyof T]>(selector: StateSelector<T, SL>): Observable<SL> {
    const selected = this.select(selector);
    return new Observable<SL>((subscriber) => {
      const watcher = effect(
        () => {
          let value: SL;
          try {
            value = selected();
          } catch (error) {
            untracked(() => subscriber.error(error));
            return;
          }
          // Subscriber code must not become a dependency of the effect.
          untracked(() => subscriber.next(value));
        },
        { injector: this.injector, manualCleanup: true },
      );
      return () => watcher.destroy();
    });
  }

  reset(): void {
    this.setState(this._initialState);
  }
}
