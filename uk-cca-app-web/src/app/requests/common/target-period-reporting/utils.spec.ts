import { to7DecimalPlacesNumber } from '@shared/utils';
import BigNumber from 'bignumber.js';

import {
  PerformanceDataFacilityCalculatedResults,
  PerformanceDataFacilityInputData,
  PerformanceDataFacilityInputEnergyFuelDetails,
  PerformanceDataFacilityReferenceData,
  ProductVariableEnergyConsumptionData,
} from 'cca-api';

import { ThroughputCalculationInputs } from './target-period-reporting-form.types';
import {
  applyImprovementTarget,
  calculateActualEnergyTotal,
  calculateAdjustedImprovementTarget,
  calculateAdjustedThroughput,
  calculateFacilityImprovementTarget,
  calculateImprovementTarget,
  calculatePrimaryCarbon,
  calculatePrimaryEnergy,
  calculateProductTargetEnergy,
  calculateThroughputAdjustmentFactor,
  calculateThroughputValues,
  calculateWeightedConversionFactor,
  co2ConversionFactorForMeasurement,
  primaryCarbonDisplayUnit,
  resolveCalculatedResults,
  resolveFacilityBaselineYear,
  resolveMeasurementUnit,
  resolveProductEnergyCarbonIntensity,
  roundHalfUpTo7Decimals,
} from './utils';

const EMPTY_CALCULATED_RESULTS: PerformanceDataFacilityCalculatedResults = {
  actualEnergyCarbon: '0',
  targetEnergyCarbon: '0',
  energyCarbonDifference: '0',
  targetImprovement: '0',
  weightedConversionFactor: '0',
  targetCo2Emissions: '0',
  actualCo2Emissions: '0',
  co2EmissionsDifference: '0',
  actualImprovement: '0',
};

function withRequiredEnergyFuelDetails(
  details: Omit<PerformanceDataFacilityInputEnergyFuelDetails, 'atLeastSeventyPercentEnergyUsed'>,
): PerformanceDataFacilityInputEnergyFuelDetails {
  return {
    atLeastSeventyPercentEnergyUsed: false,
    ...details,
  };
}

function withRequiredInputData(data: {
  energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails;
  throughputDetails: {
    actualThroughput?: string;
    targetImprovement?: string;
    adjustedThroughput?: string;
    variableEnergyConsumptionDataByProduct?: PerformanceDataFacilityInputData['throughputDetails']['variableEnergyConsumptionDataByProduct'];
  };
}): PerformanceDataFacilityInputData {
  return {
    ...data,
    throughputDetails: {
      ...data.throughputDetails,
      totalTargetVariableEnergy: '0',
    },
    calculatedResults: EMPTY_CALCULATED_RESULTS,
  };
}

function makeProduct(
  overrides: Partial<ProductVariableEnergyConsumptionData> = {},
): ProductVariableEnergyConsumptionData {
  return {
    productName: 'Test',
    baselineYear: 2022,
    productStatus: 'LIVE',
    energy: '0',
    throughput: '0',
    throughputUnit: 'unit',
    ...overrides,
  };
}

describe('calculateThroughputAdjustmentFactor', () => {
  it('should calculate correctly with values from the Excel example (delivered energy)', () => {
    const gridDelivered = 10_000_000;
    const nonGridDelivered = 1_000_000;
    const chpDelivered = 5_000_678;

    const result = calculateThroughputAdjustmentFactor(gridDelivered, nonGridDelivered, chpDelivered);
    expect(result.toNumber()).toBeCloseTo(0.6874709, 7);
  });

  it('should calculate correctly with a second set of values (delivered energy)', () => {
    const gridDelivered = 15_000_000;
    const nonGridDelivered = 1_200_000;
    const chpDelivered = 5_100_678;

    const result = calculateThroughputAdjustmentFactor(gridDelivered, nonGridDelivered, chpDelivered);
    expect(result.toNumber()).toBeCloseTo(0.7605392, 7);
  });

  it('should return 0 when only CHP delivered energy is provided', () => {
    const result = calculateThroughputAdjustmentFactor(0, 0, 10_000);
    expect(result.toNumber()).toBe(0);
  });

  it('should return 1 when all delivered energy components are zero', () => {
    const result = calculateThroughputAdjustmentFactor(0, 0, 0);
    expect(result.toNumber()).toBe(1);
  });

  it('should produce precise result for large values', () => {
    const result = calculateThroughputAdjustmentFactor(10_000_000, 1_000_000, 5_000_678);
    expect(result.toNumber()).toBeCloseTo(0.6874709, 7);
  });

  it('should handle zero grid electricity with non-zero non-grid', () => {
    const result = calculateThroughputAdjustmentFactor(0, 500_000, 1_000_000);
    expect(result.toNumber()).toBeCloseTo(0.3333333, 7);
  });

  it('should handle all inputs as strings', () => {
    const result = calculateThroughputAdjustmentFactor('10000000', '1000000', '5000678');
    expect(result.toNumber()).toBeCloseTo(0.6874709, 7);
  });

  it('should handle fractional delivered energy', () => {
    const result = calculateThroughputAdjustmentFactor(1000.5, 200.25, 300.123);
    // (1000.5 + 200.25) / (1000.5 + 200.25 + 300.123) = 1200.75 / 1500.873
    expect(result.toNumber()).toBeCloseTo(0.8, 1);
  });

  it('should return 1 when denominator is effectively zero from all-zero inputs', () => {
    const result = calculateThroughputAdjustmentFactor(0, 0, 0.0);
    expect(result.toNumber()).toBe(1);
  });
});

describe('co2ConversionFactorForMeasurement', () => {
  // Grid Electricity base factor: 0.10046 kgCO2e/kWh
  it('should return the factor unchanged for kWh', () => {
    expect(co2ConversionFactorForMeasurement(0.10046, 'kWh').toNumber()).toBeCloseTo(0.10046, 7);
  });

  it('should scale the factor by 1000 for MWh', () => {
    // 0.10046 kgCO2e/kWh × 1000 = 100.46 kgCO2e/MWh
    expect(co2ConversionFactorForMeasurement(0.10046, 'MWh').toNumber()).toBeCloseTo(100.46, 7);
  });

  it('should scale the factor by 1000/3.6 for GJ', () => {
    // 0.10046 kgCO2e/kWh × (1000/3.6), rounded to backend-aligned 5 decimals
    expect(co2ConversionFactorForMeasurement(0.10046, 'GJ').toNumber()).toBeCloseTo(27.9055556, 8);
  });

  it('should treat carbon measurement types (kg/tonne) as kWh', () => {
    // Carbon types use kWh internally, so factor is returned as-is
    expect(co2ConversionFactorForMeasurement(0.18254, 'kg').toNumber()).toBeCloseTo(0.18254, 7);
  });
});

describe('calculatePrimaryEnergy', () => {
  // Adjusted Consumption = deliveredEnergy × primaryEnergyConversionFactor
  it('should multiply grid electricity by its primary factor of 2.1', () => {
    // 10,000,000 kWh × 2.1 = 21,000,000
    expect(calculatePrimaryEnergy(10_000_000, 2.1).toNumber()).toBeCloseTo(21_000_000.0, 7);
  });

  it('should return delivered energy unchanged for fuels with a primary factor of 1.0', () => {
    // 5,000,000 kWh × 1.0 = 5,000,000
    expect(calculatePrimaryEnergy(5_000_000, 1.0).toNumber()).toBeCloseTo(5_000_000.0, 7);
  });

  it('should treat null as zero', () => {
    expect(calculatePrimaryEnergy(null, 2.1).toNumber()).toBe(0);
  });

  it('should treat empty string as zero', () => {
    expect(calculatePrimaryEnergy('', 2.1).toNumber()).toBe(0);
  });

  it('should handle string input', () => {
    expect(calculatePrimaryEnergy('5000000', 1.0).toNumber()).toBeCloseTo(5_000_000, 7);
  });

  it('should handle high-precision decimal delivered energy', () => {
    // 12345.1234567890123 × 2.1 = approx 25924.759259...
    expect(calculatePrimaryEnergy('12345.1234567890123', 2.1).toNumber()).toBeCloseTo(25924.75926, 1);
  });
});

