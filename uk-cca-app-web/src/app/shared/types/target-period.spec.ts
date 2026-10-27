import { compareTargetPeriodsDesc, isCCA3TargetPeriod, TargetPeriodType } from './target-period';

describe('Target period helpers', () => {
  describe('isCCA3TargetPeriod', () => {
    it('should recognise the CCA3 target periods', () => {
      const testCases: { targetPeriod: TargetPeriodType; expected: boolean }[] = [
        { targetPeriod: 'TP5', expected: false },
        { targetPeriod: 'TP6', expected: false },
        { targetPeriod: 'TP7', expected: true },
        { targetPeriod: 'TP8', expected: true },
        { targetPeriod: 'TP9', expected: true },
      ];

      testCases.forEach(({ targetPeriod, expected }) => {
        expect(isCCA3TargetPeriod(targetPeriod)).toBe(expected);
      });
    });

    it('should treat a missing target period as not being CCA3', () => {
      expect(isCCA3TargetPeriod(null)).toBe(false);
      expect(isCCA3TargetPeriod(undefined)).toBe(false);
    });
  });

  describe('compareTargetPeriodsDesc', () => {
    it('should order target periods from the latest to the earliest', () => {
      expect(['TP7', 'TP9', 'TP5', 'TP8', 'TP6'].sort(compareTargetPeriodsDesc)).toEqual([
        'TP9',
        'TP8',
        'TP7',
        'TP6',
        'TP5',
      ]);
    });

    it('should compare the number and not the string, so that TP10 outranks TP9', () => {
      expect(['TP9', 'TP10'].sort(compareTargetPeriodsDesc)).toEqual(['TP10', 'TP9']);
    });

    it('should push missing target periods to the end', () => {
      expect([undefined, 'TP7', undefined, 'TP8'].sort(compareTargetPeriodsDesc)).toEqual([
        'TP8',
        'TP7',
        undefined,
        undefined,
      ]);
    });
  });
});
