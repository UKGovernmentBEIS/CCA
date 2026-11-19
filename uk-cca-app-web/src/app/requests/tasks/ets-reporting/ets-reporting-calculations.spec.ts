import { EtsFuelRowInput, EtsMeasurementType } from './ets-reporting.types';
import {
  buildEtsFuelSummaryRows,
  createEtsFuelDetails,
  ETS_FUEL_ENTRIES,
  getEtsDeliveredUnit,
  getEtsMeasurementUnit,
  mapEtsStandardFuelRows,
  normaliseEtsDecimal,
} from './ets-reporting-calculations';

const customFuel = (overrides: Partial<EtsFuelRowInput> = {}): EtsFuelRowInput => ({
  fuelKey: null,
  fuelType: ' Other fuel ',
  co2ConversionFactor: '0.5',
  deliveredEnergy: '1000',
  primaryEnergyConversionFactor: '1',
  isCustom: true,
  ...overrides,
});

describe('UK ETS fuel calculations', () => {
  it('retains the reference fuel order while excluding electricity and cooling', () => {
    expect(ETS_FUEL_ENTRIES.map(([key]) => key)).toEqual([
      'NATURAL_GAS',
      'LPG',
      'GAS_DIESEL_OIL',
      'FUEL_OIL',
      'KEROSENE',
      'COAL',
      'COKE',
      'PETROL',
      'ETHANE',
      'NAPHTHA',
      'PETROLEUM_COKE',
      'REFINERY_GAS',
    ]);
    expect(mapEtsStandardFuelRows('ENERGY_KWH').every((row) => row.deliveredEnergy === '0')).toBe(true);
  });

  it.each<[EtsMeasurementType, string, string, string, string]>([
    ['ENERGY_KWH', 'kWh', 'kWh', '1000', '0.18254'],
    ['ENERGY_MWH', 'MWh', 'MWh', '1000', '182.54'],
    ['ENERGY_GJ', 'GJ', 'GJ', '1000', '50.7055556'],
    ['CARBON_KG', 'kg', 'kWh', '182.54', '0.18254'],
    ['CARBON_TONNE', 'tonne', 'kWh', '0.18254', '0.18254'],
  ])('calculates natural gas and units for %s', (type, primaryUnit, deliveredUnit, primary, emissions) => {
    const rows = mapEtsStandardFuelRows(type);
    rows[0].deliveredEnergy = '1000';
    const details = createEtsFuelDetails(type, rows);

    expect(details).toEqual({
      standardFuels: { NATURAL_GAS: { deliveredEnergy: '1000', primaryEnergy: primary } },
      nonStandardFuels: [],
      totalPrimaryEnergy: primary,
      totalCo2Emissions: emissions,
    });
    expect(getEtsMeasurementUnit(type)).toBe(primaryUnit);
    expect(getEtsDeliveredUnit(type)).toBe(deliveredUnit);
  });

  it('uses the API 20-decimal GJ factor instead of the rounded display factor', () => {
    const rows = mapEtsStandardFuelRows('ENERGY_GJ');
    expect(rows[0].co2ConversionFactor).toBe('50.70555555555555555556');
    rows[0].deliveredEnergy = '1000000';
    rows[0].co2ConversionFactor = '50.7055556';
    const details = createEtsFuelDetails('ENERGY_GJ', rows);

    expect(details.totalCo2Emissions).toBe('50705.5555556');
    expect(buildEtsFuelSummaryRows('ENERGY_GJ', details)[0].co2ConversionFactor).toBe('50.7055556');
  });

  it.each<EtsMeasurementType>(['ENERGY_KWH', 'ENERGY_MWH', 'ENERGY_GJ', 'CARBON_KG', 'CARBON_TONNE'])(
    'uses custom factors in the entered units for %s',
    (type) => {
      const details = createEtsFuelDetails(type, [customFuel()]);
      expect(details.nonStandardFuels[0].name).toBe('Other fuel');
      expect(details.nonStandardFuels[0].conversionFactor).toBe('0.5');
      expect(details.totalCo2Emissions).toBe('0.5');
      expect(details.totalPrimaryEnergy).toBe(type === 'CARBON_KG' ? '500' : type === 'CARBON_TONNE' ? '0.5' : '1000');
    },
  );

  it('aggregates unrounded primary amounts before HALF_UP serialization', () => {
    const rows = [
      customFuel({ fuelType: 'One', deliveredEnergy: '0.0000001' }),
      customFuel({ fuelType: 'Two', deliveredEnergy: '0.0000001' }),
    ];
    const details = createEtsFuelDetails('CARBON_KG', rows);
    expect(details.nonStandardFuels.map((fuel) => fuel.primaryEnergy)).toEqual(['0.0000001', '0.0000001']);
    expect(details.totalPrimaryEnergy).toBe('0.0000001');
    expect(details.totalCo2Emissions).toBe('0');
  });

  it('converts kg to tonnes exactly once while aggregating standard and custom fuels', () => {
    const rows = mapEtsStandardFuelRows('CARBON_TONNE');
    rows[0].deliveredEnergy = '1000';
    const details = createEtsFuelDetails('CARBON_TONNE', [...rows, customFuel()]);
    expect(details.totalPrimaryEnergy).toBe('0.68254');
    expect(details.totalCo2Emissions).toBe('0.68254');
  });

  it('keeps positive delivered fuels with a zero factor visible in a carbon summary', () => {
    const details = createEtsFuelDetails('CARBON_KG', [customFuel({ co2ConversionFactor: '0' })]);
    expect(details.totalPrimaryEnergy).toBe('0');
    expect(details.totalCo2Emissions).toBe('0');
    expect(buildEtsFuelSummaryRows('CARBON_KG', details)).toEqual([
      expect.objectContaining({ fuelType: 'Other fuel', deliveredEnergy: '1000', primaryEnergy: '0' }),
    ]);
  });

  it('normalizes persisted scientific numbers for form re-entry without rounding invalid excess decimals', () => {
    expect(normaliseEtsDecimal('1E-7')).toBe('0.0000001');
    expect(normaliseEtsDecimal('1.23E+5')).toBe('123000');
    expect(normaliseEtsDecimal('0E-7')).toBe('0');
    expect(normaliseEtsDecimal('1E-8')).toBe('0.00000001');
    expect(normaliseEtsDecimal('invalid')).toBe('invalid');
    expect(normaliseEtsDecimal(null)).toBeNull();
    expect(normaliseEtsDecimal(undefined)).toBeUndefined();
    expect(
      mapEtsStandardFuelRows('ENERGY_KWH', {
        NATURAL_GAS: { deliveredEnergy: '1E-7', primaryEnergy: '1E-7' },
      })[0].deliveredEnergy,
    ).toBe('0.0000001');
  });

  it('serializes plain decimals without trailing zeros and trims custom names', () => {
    const details = createEtsFuelDetails('ENERGY_KWH', [
      customFuel({ deliveredEnergy: '1.2300000', co2ConversionFactor: '0.1000000' }),
    ]);
    expect(details.nonStandardFuels[0]).toEqual({
      name: 'Other fuel',
      conversionFactor: '0.1',
      deliveredEnergy: '1.23',
      primaryEnergy: '1.23',
    });
  });

  it('renders absent fuel details and empty fuel collections', () => {
    expect(buildEtsFuelSummaryRows('ENERGY_KWH')).toEqual([]);
    expect(
      buildEtsFuelSummaryRows('ENERGY_KWH', {
        standardFuels: {},
        nonStandardFuels: [],
        totalPrimaryEnergy: '0',
        totalCo2Emissions: '0',
      }),
    ).toEqual([]);
  });
});