describe('calculatePrimaryCarbon', () => {
  // Primary Carbon = deliveredEnergy × primaryFactor × co2Factor
  it('should calculate primary carbon for grid electricity', () => {
    // 10,000,000 × 2.1 × 0.10046 = 2,109,660 kgCO2e (≈ 2,110 in the Excel table)
    expect(calculatePrimaryCarbon(10_000_000, 2.1, 0.10046).toNumber()).toBeCloseTo(2_109_660.0, 7);
  });

  it('should calculate primary carbon for natural gas', () => {
    // 5,000,000 × 1.0 × 0.18254 = 912,700 kgCO2e (≈ 913 in the Excel table)
    expect(calculatePrimaryCarbon(5_000_000, 1.0, 0.18254).toNumber()).toBeCloseTo(912_700.0, 7);
  });

  it('should convert primary carbon to tCO2e for tonne measurement', () => {
    // 5,000,000 × 1.0 × 0.18254 × 0.001 = 912.7 tCO2e
    expect(calculatePrimaryCarbon(5_000_000, 1.0, 0.18254, 'tonne').toNumber()).toBeCloseTo(912.7, 7);
  });

  it('should resolve the primary carbon display unit from the measurement unit', () => {
    expect(primaryCarbonDisplayUnit('kg')).toBe('kgCO2e');
    expect(primaryCarbonDisplayUnit('tonne')).toBe('tCO2e');
  });

  it('should treat null as zero', () => {
    expect(calculatePrimaryCarbon(null, 2.1, 0.1).toNumber()).toBe(0);
  });

  it('should treat empty string as zero', () => {
    expect(calculatePrimaryCarbon('', 2.1, 0.1).toNumber()).toBe(0);
  });

  it('should handle empty string co2Factor without throwing', () => {
    // Regression test: template passes form control values (strings) to co2Factor.
    // An empty co2ConversionFactor form control (new custom fuel row) must not crash.
    expect(() => calculatePrimaryCarbon(100, 2.1, '')).not.toThrow();
    expect(calculatePrimaryCarbon(100, 2.1, '').toNumber()).toBe(0);
  });

  it('should handle string input', () => {
    expect(calculatePrimaryCarbon('10000000', 2.1, 0.10046).toNumber()).toBeCloseTo(2_109_660, 7);
  });

  it('should handle high-precision decimal values', () => {
    // 12345.1234567890123 × 2.1 × 0.10046 ≈ 2604.4013
    expect(calculatePrimaryCarbon('12345.1234567890123', 2.1, 0.10046).toNumber()).toBeCloseTo(2604.401, 3);
  });

  it('should handle tCO2e conversion with high precision', () => {
    // 12345.67 × 1.0 × 0.5 / 1000 = 6.172835
    expect(calculatePrimaryCarbon('12345.67', 1.0, 0.5, 'tonne').toNumber()).toBeCloseTo(6.172835, 7);
  });
});

describe('calculateAdjustedThroughput', () => {
  it('should return actualThroughput unchanged when usedReportingMechanism is false', () => {
    const result = calculateAdjustedThroughput('8000', 0.6875, false);

    expect(result!.toNumber()).toBeCloseTo(8000, 7);
  });

  it('should apply throughput adjustment factor when usedReportingMechanism is true', () => {
    // 0.6875 × 8000 = 5500
    const result = calculateAdjustedThroughput('8000', 0.6875, true);

    expect(result!.toNumber()).toBeCloseTo(5500, 7);
  });

  it('should return null if actualThroughput is null', () => {
    const result = calculateAdjustedThroughput(null, 0.6875, true);

    expect(result).toBeNull();
  });

  it('should return null if actualThroughput is empty string', () => {
    const result = calculateAdjustedThroughput('', 0.6875, true);

    expect(result).toBeNull();
  });

  it.each(['`', 'abc', '1,000', 'NaN'])('should return null for invalid throughput input %j', (actualThroughput) => {
    expect(() => calculateAdjustedThroughput(actualThroughput, 0.6875, true)).not.toThrow();
    expect(calculateAdjustedThroughput(actualThroughput, 0.6875, true)).toBeNull();
  });

  it('should handle different adjustment factors', () => {
    // 0.5 × 10000 = 5000
    const result = calculateAdjustedThroughput('10000', 0.5, true);

    expect(result!.toNumber()).toBeCloseTo(5000, 7);
  });

  it('should handle high-precision throughput with 15 decimal places', () => {
    // 12345.1234567890123 × 0.6875 = 8487.272376...
    const result = calculateAdjustedThroughput('12345.1234567890123', 0.6875, true);
    expect(result!.toNumber()).toBeCloseTo(8487.272, 3);
  });
});

describe('applyImprovementTarget', () => {
  it('should apply improvement target to intensity × throughput sum', () => {
    // 5000 × (1 - 0.12) = 4400
    const result = applyImprovementTarget(5000, 0.12, true);
    expect(result.toNumber()).toBeCloseTo(4400, 7);
  });

  it('should return 0 when hasVariableEnergy is false', () => {
    const result = applyImprovementTarget(5000, 0.12, false);
    expect(result.toNumber()).toBe(0);
  });

  it('should accept BigNumber input', () => {
    // baselineEnergyIntensity × throughput as BigNumber
    const sum = new BigNumber('6078519').div('97186').times('10000');
    const result = applyImprovementTarget(sum, '0.032097', true);
    // 6078519/97186 × 10000 × (1 - 0.032097)
    expect(result.toNumber()).toBeGreaterThan(605000);
    expect(result.toNumber()).toBeLessThan(605500);
  });

  it('should handle improvement target as a decimal string', () => {
    // Callers pass the target in decimal form (e.g., '0.12' not '12')
    const result = applyImprovementTarget(5000, '0.12', true);
    expect(result.toNumber()).toBeCloseTo(4400, 7);
  });

  it('should return the same sum when improvement target is 0', () => {
    const result = applyImprovementTarget(5000, 0, true);
    expect(result.toNumber()).toBeCloseTo(5000, 7);
  });

  it('should return 0 when improvement target is 100%', () => {
    const result = applyImprovementTarget(5000, 1, true);
    expect(result.toNumber()).toBe(0);
  });

  it('should handle zero sum input', () => {
    const result = applyImprovementTarget(0, 0.12, true);
    expect(result.toNumber()).toBe(0);
  });
});

describe('calculateImprovementTarget', () => {
  it('should return FINAL improvement target directly for non-interim periods', () => {
    const baselineData: Partial<PerformanceDataFacilityReferenceData> = {
      baselineAndTargets: {
        improvements: {
          TP5: '5',
          TP6: '6',
          TP7: '8',
          TP8: '12',
          TP9: '15',
        },
      },
    };

    expect(calculateImprovementTarget(baselineData, 'TP5').toNumber()).toBeCloseTo(0.05, 7);
    expect(calculateImprovementTarget(baselineData, 'TP7').toNumber()).toBeCloseTo(0.08, 7);
  });

  it('should calculate interim target for TP8: (TP8 + TP7) / 2', () => {
    // (12 + 8) / 2 = 20 / 2 = 10 → 10/100 = 0.1
    const baselineData: Partial<PerformanceDataFacilityReferenceData> = {
      baselineAndTargets: {
        improvements: {
          TP7: '8',
          TP8: '12',
        },
      },
    };

    const result = calculateImprovementTarget(baselineData, 'TP8');

    expect(result.toNumber()).toBeCloseTo(0.1, 7);
  });

  it('should calculate interim target for TP9: (TP9 + TP8) / 2', () => {
    // (15 + 12) / 2 = 27 / 2 = 13.5 → 13.5/100 = 0.135
    const baselineData: Partial<PerformanceDataFacilityReferenceData> = {
      baselineAndTargets: {
        improvements: {
          TP8: '12',
          TP9: '15',
        },
      },
    };

    const result = calculateImprovementTarget(baselineData, 'TP9');

    expect(result.toNumber()).toBeCloseTo(0.135, 7);
  });

  it('should handle negative improvement targets', () => {
    // (10 + 8) / 2 = 18 / 2 = 9 → 9/100 = 0.09
    const baselineData: Partial<PerformanceDataFacilityReferenceData> = {
      baselineAndTargets: {
        improvements: {
          TP7: '8',
          TP8: '10',
        },
      },
    };

    const result = calculateImprovementTarget(baselineData, 'TP8');

    expect(result.toNumber()).toBeCloseTo(0.09, 7);
  });

  it('should return 0 when target period improvements are missing', () => {
    const baselineData: Partial<PerformanceDataFacilityReferenceData> = {
      baselineAndTargets: {
        improvements: { TP7: '8' },
      },
    };

    const result = calculateImprovementTarget(baselineData, 'TP5');
    expect(result.toNumber()).toBe(0);
  });
});

