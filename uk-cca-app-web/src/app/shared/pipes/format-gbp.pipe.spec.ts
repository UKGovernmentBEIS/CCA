import { FormatGbpPipe } from './format-gbp.pipe';

describe('GBP display formatting', () => {
  const pipe = new FormatGbpPipe();

  it.each([
    ['0', '0.00'],
    [0, '0.00'],
    ['0.0000000', '0.00'],
    ['0E-7', '0.00'],
    ['37', '37.00'],
    ['120.5', '120.50'],
    ['1250.25', '1,250.25'],
    ['1.004', '1.00'],
    ['1.005', '1.01'],
    ['999999999999999999.99', '999,999,999,999,999,999.99'],
    ['9007199254740993.125', '9,007,199,254,740,993.13'],
  ])('formats %s as %s without floating-point precision loss', (value, expected) => {
    expect(pipe.transform(value)).toBe(expected);
  });

  it.each([null, undefined, ''])('leaves a missing amount blank (%s)', (value) => {
    expect(pipe.transform(value)).toBe('');
  });

  it.each(['abc', '1,000', ' ', '1.2.3', 'NaN', 'Infinity', '-Infinity', NaN, Infinity, -Infinity, '1e999999999'])(
    'leaves an invalid or non-finite amount blank without throwing (%s)',
    (value) => {
      expect(pipe.transform(value)).toBe('');
    },
  );
});
