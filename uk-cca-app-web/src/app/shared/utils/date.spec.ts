import { formatCalendarDate, getToday } from './date';

describe('calendar dates', () => {
  afterEach(() => vi.useRealTimers());

  it.each([
    [2025, 0, 1, 0],
    [2025, 2, 30, 0],
    [2025, 6, 15, 0],
    [2025, 6, 15, 23],
    [2025, 9, 26, 0],
    [2025, 11, 31, 23],
  ])('stores the local day at UTC midnight for %i/%i/%i at %i:30', (year, month, day, hour) => {
    vi.useFakeTimers({ toFake: ['Date'] });
    vi.setSystemTime(new Date(year, month, day, hour, 30));

    expect(getToday()).toEqual(new Date(Date.UTC(year, month, day)));
  });

  it.each(['2025-01-01', '2025-03-30', '2025-07-15', '2025-10-26', '2025-12-31'])(
    'preserves %s when formatting API strings and date input values',
    (day) => {
      expect(formatCalendarDate(day, 'yyyy-MM-dd')).toBe(day);
      expect(formatCalendarDate(`${day}T00:00:00.000Z`, 'yyyy-MM-dd')).toBe(day);
      expect(formatCalendarDate(new Date(day), 'yyyy-MM-dd')).toBe(day);
    },
  );

  it('formats calendar dates for display', () => {
    expect(formatCalendarDate('2025-07-15')).toBe('15 Jul 2025');
    expect(formatCalendarDate(new Date('2025-01-01'), 'dd MMM yyyy')).toBe('01 Jan 2025');
  });

  it.each([null, undefined, ''])('leaves an empty calendar date empty', (date) => {
    expect(formatCalendarDate(date)).toBeNull();
  });
});