describe('resolveProductEnergyCarbonIntensity', () => {
  it('should return 0 when product is undefined', () => {
    expect(resolveProductEnergyCarbonIntensity(undefined).toNumber()).toBe(0);
  });

  it('should use energyCarbonIntensity when available', () => {
    const product = makeProduct({ energyCarbonIntensity: '42.5' });
    expect(resolveProductEnergyCarbonIntensity(product).toNumber()).toBeCloseTo(42.5, 7);
  });

  it('should compute energy / throughput when no energyCarbonIntensity', () => {
    const product = makeProduct({ energy: '500000', throughput: '10000' });
    expect(resolveProductEnergyCarbonIntensity(product).toNumber()).toBeCloseTo(50, 7);
  });

  it('should return 0 when throughput is 0 to prevent division by zero', () => {
    const product = makeProduct({ energy: '500000', throughput: '0' });
    expect(resolveProductEnergyCarbonIntensity(product).toNumber()).toBe(0);
  });

  it('should handle high-precision division', () => {
    const product = makeProduct({ energy: '1234567', throughput: '89123' });
    // 1234567 / 89123 ≈ 13.852395
    expect(resolveProductEnergyCarbonIntensity(product).toNumber()).toBeCloseTo(13.8524, 4);
  });

  it('should handle string values from API payload', () => {
    const product = makeProduct({ energy: '9876543', throughput: '1234' });
    expect(resolveProductEnergyCarbonIntensity(product).toNumber()).toBeCloseTo(8003.6815, 4);
  });
});

