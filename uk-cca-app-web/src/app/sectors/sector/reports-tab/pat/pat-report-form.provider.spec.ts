import { toPatTargetPeriodYear } from './pat-report-form.provider';

describe('PAT reporting years', () => {
  it.each([2024, 2026, 2027, 2028, 2029, 2030])('accepts the supported year %s', (year) => {
    expect(toPatTargetPeriodYear(String(year))).toBe(year);
  });

  it('rejects unsupported or missing reporting years', () => {
    expect(toPatTargetPeriodYear('2025')).toBeNull();
    expect(toPatTargetPeriodYear(null)).toBeNull();
  });
});
