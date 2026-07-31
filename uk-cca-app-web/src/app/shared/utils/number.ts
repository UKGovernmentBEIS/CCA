import BigNumber from 'bignumber.js';

export const toNumber = (value?: string | number | null): number => (value == null ? 0 : Number(value));

/**
 * Converts a value to a BigNumber instance for precise decimal arithmetic.
 * Returns BigNumber(0) for null/undefined/empty-string or invalid numeric values.
 *
 * Form controls can contain transient invalid values while the user is typing. BigNumber
 * runs in strict mode and throws for those values, so keep live calculations defensive and
 * leave validation to prevent the invalid value from being submitted.
 */
export const toBigNumber = (value?: string | number | BigNumber | null): BigNumber => {
  if (value == null || value === '') return new BigNumber(0);

  try {
    const result = value instanceof BigNumber ? value : new BigNumber(value);
    return result.isNaN() ? new BigNumber(0) : result;
  } catch {
    return new BigNumber(0);
  }
};

/**
 * Rounds a BigNumber to 7 decimal places using half-up rounding and converts to a JS Number.
 * Returns 0 for null/undefined/NaN values.
 *
 * This mirrors the rounding used by {@link roundHalfUpTo7Decimals} for API payloads,
 * ensuring that values displayed via Angular's {@code number} pipe match what is sent to the server.
 */
export const to7DecimalPlacesNumber = (value?: BigNumber | null): number => {
  if (!value || value.isNaN()) return 0;
  return value.decimalPlaces(7, BigNumber.ROUND_HALF_UP).toNumber();
};

// This is to format number values like 0E-7
export const formatScientificZero = (value: string | null | undefined): string | null | undefined =>
  value?.match(/^[-+]?0(?:\.0+)?e[-+]?\d+$/i) ? '0' : value;