describe('calculateThroughputValues', () => {
  it('should not calculate throughput-dependent values for invalid live form input', () => {
    const inputs: ThroughputCalculationInputs = {
      referenceData: {
        baselineAndTargets: {
          improvements: { TP5: '5' },
          baselineEnergyCarbonIntensity: '0.5',
          variableEnergyType: 'TOTALS',
          usedReportingMechanism: false,
        },
      },
      performanceData: withRequiredInputData({
        energyFuelDetails: withRequiredEnergyFuelDetails({
          standardFuels: {
            GRID_ELECTRICITY: { primaryEnergy: '0', deliveredEnergy: '0' },
            NON_GRID_ELECTRICITY: { primaryEnergy: '0', deliveredEnergy: '0' },
          },
          electricitySuppliedFromCHP: '0',
        }),
        throughputDetails: {},
      }),
      reportType: 'FINAL',
      targetPeriodType: 'TP5',
      actualThroughput: '`',
    };

    expect(() => calculateThroughputValues(inputs)).not.toThrow();

    const result = calculateThroughputValues(inputs);
    expect(result.adjustedThroughput).toBeNull();
    expect(result.targetVariableEnergy).toBeNull();
  });

  it('should calculate all values correctly for FINAL report without SRM', () => {
    const inputs: ThroughputCalculationInputs = {
      referenceData: {
        baselineAndTargets: {
          improvements: { TP5: '5', TP7: '8' },
          baselineEnergyCarbonIntensity: '0.5',
          variableEnergyType: 'TOTALS',
          usedReportingMechanism: false,
        },
      },
      performanceData: withRequiredInputData({
        energyFuelDetails: withRequiredEnergyFuelDetails({
          standardFuels: {
            GRID_ELECTRICITY: { primaryEnergy: '0', deliveredEnergy: '0' },
            NON_GRID_ELECTRICITY: { primaryEnergy: '0', deliveredEnergy: '0' },
          },
          electricitySuppliedFromCHP: '0',
        }),
        throughputDetails: { actualThroughput: '8000' },
      }),
      reportType: 'FINAL',
      targetPeriodType: 'TP5',
      actualThroughput: '8000',
    };

    const result = calculateThroughputValues(inputs);

    expect(result.improvementTarget.toNumber()).toBeCloseTo(0.05, 7);
    expect(result.throughputAdjustmentFactor.toNumber()).toBeCloseTo(1, 7);
    expect(result.baselineEnergyIntensity.toNumber()).toBeCloseTo(0.5, 7);
    expect(result.adjustedThroughput!.toNumber()).toBeCloseTo(8000, 7);
    expect(result.targetVariableEnergy!.toNumber()).toBeCloseTo(3800, 7); // 0.5 × 8000 × (1 - 0.05)
  });

  it('should return zero target variable energy for fixed-only facilities when variableEnergyType is null', () => {
    const inputs: ThroughputCalculationInputs = {
      referenceData: {
        baselineAndTargets: {
          improvements: { TP5: '5', TP7: '8' },
          baselineEnergyCarbonIntensity: '0.5',
          variableEnergyType: null,
          usedReportingMechanism: false,
        },
      },
      performanceData: withRequiredInputData({
        energyFuelDetails: withRequiredEnergyFuelDetails({
          standardFuels: {
            GRID_ELECTRICITY: { primaryEnergy: '0', deliveredEnergy: '0' },
            NON_GRID_ELECTRICITY: { primaryEnergy: '0', deliveredEnergy: '0' },
          },
          electricitySuppliedFromCHP: '0',
        }),
        throughputDetails: { actualThroughput: '8000' },
      }),
      reportType: 'FINAL',
      targetPeriodType: 'TP5',
      actualThroughput: '8000',
    };

    const result = calculateThroughputValues(inputs);

    expect(result.targetVariableEnergy!.toNumber()).toBe(0);
  });

  it('should calculate all values correctly for INTERIM report TP8', () => {
    const inputs: ThroughputCalculationInputs = {
      referenceData: {
        baselineAndTargets: {
          improvements: { TP7: '8', TP8: '12' },
          baselineEnergyCarbonIntensity: '0.5',
          variableEnergyType: 'TOTALS',
          usedReportingMechanism: false,
        },
      },
      performanceData: withRequiredInputData({
        energyFuelDetails: withRequiredEnergyFuelDetails({
          standardFuels: {
            GRID_ELECTRICITY: { primaryEnergy: '0', deliveredEnergy: '0' },
            NON_GRID_ELECTRICITY: { primaryEnergy: '0', deliveredEnergy: '0' },
          },
          electricitySuppliedFromCHP: '0',
        }),
        throughputDetails: { actualThroughput: '8000' },
      }),
      reportType: 'INTERIM',
      targetPeriodType: 'TP8',
      actualThroughput: '8000',
    };

    const result = calculateThroughputValues(inputs);

    expect(result.improvementTarget.toNumber()).toBeCloseTo(0.1, 7);
    expect(result.baselineEnergyIntensity.toNumber()).toBeCloseTo(0.5, 7);
    expect(result.adjustedThroughput!.toNumber()).toBeCloseTo(8000, 7);
    expect(result.targetVariableEnergy!.toNumber()).toBeCloseTo(3600, 7); // 0.5 × 8000 × (1 - 0.1)
  });

  it('should apply SRM factor when usedReportingMechanism is true', () => {
    const inputs: ThroughputCalculationInputs = {
      referenceData: {
        baselineAndTargets: {
          improvements: { TP5: '5' },
          baselineEnergyCarbonIntensity: '0.5',
          variableEnergyType: 'TOTALS',
          usedReportingMechanism: true,
        },
      },
      performanceData: withRequiredInputData({
        energyFuelDetails: withRequiredEnergyFuelDetails({
          standardFuels: {
            GRID_ELECTRICITY: { primaryEnergy: '10000000', deliveredEnergy: '10000000' },
            NON_GRID_ELECTRICITY: { primaryEnergy: '1000000', deliveredEnergy: '1000000' },
          },
          electricitySuppliedFromCHP: '5000678',
        }),
        throughputDetails: { actualThroughput: '8000' },
      }),
      reportType: 'FINAL',
      targetPeriodType: 'TP5',
      actualThroughput: '8000',
    };

    const result = calculateThroughputValues(inputs);

    expect(result.throughputAdjustmentFactor.toNumber()).toBeCloseTo(0.6874709, 7);
    expect(result.adjustedThroughput!.toNumber()).toBeCloseTo(5499.767, 3); // 0.6874709 × 8000
  });

  it('should recalculate SRM electricity inputs from delivered energy, not payload primaryEnergy', () => {
    const inputsWithPayloadPrimary: ThroughputCalculationInputs = {
      referenceData: {
        baselineAndTargets: {
          improvements: { TP5: '5' },
          baselineEnergyCarbonIntensity: '0.5',
          variableEnergyType: 'TOTALS',
          usedReportingMechanism: true,
        },
      },
      performanceData: withRequiredInputData({
        energyFuelDetails: withRequiredEnergyFuelDetails({
          standardFuels: {
            GRID_ELECTRICITY: { primaryEnergy: '1', deliveredEnergy: '10000000' },
            NON_GRID_ELECTRICITY: { primaryEnergy: '999999999', deliveredEnergy: '1000000' },
          },
          electricitySuppliedFromCHP: '5000678',
        }),
        throughputDetails: { actualThroughput: '8000' },
      }),
      reportType: 'FINAL',
      targetPeriodType: 'TP5',
      actualThroughput: '8000',
    };

    const result = calculateThroughputValues(inputsWithPayloadPrimary);

    expect(result.throughputAdjustmentFactor.toNumber()).toBeCloseTo(0.6874709, 7);
    expect(result.adjustedThroughput!.toNumber()).toBeCloseTo(5499.767, 3);
  });

  it('should keep calculations in MWh scale when baseline values are provided in MWh', () => {
    const inputs: ThroughputCalculationInputs = {
      referenceData: {
        baselineAndTargets: {
          improvements: { TP5: '5' },
          baselineEnergyCarbonIntensity: '0.0005',
          variableEnergyType: 'TOTALS',
          usedReportingMechanism: false,
        },
      },
      performanceData: withRequiredInputData({
        energyFuelDetails: withRequiredEnergyFuelDetails({
          standardFuels: {
            GRID_ELECTRICITY: { primaryEnergy: '0', deliveredEnergy: '0' },
            NON_GRID_ELECTRICITY: { primaryEnergy: '0', deliveredEnergy: '0' },
          },
          electricitySuppliedFromCHP: '0',
        }),
        throughputDetails: { actualThroughput: '8000' },
      }),
      reportType: 'FINAL',
      targetPeriodType: 'TP5',
      actualThroughput: '8000',
    };

    const result = calculateThroughputValues(inputs);

    expect(result.baselineEnergyIntensity.toNumber()).toBeCloseTo(0.0005, 7);
    expect(result.targetVariableEnergy!.toNumber()).toBeCloseTo(3.8, 7);
  });

  it('should keep calculations in GJ scale when baseline values are provided in GJ', () => {
    const inputs: ThroughputCalculationInputs = {
      referenceData: {
        baselineAndTargets: {
          improvements: { TP5: '5' },
          baselineEnergyCarbonIntensity: '0.0018',
          variableEnergyType: 'TOTALS',
          usedReportingMechanism: false,
        },
      },
      performanceData: withRequiredInputData({
        energyFuelDetails: withRequiredEnergyFuelDetails({
          standardFuels: {
            GRID_ELECTRICITY: { primaryEnergy: '0', deliveredEnergy: '0' },
            NON_GRID_ELECTRICITY: { primaryEnergy: '0', deliveredEnergy: '0' },
          },
          electricitySuppliedFromCHP: '0',
        }),
        throughputDetails: { actualThroughput: '8000' },
      }),
      reportType: 'FINAL',
      targetPeriodType: 'TP5',
      actualThroughput: '8000',
    };

    const result = calculateThroughputValues(inputs);

    expect(result.baselineEnergyIntensity.toNumber()).toBeCloseTo(0.0018, 7);
    expect(result.targetVariableEnergy!.toNumber()).toBeCloseTo(13.68, 7);
  });

  it('should produce the same SRM adjustment factor for equivalent kWh and MWh fuel inputs', () => {
    const kwhInputs: ThroughputCalculationInputs = {
      referenceData: {
        baselineAndTargets: {
          improvements: { TP5: '5' },
          baselineEnergyCarbonIntensity: '0.5',
          variableEnergyType: 'TOTALS',
          usedReportingMechanism: true,
        },
      },
      performanceData: withRequiredInputData({
        energyFuelDetails: withRequiredEnergyFuelDetails({
          standardFuels: {
            GRID_ELECTRICITY: { primaryEnergy: '10000000', deliveredEnergy: '10000000' },
            NON_GRID_ELECTRICITY: { primaryEnergy: '1000000', deliveredEnergy: '1000000' },
          },
          electricitySuppliedFromCHP: '5000678',
        }),
        throughputDetails: { actualThroughput: '8000' },
      }),
      reportType: 'FINAL',
      targetPeriodType: 'TP5',
      actualThroughput: '8000',
    };

    const mwhInputs: ThroughputCalculationInputs = {
      ...kwhInputs,
      referenceData: {
        ...kwhInputs.referenceData,
        baselineAndTargets: {
          ...kwhInputs.referenceData.baselineAndTargets,
          baselineVariableEnergy: '5',
        },
      },
      performanceData: withRequiredInputData({
        ...kwhInputs.performanceData,
        energyFuelDetails: withRequiredEnergyFuelDetails({
          standardFuels: {
            GRID_ELECTRICITY: { primaryEnergy: '10000', deliveredEnergy: '10000' },
            NON_GRID_ELECTRICITY: { primaryEnergy: '1000', deliveredEnergy: '1000' },
          },
          electricitySuppliedFromCHP: '5000.678',
        }),
        throughputDetails: { actualThroughput: '8000' },
      }),
    };

    const kwhResult = calculateThroughputValues(kwhInputs);
    const mwhResult = calculateThroughputValues(mwhInputs);

    expect(mwhResult.throughputAdjustmentFactor.toNumber()).toBeCloseTo(
      kwhResult.throughputAdjustmentFactor.toNumber(),
      7,
    );
    expect(mwhResult.adjustedThroughput!.toNumber()).toBeCloseTo(kwhResult.adjustedThroughput!.toNumber(), 7);
  });

  it('should return all null values when baselineEnergyCarbonIntensity is null', () => {
    const inputs: ThroughputCalculationInputs = {
      referenceData: {
        baselineAndTargets: {
          improvements: { TP5: '5' },
          baselineEnergyCarbonIntensity: null,
          variableEnergyType: 'TOTALS',
          totalThroughput: '10000',
          usedReportingMechanism: false,
        },
      },
      performanceData: withRequiredInputData({
        energyFuelDetails: withRequiredEnergyFuelDetails({
          standardFuels: {
            GRID_ELECTRICITY: { primaryEnergy: '0', deliveredEnergy: '0' },
            NON_GRID_ELECTRICITY: { primaryEnergy: '0', deliveredEnergy: '0' },
          },
          electricitySuppliedFromCHP: '0',
        }),
        throughputDetails: { actualThroughput: '8000' },
      }),
      reportType: 'FINAL',
      targetPeriodType: 'TP5',
      actualThroughput: '8000',
    };

    const result = calculateThroughputValues(inputs);

    expect(result.baselineEnergyIntensity).toBeNull();
    expect(result.targetVariableEnergy).toBeNull();
  });

  it('should compute precise baselineEnergyIntensity from division (regression test for BigNumber round-trip)', () => {
    // Real-world input where 6078519 / 97186 exposes floating-point precision loss
    const inputs: ThroughputCalculationInputs = {
      referenceData: {
        baselineAndTargets: {
          improvements: { TP7: '0.0418062', TP8: '6.3775884', TP9: '6.7509993' },
          baselineVariableEnergy: '6078519',
          totalThroughput: '97186',
          variableEnergyType: 'TOTALS',
          usedReportingMechanism: false,
        },
      },
      performanceData: withRequiredInputData({
        energyFuelDetails: withRequiredEnergyFuelDetails({
          standardFuels: {
            GRID_ELECTRICITY: { primaryEnergy: '11407410', deliveredEnergy: '5432100' },
            NATURAL_GAS: { primaryEnergy: '789012', deliveredEnergy: '789012' },
          },
          electricitySuppliedFromCHP: '0',
        }),
        throughputDetails: { actualThroughput: '10000' },
      }),
      reportType: 'INTERIM',
      targetPeriodType: 'TP8',
      actualThroughput: '10000',
    };

    const result = calculateThroughputValues(inputs);

    expect(result.improvementTarget.toNumber()).toBeCloseTo(0.032097, 6);
    // Throughput adjustment factor: (5432100 + 789012) / (5432100 + 789012 + 0) = 1
    expect(result.throughputAdjustmentFactor.toNumber()).toBe(1);
    // 6078519 / 97186 ≈ 62.545212273372708 — must not be truncated by Number precision
    expect(result.baselineEnergyIntensity.toNumber()).toBeCloseTo(62.54521, 5);
    // No SRM, throughput unchanged
    expect(result.adjustedThroughput!.toNumber()).toBeCloseTo(10000, 7);
    // 62.545212273372708 × 10000 × (1 - 0.032096973) ≈ 605377
    expect(result.targetVariableEnergy!.toNumber()).toBeCloseTo(605377, 1);
  });

  it('should handle missing energy fuel details entirely', () => {
    const inputs: ThroughputCalculationInputs = {
      referenceData: {
        baselineAndTargets: {
          improvements: { TP5: '5' },
          baselineEnergyCarbonIntensity: '0.5',
          variableEnergyType: 'TOTALS',
          usedReportingMechanism: false,
        },
      },
      performanceData: {} as PerformanceDataFacilityInputData,
      reportType: 'FINAL',
      targetPeriodType: 'TP5',
      actualThroughput: '8000',
    };

    const result = calculateThroughputValues(inputs);

    // Defaults to 1 when no energy data
    expect(result.throughputAdjustmentFactor.toNumber()).toBe(1);
    expect(result.baselineEnergyIntensity!.toNumber()).toBe(0.5);
  });
});

