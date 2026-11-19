import BigNumber from 'bignumber.js';

/** Format finite GBP amounts without losing precision; leave missing or invalid values blank. */
export function formatGbp(value: string | number | null | undefined): string {
  if (value == null || value === '') return '';

  try {
    const amount = new BigNumber(value);
    return amount.isFinite() ? amount.toFormat(2, BigNumber.ROUND_HALF_UP) : '';
  } catch {
    return '';
  }
}
