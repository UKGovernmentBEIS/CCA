import { alphabeticalCompare, extractBaselineYear } from './utils';

describe('alphabeticalCompare', () => {
  it('should sort strings using en-GB numeric case-insensitive ordering', () => {
    const values = ['WF-010', 'wf-002', 'WF-001'];

    expect(values.sort(alphabeticalCompare)).toEqual(['WF-001', 'wf-002', 'WF-010']);
  });
});

describe('extractBaselineYear', () => {
  it.each([
    ['2025-01-01', 2025],
    ['2025-07-02', 2025],
    ['2025-07-03', 2026],
    ['2024-07-01', 2024],
    ['2024-07-02', 2025],
    ['2025-12-31', 2026],
  ])('uses the calendar day of %s for the baseline year', (date, expected) => {
    expect(extractBaselineYear(date)).toBe(expected);
    expect(extractBaselineYear(`${date}T00:00:00.000Z`)).toBe(expected);
  });
});