describe('calculateFacilityImprovementTarget', () => {
  it('should return final target for FINAL reports', () => {
    const baselineData: Partial<PerformanceDataFacilityReferenceData> = {
      baselineAndTargets: {
        improvements: { TP7: '8' },
      },
    };
    const result = calculateFacilityImprovementTarget(
      baselineData as PerformanceDataFacilityReferenceData,
      'FINAL',
      'TP7',
    );
    expect(result.toNumber()).toBeCloseTo(0.08, 7);
  });

  it('should return interim average for INTERIM reports', () => {
    const baselineData: Partial<PerformanceDataFacilityReferenceData> = {
      baselineAndTargets: {
        improvements: { TP7: '8', TP8: '12' },
      },
    };
    const result = calculateFacilityImprovementTarget(
      baselineData as PerformanceDataFacilityReferenceData,
      'INTERIM',
      'TP8',
    );
    expect(result.toNumber()).toBeCloseTo(0.1, 7);
  });
});

describe('calculateAdjustedImprovementTarget', () => {
  it('should return facility target when product and facility base years match', () => {
    const result = calculateAdjustedImprovementTarget(
      {
        baselineAndTargets: {
          improvements: { TP7: '8', TP8: '12', TP9: '16' },
        },
      } as PerformanceDataFacilityReferenceData,
      'FINAL',
      'TP8',
      2022,
      2022,
    );
    expect(result.toNumber()).toBeCloseTo(0.12, 7);
  });

  it('should calculate adjusted product target based on spec example', () => {
    const result = calculateAdjustedImprovementTarget(
      {
        baselineAndTargets: {
          improvements: { TP7: '8', TP8: '10', TP9: '16' },
        },
      } as PerformanceDataFacilityReferenceData,
      'FINAL',
      'TP8',
      2022,
      2027,
    );
    expect(result.toNumber()).toBeCloseTo(0.010989011, 7);
  });

  it('should use the interim target when rebasing TP8 interim values', () => {
    const result = calculateAdjustedImprovementTarget(
      {
        baselineAndTargets: {
          improvements: { TP7: '8', TP8: '10', TP9: '16' },
        },
      } as PerformanceDataFacilityReferenceData,
      'INTERIM',
      'TP8',
      2022,
      2027,
    );
    expect(result.toNumber()).toBeCloseTo(0, 7);
  });

  it('should calculate adjusted product target for TP9 final when product base year falls within TP9 period', () => {
    // facilityTarget=16%→0.16, totalProgress at productBaseYear(2029)=13%→0.13 → (0.16-0.13)/(1-0.13)
    const result = calculateAdjustedImprovementTarget(
      {
        baselineAndTargets: {
          improvements: { TP7: '8', TP8: '10', TP9: '16' },
        },
      } as PerformanceDataFacilityReferenceData,
      'FINAL',
      'TP9',
      2022,
      2029,
    );
    expect(result.toNumber()).toBeCloseTo(0.034482758, 7);
  });

  it('should use the interim target when rebasing TP9 interim values with product base year in TP8 period', () => {
    // interimTarget=(16+10)/2/100=0.13, totalProgress at productBaseYear(2027)=9% → (0.13-0.09)/(1-0.09)
    const result = calculateAdjustedImprovementTarget(
      {
        baselineAndTargets: {
          improvements: { TP7: '8', TP8: '10', TP9: '16' },
        },
      } as PerformanceDataFacilityReferenceData,
      'INTERIM',
      'TP9',
      2022,
      2027,
    );
    expect(result.toNumber()).toBeCloseTo(0.043956044, 7);
  });

  // Spec worked example: facilityBaseYear=2022, productBaseYear=2027, TP7=8%, TP8=10%, TP9=16%
  // progress = 0.08 + 0.01 = 0.09 (9%)

  it('spec example — TP7 final should be 0 because productBaseYear falls after TP7', () => {
    // MAX(0, (0.08 - 0.09) / (1 - 0.09)) = MAX(0, negative) = 0
    const result = calculateAdjustedImprovementTarget(
      {
        baselineAndTargets: {
          improvements: { TP7: '8', TP8: '10', TP9: '16' },
        },
      } as PerformanceDataFacilityReferenceData,
      'FINAL',
      'TP7',
      2022,
      2027,
    );
    expect(result.toNumber()).toBe(0);
  });

  it('spec example — TP8 interim should be 0 at productBaseYear itself', () => {
    // interimTarget=(10+8)/2/100=0.09, progress=0.09 → (0.09-0.09)/(1-0.09)=0
    const result = calculateAdjustedImprovementTarget(
      {
        baselineAndTargets: {
          improvements: { TP7: '8', TP8: '10', TP9: '16' },
        },
      } as PerformanceDataFacilityReferenceData,
      'INTERIM',
      'TP8',
      2022,
      2027,
    );
    expect(result.toNumber()).toBe(0);
  });

  it('spec example — TP9 final with productBaseYear=2027 → ≈7.692%', () => {
    // (0.16 - 0.09) / (1 - 0.09) = 0.07 / 0.91 ≈ 0.076923
    const result = calculateAdjustedImprovementTarget(
      {
        baselineAndTargets: {
          improvements: { TP7: '8', TP8: '10', TP9: '16' },
        },
      } as PerformanceDataFacilityReferenceData,
      'FINAL',
      'TP9',
      2022,
      2027,
    );
    expect(result.toNumber()).toBeCloseTo(0.076923077, 7);
  });

  it('should calculate adjusted target when productBaseYear < facilityBaseYear (negative progress)', () => {
    // facilityBaseYear=2023, productBaseYear=2022, TP7=6%, TP8=9%, TP9=12%
    // tp7Progress = (2022-2023) = -1, tp7Years = 3, progress = (-1/3)*0.06 = -0.02
    // result = (0.06 - (-0.02)) / (1 - (-0.02)) = 0.08 / 1.02 ≈ 0.07843137
    const result = calculateAdjustedImprovementTarget(
      {
        baselineAndTargets: {
          improvements: { TP7: '6', TP8: '9', TP9: '12' },
        },
      } as PerformanceDataFacilityReferenceData,
      'FINAL',
      'TP7',
      2023,
      2022,
    );
    expect(result.toNumber()).toBeCloseTo(0.078431373, 7);
  });

  it('should match spreadsheet: 2022 product with 2023 facility → 8% sector target', () => {
    // From bug report: facility base year 2023, product base year 2022
    // facility TP7 = 6.122449%, spreadsheet expects 8.000%
    // tp7Progress = (2022-2023)/3 * 0.06122449 = -0.0204081633
    // result = (0.06122449 + 0.0204081633) / (1 + 0.0204081633) = 0.0816326533 / 1.0204081633 = 0.08
    const result = calculateAdjustedImprovementTarget(
      {
        baselineAndTargets: {
          improvements: { TP7: '6.122449', TP8: '10.2040816', TP9: '14.2857143' },
        },
      } as PerformanceDataFacilityReferenceData,
      'FINAL',
      'TP7',
      2023,
      2022,
    );
    expect(result.toNumber()).toBeCloseTo(0.08, 5);
  });

  it('should handle productBaseYear well below facilityBaseYear', () => {
    // facilityBaseYear=2025, productBaseYear=2020, TP7=10%, TP8=14%, TP9=20%
    // tp7Progress = (2020-2025) = -5, tp7Years = (2026-2025) = 1
    // tp8Progress = max(min(2020,2028)-2026, 0) = max(2020-2026, 0) = 0
    // progress = (-5/1)*0.10 = -0.50
    // result = (0.10 - (-0.50)) / (1 - (-0.50)) = 0.60 / 1.50 = 0.40
    const result = calculateAdjustedImprovementTarget(
      {
        baselineAndTargets: {
          improvements: { TP7: '10', TP8: '14', TP9: '20' },
        },
      } as PerformanceDataFacilityReferenceData,
      'FINAL',
      'TP7',
      2025,
      2020,
    );
    expect(result.toNumber()).toBeCloseTo(0.4, 7);
  });

  it('should calculate adjusted TP8 FINAL target when productBaseYear < facilityBaseYear', () => {
    // facilityBaseYear=2023, productBaseYear=2022, TP7=6%, TP8=9%, TP9=12%
    // tp7Progress = (2022-2023)/3 * 0.06 = -0.02
    // result = (0.09 - (-0.02)) / (1 - (-0.02)) = 0.11 / 1.02
    const result = calculateAdjustedImprovementTarget(
      {
        baselineAndTargets: {
          improvements: { TP7: '6', TP8: '9', TP9: '12' },
        },
      } as PerformanceDataFacilityReferenceData,
      'FINAL',
      'TP8',
      2023,
      2022,
    );
    expect(result.toNumber()).toBeCloseTo(0.107843137, 7);
  });

  it('should calculate adjusted TP9 FINAL target when productBaseYear < facilityBaseYear', () => {
    // facilityBaseYear=2023, productBaseYear=2022, TP7=6%, TP8=9%, TP9=12%
    // tp7Progress = (2022-2023)/3 * 0.06 = -0.02
    // result = (0.12 - (-0.02)) / (1 - (-0.02)) = 0.14 / 1.02
    const result = calculateAdjustedImprovementTarget(
      {
        baselineAndTargets: {
          improvements: { TP7: '6', TP8: '9', TP9: '12' },
        },
      } as PerformanceDataFacilityReferenceData,
      'FINAL',
      'TP9',
      2023,
      2022,
    );
    expect(result.toNumber()).toBeCloseTo(0.137254902, 7);
  });

  it('should calculate adjusted TP8 INTERIM target when productBaseYear < facilityBaseYear', () => {
    // facilityBaseYear=2023, productBaseYear=2022, TP7=6%, TP8=9%, TP9=12%
    // interimTarget = (9+6)/2/100 = 0.075
    // tp7Progress = (2022-2023)/3 * 0.06 = -0.02
    // result = (0.075 - (-0.02)) / (1 - (-0.02)) = 0.095 / 1.02
    const result = calculateAdjustedImprovementTarget(
      {
        baselineAndTargets: {
          improvements: { TP7: '6', TP8: '9', TP9: '12' },
        },
      } as PerformanceDataFacilityReferenceData,
      'INTERIM',
      'TP8',
      2023,
      2022,
    );
    expect(result.toNumber()).toBeCloseTo(0.093137255, 7);
  });

  it('should calculate adjusted TP9 INTERIM target when productBaseYear < facilityBaseYear', () => {
    // facilityBaseYear=2023, productBaseYear=2022, TP7=6%, TP8=9%, TP9=12%
    // interimTarget = (12+9)/2/100 = 0.105
    // tp7Progress = (2022-2023)/3 * 0.06 = -0.02
    // result = (0.105 - (-0.02)) / (1 - (-0.02)) = 0.125 / 1.02
    const result = calculateAdjustedImprovementTarget(
      {
        baselineAndTargets: {
          improvements: { TP7: '6', TP8: '9', TP9: '12' },
        },
      } as PerformanceDataFacilityReferenceData,
      'INTERIM',
      'TP9',
      2023,
      2022,
    );
    expect(result.toNumber()).toBeCloseTo(0.12254902, 7);
  });
});

