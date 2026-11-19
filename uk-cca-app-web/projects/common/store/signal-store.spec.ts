import { Injectable, signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';

import { SignalStore } from './signal-store';

type TestState = { count: number; other: number };

@Injectable()
class TestStore extends SignalStore<TestState> {
  constructor() {
    super({ count: 0, other: 0 });
  }
}

type NestedState = { list: { items: string[] }; selected: Map<string, number> };

const NESTED_INITIAL_STATE: NestedState = { list: { items: ['a'] }, selected: new Map() };

class NestedStore extends SignalStore<NestedState> {
  constructor() {
    super(NESTED_INITIAL_STATE);
  }
}

describe('SignalStore state ownership', () => {
  const createNestedStore = () => TestBed.runInInjectionContext(() => new NestedStore());

  it('should throw on in-place mutation of the state, including Maps', () => {
    const store = createNestedStore();

    expect(() => store.state.selected.set('x', 1)).toThrow();
    expect(() => store.state.list.items.push('b')).toThrow();
    expect(() => {
      (store.state.list as { items: string[] }).items = [];
    }).toThrow();
  });

  it('should freeze objects passed in through updateState', () => {
    const store = createNestedStore();
    const selected = new Map([['x', 1]]);

    store.updateState({ selected });

    expect(() => selected.delete('x')).toThrow();
  });

  it('should leave the initial state object unfrozen and unshared', () => {
    const store = createNestedStore();

    expect(store.state.selected).not.toBe(NESTED_INITIAL_STATE.selected);
    expect(Object.isFrozen(NESTED_INITIAL_STATE)).toBe(false);
    expect(() => NESTED_INITIAL_STATE.selected.set('y', 2)).not.toThrow();
    NESTED_INITIAL_STATE.selected.clear();
  });

  it('should reset to the initial state', () => {
    const store = createNestedStore();
    store.updateState({ selected: new Map([['x', 1]]), list: { items: [] } });

    store.reset();

    expect(store.state).toEqual(NESTED_INITIAL_STATE);
  });

  it('should keep the references of slices that an update does not touch', () => {
    const store = createNestedStore();
    const list = store.state.list;

    store.updateState({ selected: new Map([['x', 1]]) });

    expect(store.state.list).toBe(list);
  });
});

describe('SignalStore', () => {
  let store: TestStore;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [TestStore] });
    store = TestBed.inject(TestStore);
  });

  describe('rxSelect', () => {
    it('should emit the current value, then each change of the selected slice', () => {
      const values: number[] = [];
      store.rxSelect((state) => state.count).subscribe((value) => values.push(value));
      TestBed.tick();

      store.updateState({ count: 1 });
      TestBed.tick();
      store.updateState({ other: 5 });
      TestBed.tick();

      expect(values).toEqual([0, 1]);
    });

    it('should stop running the selector once unsubscribed', () => {
      const selector = vi.fn((state: TestState) => state.count);
      const subscription = store.rxSelect(selector).subscribe();
      TestBed.tick();
      store.updateState({ count: 1 });
      TestBed.tick();
      const callsWhileSubscribed = selector.mock.calls.length;

      subscription.unsubscribe();
      store.updateState({ count: 2 });
      TestBed.tick();

      expect(callsWhileSubscribed).toBeGreaterThan(0);
      expect(selector).toHaveBeenCalledTimes(callsWhileSubscribed);
    });

    it('should not track signals read by the subscriber', () => {
      const unrelated = signal(0);
      const values: number[] = [];
      store.rxSelect((state) => state.count).subscribe((value) => values.push(value + unrelated() * 0));
      TestBed.tick();

      unrelated.set(1);
      TestBed.tick();

      expect(values).toEqual([0]);
    });
  });
});
