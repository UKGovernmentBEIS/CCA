import { TestBed } from '@angular/core/testing';

import { ConfigService } from '@shared/config';

import { RequestsService, SubsistenceFeesRunInfoViewService } from 'cca-api';

import { SubsistenceFeesStore } from './subsistence-fees.store';

describe('SubsistenceFeesStore charge date', () => {
  beforeEach(() => vi.useFakeTimers({ toFake: ['Date'] }));

  afterEach(() => vi.useRealTimers());

  it.each([
    ['2025-04-01', 2025, 2, 31, 23, false],
    ['2025-04-01', 2025, 3, 1, 0, true],
    ['2025-04-01', 2025, 3, 2, 0, true],
    ['2025-10-26', 2025, 9, 25, 23, false],
    ['2025-10-26', 2025, 9, 26, 0, true],
    ['2026-01-01', 2025, 11, 31, 23, false],
    ['2026-01-01', 2026, 0, 1, 0, true],
  ])(
    'uses the local calendar day for trigger %s at %i/%i/%i %i:30',
    (triggerDate, year, month, day, hour, isValidChargeDate) => {
      vi.setSystemTime(new Date(year, month, day, hour, 30));
      TestBed.configureTestingModule({
        providers: [
          SubsistenceFeesStore,
          { provide: ConfigService, useValue: { getSubsistenceFeesRunTriggerDate: () => triggerDate } },
          { provide: RequestsService, useValue: {} },
          { provide: SubsistenceFeesRunInfoViewService, useValue: {} },
        ],
      });

      expect(TestBed.inject(SubsistenceFeesStore).state.isValidChargeDate).toBe(isValidChargeDate);
    },
  );
});