describe('calculateProductTargetEnergy', () => {
  it('should calculate per-product target energy from intensity, adjusted throughput and target (decimal)', () => {
    const result = calculateProductTargetEnergy(250, 5000, 0.041666667);
    expect(result.toNumber()).toBeCloseTo(1197916.66625, 5);
  });

  it('should return zero when adjusted throughput is zero', () => {
    const result = calculateProductTargetEnergy(5000, 0, 0.12);
    expect(result.toNumber()).toBe(0);
  });
});

describe('calculateActualEnergyTotal', () => {
  it('should sum primary energy from standard fuels only', () => {
    const energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails = {
      atLeastSeventyPercentEnergyUsed: false,
      standardFuels: {
        GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '21000000' },
        NATURAL_GAS: { deliveredEnergy: '0', primaryEnergy: '5000000' },
      },
      nonStandardFuels: [],
    };

    const result = calculateActualEnergyTotal(energyFuelDetails);
    expect(result.toNumber()).toBeCloseTo(26_000_000, 7);
  });

  it('should sum primary energy from non-standard fuels only', () => {
    const energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails = {
      atLeastSeventyPercentEnergyUsed: false,
      standardFuels: {
        GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '0' },
        NATURAL_GAS: { deliveredEnergy: '0', primaryEnergy: '0' },
      },
      nonStandardFuels: [
        { name: 'Custom fuel 1', conversionFactor: '0', deliveredEnergy: '0', primaryEnergy: '1000000' },
        { name: 'Custom fuel 2', conversionFactor: '0', deliveredEnergy: '0', primaryEnergy: '2000000' },
      ],
    };

    const result = calculateActualEnergyTotal(energyFuelDetails);

    expect(result.toNumber()).toBeCloseTo(3_000_000, 7);
  });

  it('should sum primary energy from both standard and non-standard fuels', () => {
    const energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails = {
      atLeastSeventyPercentEnergyUsed: false,
      standardFuels: {
        GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '21000000' },
        NATURAL_GAS: { deliveredEnergy: '0', primaryEnergy: '5000000' },
      },
      nonStandardFuels: [
        { name: 'Custom fuel', conversionFactor: '0', deliveredEnergy: '0', primaryEnergy: '1000000' },
      ],
    };

    const result = calculateActualEnergyTotal(energyFuelDetails);

    expect(result.toNumber()).toBeCloseTo(27_000_000, 7);
  });

  it('should ignore zero values', () => {
    const energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails = {
      atLeastSeventyPercentEnergyUsed: false,
      standardFuels: {
        GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '21000000' },
        NATURAL_GAS: { deliveredEnergy: '0', primaryEnergy: '0' },
        COAL: { deliveredEnergy: '0', primaryEnergy: '0' },
      },
      nonStandardFuels: [
        { name: 'Custom fuel 1', conversionFactor: '0', deliveredEnergy: '0', primaryEnergy: '0' },
        { name: 'Custom fuel 2', conversionFactor: '0', deliveredEnergy: '0', primaryEnergy: '1000000' },
      ],
    };

    const result = calculateActualEnergyTotal(energyFuelDetails);

    expect(result.toNumber()).toBeCloseTo(22_000_000, 7);
  });

  it('should handle string primary energy inputs', () => {
    const energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails = {
      atLeastSeventyPercentEnergyUsed: false,
      standardFuels: {
        GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '21000000' },
        NATURAL_GAS: { deliveredEnergy: '0', primaryEnergy: '5000000' },
      },
      nonStandardFuels: [],
    };

    const result = calculateActualEnergyTotal(energyFuelDetails);

    expect(result.toNumber()).toBeCloseTo(26_000_000, 7);
  });

  it('should return 0 when all values are zero or missing', () => {
    const energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails = {
      atLeastSeventyPercentEnergyUsed: false,
      standardFuels: {
        GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '0' },
      },
      nonStandardFuels: [],
    };

    const result = calculateActualEnergyTotal(energyFuelDetails);

    expect(result.toNumber()).toBe(0);
  });

  it('should handle undefined or null objects gracefully', () => {
    const energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails = {
      atLeastSeventyPercentEnergyUsed: false,
      standardFuels: undefined,
      nonStandardFuels: undefined,
    };

    const result = calculateActualEnergyTotal(energyFuelDetails);

    expect(result.toNumber()).toBe(0);
  });

  it('should treat empty string primary energy as zero', () => {
    const energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails = {
      atLeastSeventyPercentEnergyUsed: false,
      standardFuels: {
        GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '' },
        NATURAL_GAS: { deliveredEnergy: '0', primaryEnergy: '5000000' },
      },
      nonStandardFuels: [],
    };

    const result = calculateActualEnergyTotal(energyFuelDetails);

    expect(result.toNumber()).toBeCloseTo(5_000_000, 7);
  });

  it('should handle negative primary energy values as valid input', () => {
    const energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails = {
      atLeastSeventyPercentEnergyUsed: false,
      standardFuels: {
        GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '1000000' },
        NATURAL_GAS: { deliveredEnergy: '0', primaryEnergy: '-500000' },
      },
      nonStandardFuels: [],
    };

    const result = calculateActualEnergyTotal(energyFuelDetails);

    // 1000000 + (-500000) = 500000
    expect(result.toNumber()).toBeCloseTo(500_000, 7);
  });
});

