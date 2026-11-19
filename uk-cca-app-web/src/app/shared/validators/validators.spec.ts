import { FormControl } from '@angular/forms';

import { futureDateValidator } from './validators';

describe('futureDateValidator', () => {
  const errorMessage = 'The date must be today or in the past';
  const validator = futureDateValidator(errorMessage);

  beforeEach(() => vi.useFakeTimers({ toFake: ['Date'] }));

  afterEach(() => vi.useRealTimers());

  it('leaves empty values to required validators', () => {
    expect(validator(new FormControl(null))).toBeNull();
  });

  it('ignores values that are not dates', () => {
    expect(validator(new FormControl('2099-01-01'))).toBeNull();
  });

  // Run this matrix across timezones with yarn test:timezones.
  // Early morning and late evening expose local/UTC day differences in BST and negative UTC offsets.
  describe.each([
    ['winter', 2025, 0, 15],
    ['the start of British Summer Time', 2025, 2, 30],
    ['the day after British Summer Time starts', 2025, 2, 31],
    ['summer', 2025, 6, 15],
    ['the end of British Summer Time', 2025, 9, 26],
    ['the day after British Summer Time ends', 2025, 9, 27],
    ['the end of the year', 2025, 11, 31],
  ] as const)('during %s', (_label, year, month, day) => {
    it.each([0, 23])('accepts today and yesterday but rejects tomorrow at %i:30 local time', (hour) => {
      vi.setSystemTime(new Date(year, month, day, hour, 30));

      expect(validator(new FormControl(new Date(Date.UTC(year, month, day - 1))))).toBeNull();
      expect(validator(new FormControl(new Date(Date.UTC(year, month, day))))).toBeNull();
      expect(validator(new FormControl(new Date(Date.UTC(year, month, day + 1))))).toEqual({
        invalidDate: errorMessage,
      });
    });
  });

  it('ignores the time of day on the selected date without mutating it', () => {
    vi.setSystemTime(new Date(2025, 6, 15, 0, 30));
    const selectedDate = new Date('2025-07-15T23:59:59.999Z');

    expect(validator(new FormControl(selectedDate))).toBeNull();
    expect(selectedDate.toISOString()).toBe('2025-07-15T23:59:59.999Z');
  });

  it('re-evaluates today when a form survives past local midnight', () => {
    vi.setSystemTime(new Date(2025, 6, 15, 23, 30));
    const control = new FormControl(new Date('2025-07-16'), validator);
    expect(control.errors).toEqual({ invalidDate: errorMessage });

    vi.setSystemTime(new Date(2025, 6, 16, 0, 30));
    control.updateValueAndValidity();

    expect(control.valid).toBe(true);
  });
});
