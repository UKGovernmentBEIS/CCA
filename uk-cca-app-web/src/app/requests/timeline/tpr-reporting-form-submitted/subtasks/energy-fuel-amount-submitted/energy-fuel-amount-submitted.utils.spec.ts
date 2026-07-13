import {
  buildSubmittedEnergyFuelRows,
  calculateSubmittedThroughputAdjustmentFactor,
  resolveCo2FactorUnit,
  resolveMeasurementTypeUnit,
} from './energy-fuel-amount-submitted.utils';

describe('energy-fuel-amount-submitted utils', () => {
  it('should resolve measurement enum to display unit', () => {
    expect(resolveMeasurementTypeUnit('ENERGY_MWH')).toBe('MWh');
    expect(resolveMeasurementTypeUnit('CARBON_KG')).toBe('kg');
    expect(resolveMeasurementTypeUnit()).toBe('kWh');
  });

  it('should resolve CO2 factor unit based on measurement unit', () => {
    expect(resolveCo2FactorUnit('kg')).toBe('kWh');
    expect(resolveCo2FactorUnit('tonne')).toBe('kWh');
    expect(resolveCo2FactorUnit('MWh')).toBe('MWh');
  });

  it('should map submitted fuels and filter zero delivered energy rows', () => {
    const rows = buildSubmittedEnergyFuelRows({
      fuels: [
        {
          name: 'Natural gas',
          fixedConversionFactorCode: 'NATURAL_GAS',
          conversionFactor: '0.18254',
          deliveredEnergy: '0',
          primaryConversionFactor: '1',
          primaryEnergy: '0',
        },
        {
          name: 'Custom fuel',
          conversionFactor: '0.5',
          deliveredEnergy: '12.5',
          primaryConversionFactor: '1',
          primaryEnergy: '12.5',
        },
      ],
      atLeastSeventyPercentEnergyUsed: true,
    });

    expect(rows).toEqual([
      {
        fuelType: 'Custom fuel',
        co2ConversionFactor: 0.5,
        deliveredEnergy: 12.5,
        primaryEnergyConversionFactor: 1,
        primaryEnergy: 12.5,
        isCustom: true,
      },
    ]);
  });

  it('should calculate throughput adjustment factor from submitted fuel details', () => {
    const result = calculateSubmittedThroughputAdjustmentFactor({
      fuels: [
        {
          name: 'Grid electricity',
          fixedConversionFactorCode: 'GRID_ELECTRICITY',
          conversionFactor: '0.10046',
          deliveredEnergy: '100',
          primaryConversionFactor: '2.1',
          primaryEnergy: '210',
        },
        {
          name: 'Non-grid electricity',
          fixedConversionFactorCode: 'NON_GRID_ELECTRICITY',
          conversionFactor: '0',
          deliveredEnergy: '50',
          primaryConversionFactor: '1',
          primaryEnergy: '50',
        },
      ],
      atLeastSeventyPercentEnergyUsed: true,
      electricitySuppliedFromCHP: '30',
    });

    expect(result).toBeCloseTo(150 / 180, 7);
  });

  it('should keep submitted delivered energy unchanged for tonne values', () => {
    const rows = buildSubmittedEnergyFuelRows(
      {
        fuels: [
          {
            name: 'Natural gas',
            fixedConversionFactorCode: 'NATURAL_GAS',
            conversionFactor: '0.18254',
            deliveredEnergy: '1.8',
            primaryConversionFactor: '1',
            primaryEnergy: '0.328572',
          },
        ],
        atLeastSeventyPercentEnergyUsed: true,
      },
      'tonne',
    );

    expect(rows).toEqual([
      {
        fuelType: 'Natural gas',
        co2ConversionFactor: 0.18254,
        deliveredEnergy: 1.8,
        primaryEnergyConversionFactor: 1,
        primaryEnergy: 0.000328572,
        isCustom: false,
      },
    ]);
  });

  it('should calculate throughput using submitted CHP and delivered energy values unchanged', () => {
    const result = calculateSubmittedThroughputAdjustmentFactor({
      fuels: [
        {
          name: 'Grid electricity',
          fixedConversionFactorCode: 'GRID_ELECTRICITY',
          conversionFactor: '0.10046',
          deliveredEnergy: '0.1',
          primaryConversionFactor: '2.1',
          primaryEnergy: '0.21',
        },
        {
          name: 'Non-grid electricity',
          fixedConversionFactorCode: 'NON_GRID_ELECTRICITY',
          conversionFactor: '0',
          deliveredEnergy: '0.05',
          primaryConversionFactor: '1',
          primaryEnergy: '0.05',
        },
      ],
      atLeastSeventyPercentEnergyUsed: true,
      electricitySuppliedFromCHP: '0.03',
    });

    expect(result).toBeCloseTo(0.15 / 0.18, 7);
  });
});
