import { DaysRemainingPipe } from './days-remaining.pipe';

describe('DaysRemainingPipe', () => {
  const pipe = new DaysRemainingPipe();

  afterEach(() => vi.useRealTimers());

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return empty if null value', () => {
    const transformation = pipe.transform(null);

    expect(transformation).toEqual('');
  });

  it('should return empty if undefined value', () => {
    const transformation = pipe.transform(undefined);

    expect(transformation).toEqual('');
  });

  it('should return 0 value', () => {
    const transformation = pipe.transform(0);

    expect(transformation).toEqual('0');
  });

  it('should return value', () => {
    const transformation = pipe.transform(13);

    expect(transformation).toEqual('13');
  });

  it('should return Overdue if negative value', () => {
    const transformation = pipe.transform(-13);

    expect(transformation).toEqual('Overdue');
  });

  describe.each([
    ['the start of the year', 2025, 0, 1],
    ['the start of British Summer Time', 2025, 2, 30],
    ['the day after British Summer Time starts', 2025, 2, 31],
    ['summer', 2025, 6, 15],
    ['the end of British Summer Time', 2025, 9, 26],
    ['the day after British Summer Time ends', 2025, 9, 27],
    ['the end of the year', 2025, 11, 31],
  ] as const)('during %s', (_label, year, month, day) => {
    it.each([0, 23])('counts calendar days at %i:30 local time', (hour) => {
      vi.useFakeTimers({ toFake: ['Date'] });
      vi.setSystemTime(new Date(year, month, day, hour, 30));
      const deadline = (offset: number) => new Date(Date.UTC(year, month, day + offset)).toISOString().slice(0, 10);

      expect(pipe.calcRemainingDays(deadline(-1))).toBe(-1);
      expect(pipe.calcRemainingDays(deadline(0))).toBe(0);
      expect(pipe.calcRemainingDays(deadline(1))).toBe(1);
    });
  });

  it('marks a deadline overdue only after its local calendar day has passed', () => {
    vi.useFakeTimers({ toFake: ['Date'] });
    vi.setSystemTime(new Date(2025, 6, 15, 23, 30));
    expect(pipe.transform(pipe.calcRemainingDays('2025-07-15'))).toBe('0');

    vi.setSystemTime(new Date(2025, 6, 16, 0, 30));
    expect(pipe.transform(pipe.calcRemainingDays('2025-07-15'))).toBe('Overdue');
  });
});
