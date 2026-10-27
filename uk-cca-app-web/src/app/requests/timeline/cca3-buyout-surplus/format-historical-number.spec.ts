import { formatHistoricalNumber } from './format-historical-number';

describe('formatHistoricalNumber', () => {
  it.each([
    ['19900', '19,900'],
    ['1001.123456789', '1,001.123456789'],
    ['0', '0'],
    ['0.0000000', '0.0000000'],
    ['9007199254740993.00000001', '9,007,199,254,740,993.00000001'],
    ['-1234.5000', '-1,234.5000'],
    ['+1234567.8900', '+1,234,567.8900'],
    ['0001234.0500', '0,001,234.0500'],
    ['123', '123'],
    ['123456', '123,456'],
    ['0.000000000000000001', '0.000000000000000001'],
    [0, '0'],
    [38, '38'],
    [undefined, ''],
    [null, ''],
  ])('preserves the supplied precision for %s', (value, expected) => {
    expect(formatHistoricalNumber(value)).toBe(expected);
  });

  it.each(['', 'not a number', '1,234.50', '1e6', '1234.', '.1234', ' 1234', '1234x'])(
    'leaves non-decimal input unchanged: %s',
    (value) => {
      expect(formatHistoricalNumber(value)).toBe(value);
    },
  );

  it('groups a very long decimal string without losing precision', () => {
    const value = `-${'123'.repeat(30_000)}.0000100`;
    const expected = `-${'123,'.repeat(29_999)}123.0000100`;

    expect(formatHistoricalNumber(value)).toBe(expected);
  });
});