describe('roundHalfUpTo7Decimals', () => {
  it('should round values to 7 decimal places', () => {
    expect(roundHalfUpTo7Decimals(1.234567891)).toBe('1.2345679');
  });

  it('should trim trailing zeros after rounding', () => {
    expect(roundHalfUpTo7Decimals(1.5)).toBe('1.5');
    expect(roundHalfUpTo7Decimals(2)).toBe('2');
  });

  it('should return zero for missing values', () => {
    expect(roundHalfUpTo7Decimals(undefined)).toBe('0');
  });

  it('should handle empty string as zero', () => {
    expect(roundHalfUpTo7Decimals('')).toBe('0');
  });

  it('should handle null as zero', () => {
    expect(roundHalfUpTo7Decimals(null)).toBe('0');
  });

  it('should handle string input', () => {
    expect(roundHalfUpTo7Decimals('1.234567891')).toBe('1.2345679');
  });

  it('should not introduce floating-point errors on simple values', () => {
    expect(roundHalfUpTo7Decimals(0.076923077)).toBe('0.0769231');
    expect(roundHalfUpTo7Decimals(0.041666667)).toBe('0.0416667');
    expect(roundHalfUpTo7Decimals(0.1)).toBe('0.1');
  });

  it('should round half-up correctly at the 7-decimal boundary', () => {
    // 0.12345675 → rounds to 0.1234568 (half-up)
    expect(roundHalfUpTo7Decimals(0.12345675)).toBe('0.1234568');
    // 0.12345674 → rounds to 0.1234567
    expect(roundHalfUpTo7Decimals(0.12345674)).toBe('0.1234567');
  });

  it('should strip trailing zeros', () => {
    expect(roundHalfUpTo7Decimals(1.0)).toBe('1');
    expect(roundHalfUpTo7Decimals(1.5)).toBe('1.5');
    expect(roundHalfUpTo7Decimals(0)).toBe('0');
  });

  it('should handle negative values', () => {
    expect(roundHalfUpTo7Decimals(-1.234567891)).toBe('-1.2345679');
    expect(roundHalfUpTo7Decimals(-0.5)).toBe('-0.5');
  });

  it('should handle 15 decimal places without floating-point artifacts', () => {
    // 0.123456789012345 → rounds to 0.1234568 (half-up at 7th decimal)
    expect(roundHalfUpTo7Decimals('0.123456789012345')).toBe('0.1234568');
  });

  it('should handle BigNumber input directly', () => {
    const bn = new BigNumber('9876543').div('12345').times('2.5');
    // 9876543 / 12345 = 800.044713... × 2.5 = 2000.109963... → rounded to 7dp
    expect(roundHalfUpTo7Decimals(bn)).toBe('2000.1099635');
  });

  it('should round 0.5 up (half-up, not half-even)', () => {
    expect(roundHalfUpTo7Decimals('0.00000005')).toBe('0.0000001');
    expect(roundHalfUpTo7Decimals('-0.00000005')).toBe('-0.0000001');
  });
});

describe('to7DecimalPlacesNumber', () => {
  it('should round values to 7 decimal places using half-up', () => {
    expect(to7DecimalPlacesNumber(new BigNumber('1.234567891'))).toBeCloseTo(1.2345679, 7);
    expect(to7DecimalPlacesNumber(new BigNumber('0.12345678'))).toBeCloseTo(0.1234568, 7);
  });

  it('should round half-up at the 7-decimal boundary', () => {
    expect(to7DecimalPlacesNumber(new BigNumber('0.12345675'))).toBeCloseTo(0.1234568, 7);
    expect(to7DecimalPlacesNumber(new BigNumber('0.12345674'))).toBeCloseTo(0.1234567, 7);
  });

  it('should return 0 for null, undefined, or NaN', () => {
    expect(to7DecimalPlacesNumber(null)).toBe(0);
    expect(to7DecimalPlacesNumber(undefined)).toBe(0);
    expect(to7DecimalPlacesNumber(new BigNumber(NaN))).toBe(0);
  });

  it('should match roundHalfUpTo7Decimals for BigNumber values', () => {
    const testValues = ['123.456789012345', '0.00000005', '9999999.9999999', '0.142857142857'];

    for (const val of testValues) {
      const bn = new BigNumber(val);
      const displayNumber = to7DecimalPlacesNumber(bn);
      const apiString = roundHalfUpTo7Decimals(bn);
      const displayStr = displayNumber.toFixed(7);
      const apiStr = Number(apiString).toFixed(7);

      expect(displayStr).toBe(apiStr);
    }
  });

  it('should handle values with many decimal places consistently', () => {
    const bn = new BigNumber('605377').times('999.99999').times('0.92').div('1000');
    const displayNumber = to7DecimalPlacesNumber(bn);
    const apiString = roundHalfUpTo7Decimals(bn);

    expect(Number(apiString)).toBeCloseTo(displayNumber, 7);
    expect(displayNumber.toFixed(7)).toBe(Number(apiString).toFixed(7));
  });
});

