import { normaliseNumber } from '@shared/utils';

import { ProductVariableEnergyConsumptionData } from 'cca-api';

import { calculateFixedEnergy, calculateVariableEnergy } from './utils';

describe('normaliseNumber', () => {
  it.each([null, undefined, '', '  ', 'abc'])('should return null for %j', (value) => {
    expect(normaliseNumber(value)).toBeNull();
  });

  it.each([
    ['0', 0],
    [0, 0],
    ['12.5', 12.5],
    ['1e3', 1000],
    [42, 42],
  ])('should convert %j to %j', (value, expected) => {
    expect(normaliseNumber(value)).toBe(expected);
  });
});

describe('calculateFixedEnergy', () => {
  it('should keep a missing fixed energy as null', () => {
    expect(calculateFixedEnergy(null)).toBeNull();
  });
});

describe('calculateVariableEnergy', () => {
  it('should count products without energy as 0', () => {
    expect(
      calculateVariableEnergy(
        true,
        'BY_PRODUCT',
        null,
        [{ energy: '5', baselineYear: 2022 }, { baselineYear: 2022 }] as ProductVariableEnergyConsumptionData[],
        2022,
      ),
    ).toBe('5');
  });
});
