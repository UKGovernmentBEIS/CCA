/** Add digit grouping without converting decimal strings to floating-point numbers. */
export function formatHistoricalNumber(value: string | number | null | undefined): string {
  if (value === null || value === undefined) return '';

  // Preserve the API-supplied precision for nonmonetary values such as tCO₂e.
  const text = String(value);
  const decimal = /^([+-]?)(\d+)(\.\d+)?$/.exec(text);
  if (!decimal) return text;

  const [, sign, integer, fraction = ''] = decimal;
  const firstGroupLength = integer.length % 3 || 3;
  const groups = [integer.slice(0, firstGroupLength)];

  for (let index = firstGroupLength; index < integer.length; index += 3) {
    groups.push(integer.slice(index, index + 3));
  }

  return `${sign}${groups.join(',')}${fraction}`;
}