describe('calculateWeightedConversionFactor', () => {
  it('should calculate energy-based weighted conversion factor', () => {
    // Sum(Primary energy × CO2 factor) / Actual energy total
    // (21000000 × 0.10046) + (5000000 × 0.18254) = 2109660 + 912700 = 3022360
    // 3022360 / 26000000 = 0.11624...
    const energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails = {
      atLeastSeventyPercentEnergyUsed: false,
      standardFuels: {
        GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '21000000' },
        NATURAL_GAS: { deliveredEnergy: '0', primaryEnergy: '5000000' },
      },
      nonStandardFuels: [],
    };

    const result = calculateWeightedConversionFactor(energyFuelDetails, false);

    expect(result.toNumber()).toBeCloseTo(0.11624, 5);
  });

  it('should calculate carbon-based weighted conversion factor (primary CO2, denominator is delivered × primary factor)', () => {
    // For carbon-based: numerator = sum of primary CO2, denominator = sumDeliveredTimesPrimaryFactor
    // GRID_ELECTRICITY: deliveredEnergy = 1000, primaryFactor = 2.1, co2Factor = 0.10046
    // NATURAL_GAS: deliveredEnergy = 500, primaryFactor = 1, co2Factor = 0.18254
    // primary CO2 = deliveredEnergy × primaryFactor × co2Factor
    // GRID_ELECTRICITY: 1000 × 2.1 × 0.10046 = 210.966
    // NATURAL_GAS: 500 × 1 × 0.18254 = 91.27
    // Numerator = 210.966 + 91.27 = 302.236
    // Denominator = (1000 × 2.1) + (500 × 1) = 2100 + 500 = 2600
    // Weighted factor = 302.236 / 2600 = 0.11624461538461538
    const energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails = {
      atLeastSeventyPercentEnergyUsed: false,
      standardFuels: {
        GRID_ELECTRICITY: { deliveredEnergy: '1000', primaryEnergy: '210.966' }, // primaryEnergy field is used for CO2 here
        NATURAL_GAS: { deliveredEnergy: '500', primaryEnergy: '91.27' },
      },
      nonStandardFuels: [],
    };

    const result = calculateWeightedConversionFactor(energyFuelDetails, true);
    expect(result.toNumber()).toBeCloseTo(0.1162446, 7);
  });

  it('should fail if denominator is sum of primary CO2 (incorrect)', () => {
    // If someone (incorrectly) uses sum of primary CO2 as denominator, result would be 1
    // This test ensures the denominator is delivered × primary factor, not sum of primary CO2
    const energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails = {
      atLeastSeventyPercentEnergyUsed: false,
      standardFuels: {
        GRID_ELECTRICITY: { deliveredEnergy: '1000', primaryEnergy: '210.966' },
        NATURAL_GAS: { deliveredEnergy: '500', primaryEnergy: '91.27' },
      },
      nonStandardFuels: [],
    };
    // If denominator were 302.236, result would be 1
    const result = calculateWeightedConversionFactor(energyFuelDetails, true);
    expect(result.toNumber()).not.toBeCloseTo(1, 7);
  });

  it('should include non-standard fuels in weighted calculation', () => {
    // With non-standard fuel: 1000000 kWh with 0.2 CO2 factor
    // (21000000 × 0.10046) + (5000000 × 0.18254) + (1000000 × 0.2) = 3222360
    // 3222360 / 27000000 = 0.11935...
    const energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails = {
      atLeastSeventyPercentEnergyUsed: false,
      standardFuels: {
        GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '21000000' },
        NATURAL_GAS: { deliveredEnergy: '0', primaryEnergy: '5000000' },
      },
      nonStandardFuels: [
        { name: 'Custom fuel', conversionFactor: '0.2', deliveredEnergy: '0', primaryEnergy: '1000000' },
      ],
    };

    const result = calculateWeightedConversionFactor(energyFuelDetails, false);

    expect(result.toNumber()).toBeCloseTo(0.11935, 5);
  });

  it('should return 0 when actual energy total is 0', () => {
    const energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails = {
      atLeastSeventyPercentEnergyUsed: false,
      standardFuels: {
        GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '0' },
        NATURAL_GAS: { deliveredEnergy: '0', primaryEnergy: '0' },
      },
      nonStandardFuels: [],
    };

    const result = calculateWeightedConversionFactor(energyFuelDetails, false);

    expect(result.toNumber()).toBe(0);
  });

  it('should ignore zero-value fuels in weighting', () => {
    // Only GRID_ELECTRICITY contributes
    // (21000000 × 0.10046) / 21000000 = 0.10046
    const energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails = {
      atLeastSeventyPercentEnergyUsed: false,
      standardFuels: {
        GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '21000000' },
        NATURAL_GAS: { deliveredEnergy: '0', primaryEnergy: '0' },
      },
      nonStandardFuels: [],
    };

    const result = calculateWeightedConversionFactor(energyFuelDetails, false);

    expect(result.toNumber()).toBeCloseTo(0.10046, 7);
  });

  it('should use measurement unit-specific factors for MWh', () => {
    const energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails = {
      atLeastSeventyPercentEnergyUsed: false,
      standardFuels: {
        GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '21000' },
        NATURAL_GAS: { deliveredEnergy: '0', primaryEnergy: '5000' },
      },
      nonStandardFuels: [],
    };

    const result = calculateWeightedConversionFactor(energyFuelDetails, false, 'MWh');

    expect(result.toNumber()).toBeCloseTo(116.2446154, 7);
  });

  it('should use measurement unit-specific factors for GJ', () => {
    const energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails = {
      atLeastSeventyPercentEnergyUsed: false,
      standardFuels: {
        GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '75600' },
        NATURAL_GAS: { deliveredEnergy: '0', primaryEnergy: '18000' },
      },
      nonStandardFuels: [],
    };

    const result = calculateWeightedConversionFactor(energyFuelDetails, false, 'GJ');

    expect(result.toNumber()).toBeCloseTo(32.290171, 7);
  });

  it('should treat empty string delivered and primary energy as zero', () => {
    const energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails = {
      atLeastSeventyPercentEnergyUsed: false,
      standardFuels: {
        GRID_ELECTRICITY: { deliveredEnergy: '', primaryEnergy: '21000000' },
        NATURAL_GAS: { deliveredEnergy: '', primaryEnergy: '' },
      },
      nonStandardFuels: [],
    };

    const result = calculateWeightedConversionFactor(energyFuelDetails, false);

    // Only GRID_ELECTRICITY contributes: 21000000 × 0.10046 / 21000000 = 0.10046
    expect(result.toNumber()).toBeCloseTo(0.10046, 7);
  });

  it('should return 0 for carbon measurement when all values are empty strings', () => {
    const energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails = {
      atLeastSeventyPercentEnergyUsed: false,
      standardFuels: {
        GRID_ELECTRICITY: { deliveredEnergy: '', primaryEnergy: '' },
        NATURAL_GAS: { deliveredEnergy: '', primaryEnergy: '' },
      },
      nonStandardFuels: [],
    };

    const result = calculateWeightedConversionFactor(energyFuelDetails, true);

    expect(result.toNumber()).toBe(0);
  });

  it('should handle GJ measurement unit with carbon-based calculation', () => {
    const energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails = {
      atLeastSeventyPercentEnergyUsed: false,
      standardFuels: {
        GRID_ELECTRICITY: { deliveredEnergy: '75600', primaryEnergy: '75600' },
      },
      nonStandardFuels: [],
    };

    // Carbon-based with GJ: delivered × primaryFactor × scaled-CO2-factor / (delivered × primaryFactor)
    const result = calculateWeightedConversionFactor(energyFuelDetails, true, 'GJ');
    // Grid: 75600 × 1 × (0.10046 × 1000 / 3.6) / 75600 ≈ 27.9056
    expect(result.toNumber()).toBeGreaterThan(27);
    expect(result.toNumber()).toBeLessThan(28);
  });
});

describe('resolveMeasurementUnit', () => {
  it('should resolve API measurement type enum to display unit', () => {
    const referenceData: Partial<PerformanceDataFacilityReferenceData> = {
      baselineAndTargets: {
        measurementType: 'ENERGY_MWH',
      },
    };

    expect(resolveMeasurementUnit(referenceData as PerformanceDataFacilityReferenceData)).toBe('MWh');
  });

  it('should default to kWh when measurement type is missing', () => {
    expect(resolveMeasurementUnit(undefined)).toBe('kWh');
  });
});

describe('resolveCalculatedResults', () => {
  it('should prefer persisted calculated results and fallback to derived for missing fields', () => {
    const derivedResults: PerformanceDataFacilityCalculatedResults = {
      actualEnergyCarbon: '120',
      targetEnergyCarbon: '90',
      energyCarbonDifference: '30',
      targetImprovement: '0.12',
      weightedConversionFactor: '0.2',
      targetCo2Emissions: '0.09',
      actualCo2Emissions: '0.12',
      co2EmissionsDifference: '0.03',
      actualImprovement: '0.25',
      targetPeriodResultType: 'TARGET_NOT_MET',
      buyOutRequired: '8',
    };

    const resolved = resolveCalculatedResults(derivedResults);

    expect(resolved.actualEnergyCarbon).toBe('120');
    expect(resolved.targetEnergyCarbon).toBe('90');
    expect(resolved.targetPeriodResultType).toBe('TARGET_NOT_MET');
    expect(resolved.buyOutRequired).toBe('8');
  });

  it('should return derived results when persisted results are absent', () => {
    const derivedResults: PerformanceDataFacilityCalculatedResults = {
      actualEnergyCarbon: '1',
      targetEnergyCarbon: '2',
      energyCarbonDifference: '-1',
      targetImprovement: '3',
      weightedConversionFactor: '4',
      targetCo2Emissions: '5',
      actualCo2Emissions: '6',
      co2EmissionsDifference: '1',
      actualImprovement: '7',
    };

    expect(resolveCalculatedResults(derivedResults)).toEqual(derivedResults);
  });
});

describe('resolveFacilityBaselineYear', () => {
  it('should return null for an empty array', () => {
    expect(resolveFacilityBaselineYear([])).toBeNull();
  });

  it('should return the baseline year when there is a single product', () => {
    expect(resolveFacilityBaselineYear([makeProduct({ baselineYear: 2023 })])).toBe(2023);
  });

  it('should return the minimum baseline year when products have different years', () => {
    const products = [
      makeProduct({ productName: 'A', baselineYear: 2025 }),
      makeProduct({ productName: 'B', baselineYear: 2023 }),
      makeProduct({ productName: 'C', baselineYear: 2024 }),
    ];
    expect(resolveFacilityBaselineYear(products)).toBe(2023);
  });

  it('should return the baseline year when all products share the same year', () => {
    const products = [
      makeProduct({ productName: 'A', baselineYear: 2022 }),
      makeProduct({ productName: 'B', baselineYear: 2022 }),
    ];
    expect(resolveFacilityBaselineYear(products)).toBe(2022);
  });
});
