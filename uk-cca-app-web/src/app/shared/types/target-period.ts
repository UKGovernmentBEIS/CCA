export type TargetPeriodType = 'TP5' | 'TP6' | 'TP7' | 'TP8' | 'TP9';

export const CCA3_TARGET_PERIODS: TargetPeriodType[] = ['TP7', 'TP8', 'TP9'];

export const isCCA3TargetPeriod = (targetPeriod?: TargetPeriodType | null): boolean =>
  !!targetPeriod && CCA3_TARGET_PERIODS.includes(targetPeriod);

/**
 * Orders target periods from the latest to the earliest, e.g. `TP9, TP8, TP7`.
 */
export const compareTargetPeriodsDesc = (a?: string, b?: string): number =>
  (b ?? '').localeCompare(a ?? '', 'en-GB', { numeric: true, sensitivity: 'base' });
