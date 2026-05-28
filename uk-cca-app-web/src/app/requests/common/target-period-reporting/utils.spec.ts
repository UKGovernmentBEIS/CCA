import {
  PerformanceDataFacilityCalculatedResults,
  PerformanceDataFacilityInputData,
  PerformanceDataFacilityInputEnergyFuelDetails,
  PerformanceDataFacilityReferenceData,
} from 'cca-api';

import { ThroughputCalculationInputs } from './target-period-reporting-form.types';
import {
  calculateActualCo2Emissions,
  calculateActualEnergyTotal,
  calculateAdjustedImprovementTargetForProduct,
  calculateAdjustedThroughput,
  calculateFacilityImprovementTarget,
  calculateImprovementTarget,
  calculatePrimaryCarbon,
  calculatePrimaryEnergy,
  calculateTargetEnergyForProduct,
  calculateThroughputAdjustmentFactor,
  calculateThroughputValues,
  calculateWeightedConversionFactor,
  co2ConversionFactorForMeasurement,
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

describe('calculateThroughputAdjustmentFactor', () => {
  it('should calculate correctly with values from the Excel example', () => {
    const gridElectricity = 10_000_000;
    const nonGridElectricity = 1_000_000;
    const chpElectricity = 5_000_678;

    const result = calculateThroughputAdjustmentFactor(gridElectricity, nonGridElectricity, chpElectricity);

    expect(result).toBeCloseTo(0.6874709, 7);
  });

  it('should calculate correctly with a second set of values', () => {
    const gridElectricity = 15_000_000;
    const nonGridElectricity = 1_200_000;
    const chpElectricity = 5_100_678;

    const result = calculateThroughputAdjustmentFactor(gridElectricity, nonGridElectricity, chpElectricity);

    expect(result).toBeCloseTo(0.7605392, 7);
  });

  it('should return 0 when only CHP electricity is provided', () => {
    const result = calculateThroughputAdjustmentFactor(0, 0, 10_000);

    expect(result).toBe(0);
  });

  it('should return 1 when all electricity components are zero', () => {
    const result = calculateThroughputAdjustmentFactor(0, 0, 0);

    expect(result).toBe(1);
  });
});

describe('co2ConversionFactorForMeasurement', () => {
  // Grid Electricity base factor: 0.10046 kgCO2e/kWh
  it('should return the factor unchanged for kWh', () => {
    expect(co2ConversionFactorForMeasurement(0.10046, 'kWh')).toBeCloseTo(0.10046, 7);
  });

  it('should scale the factor by 1000 for MWh', () => {
    // 0.10046 kgCO2e/kWh × 1000 = 100.46 kgCO2e/MWh
    expect(co2ConversionFactorForMeasurement(0.10046, 'MWh')).toBeCloseTo(100.46, 7);
  });

  it('should scale the factor by 1000/3.6 for GJ', () => {
    // 0.10046 kgCO2e/kWh × (1000/3.6) = 27.9055556 kgCO2e/GJ
    expect(co2ConversionFactorForMeasurement(0.10046, 'GJ')).toBeCloseTo(27.9055556, 7);
  });

  it('should treat carbon measurement types (kg/tonne) as kWh', () => {
    // Carbon types use kWh internally, so factor is returned as-is
    expect(co2ConversionFactorForMeasurement(0.18254, 'kg')).toBeCloseTo(0.18254, 7);
  });
});

describe('calculatePrimaryEnergy', () => {
  // Adjusted Consumption = deliveredEnergy × primaryEnergyConversionFactor
  it('should multiply grid electricity by its primary factor of 2.1', () => {
    // 10,000,000 kWh × 2.1 = 21,000,000
    expect(calculatePrimaryEnergy(10_000_000, 2.1)).toBeCloseTo(21_000_000.0, 7);
  });

  it('should return delivered energy unchanged for fuels with a primary factor of 1.0', () => {
    // 5,000,000 kWh × 1.0 = 5,000,000
    expect(calculatePrimaryEnergy(5_000_000, 1.0)).toBeCloseTo(5_000_000.0, 7);
  });
});

describe('calculatePrimaryCarbon', () => {
  // Primary Carbon = deliveredEnergy × primaryFactor × co2Factor
  it('should calculate primary carbon for grid electricity', () => {
    // 10,000,000 × 2.1 × 0.10046 = 2,109,660 kgCO2e (≈ 2,110 in the Excel table)
    expect(calculatePrimaryCarbon(10_000_000, 2.1, 0.10046)).toBeCloseTo(2_109_660.0, 7);
  });

  it('should calculate primary carbon for natural gas', () => {
    // 5,000,000 × 1.0 × 0.18254 = 912,700 kgCO2e (≈ 913 in the Excel table)
    expect(calculatePrimaryCarbon(5_000_000, 1.0, 0.18254)).toBeCloseTo(912_700.0, 7);
  });
});

describe('calculateAdjustedThroughput', () => {
  it('should return actualThroughput unchanged when usedReportingMechanism is false', () => {
    const result = calculateAdjustedThroughput('8000', 0.6875, false);

    expect(result).toBeCloseTo(8000, 7);
  });

  it('should apply throughput adjustment factor when usedReportingMechanism is true', () => {
    // 0.6875 × 8000 = 5500
    const result = calculateAdjustedThroughput('8000', 0.6875, true);

    expect(result).toBeCloseTo(5500, 7);
  });

  it('should return null if actualThroughput is null', () => {
    const result = calculateAdjustedThroughput(null, 0.6875, true);

    expect(result).toBeNull();
  });

  it('should return null if actualThroughput is empty string', () => {
    const result = calculateAdjustedThroughput('', 0.6875, true);

    expect(result).toBeNull();
  });

  it('should handle different adjustment factors', () => {
    // 0.5 × 10000 = 5000
    const result = calculateAdjustedThroughput('10000', 0.5, true);

    expect(result).toBeCloseTo(5000, 7);
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

    expect(calculateImprovementTarget(baselineData, 'TP5')).toBeCloseTo(0.05, 7);
    expect(calculateImprovementTarget(baselineData, 'TP7')).toBeCloseTo(0.08, 7);
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

    expect(result).toBeCloseTo(0.1, 7);
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

    expect(result).toBeCloseTo(0.135, 7);
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

    expect(result).toBeCloseTo(0.09, 7);
  });
});

describe('calculateThroughputValues', () => {
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

    expect(result.improvementTarget).toBeCloseTo(0.05, 7);
    expect(result.throughputAdjustmentFactor).toBeCloseTo(1, 7);
    expect(result.baselineEnergyIntensity).toBeCloseTo(0.5, 7);
    expect(result.adjustedThroughput).toBeCloseTo(8000, 7);
    expect(result.targetVariableEnergy).toBeCloseTo(3800, 7); // 0.5 × 8000 × (1 - 0.05)
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

    expect(result.targetVariableEnergy).toBe(0);
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

    expect(result.improvementTarget).toBeCloseTo(0.1, 7);
    expect(result.baselineEnergyIntensity).toBeCloseTo(0.5, 7);
    expect(result.adjustedThroughput).toBeCloseTo(8000, 7);
    expect(result.targetVariableEnergy).toBeCloseTo(3600, 7); // 0.5 × 8000 × (1 - 0.1)
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

    expect(result.throughputAdjustmentFactor).toBeCloseTo(0.6874709, 7);
    expect(result.adjustedThroughput).toBeCloseTo(5499.7669, 3); // 0.6874709 × 8000
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

    expect(result.baselineEnergyIntensity).toBeCloseTo(0.0005, 7);
    expect(result.targetVariableEnergy).toBeCloseTo(3.8, 7);
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

    expect(result.baselineEnergyIntensity).toBeCloseTo(0.0018, 7);
    expect(result.targetVariableEnergy).toBeCloseTo(13.68, 7);
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

    expect(mwhResult.throughputAdjustmentFactor).toBeCloseTo(kwhResult.throughputAdjustmentFactor, 7);
    expect(mwhResult.adjustedThroughput).toBeCloseTo(kwhResult.adjustedThroughput as number, 7);
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
    expect(result).toBeCloseTo(0.08, 7);
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
    expect(result).toBeCloseTo(0.1, 7);
  });
});

describe('calculateAdjustedImprovementTargetForProduct', () => {
  it('should return facility target when product and facility base years match', () => {
    const result = calculateAdjustedImprovementTargetForProduct(
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
    expect(result).toBeCloseTo(0.12, 7);
  });

  it('should calculate adjusted product target based on spec example', () => {
    const result = calculateAdjustedImprovementTargetForProduct(
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
    expect(result).toBeCloseTo(0.010989011, 7);
  });

  it('should use the interim target when rebasing TP8 interim values', () => {
    const result = calculateAdjustedImprovementTargetForProduct(
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
    expect(result).toBeCloseTo(0, 7);
  });

  it('should calculate adjusted product target for TP9 final when product base year falls within TP9 period', () => {
    // facilityTarget=16%→0.16, totalProgress at productBaseYear(2029)=13%→0.13 → (0.16-0.13)/(1-0.13)
    const result = calculateAdjustedImprovementTargetForProduct(
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
    expect(result).toBeCloseTo(0.034482758, 7);
  });

  it('should use the interim target when rebasing TP9 interim values with product base year in TP8 period', () => {
    // interimTarget=(16+10)/2/100=0.13, totalProgress at productBaseYear(2027)=9% → (0.13-0.09)/(1-0.09)
    const result = calculateAdjustedImprovementTargetForProduct(
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
    expect(result).toBeCloseTo(0.043956044, 7);
  });

  // Spec worked example: facilityBaseYear=2022, productBaseYear=2027, TP7=8%, TP8=10%, TP9=16%
  // progress = 0.08 + 0.01 = 0.09 (9%)

  it('spec example — TP7 final should be 0 because productBaseYear falls after TP7', () => {
    // MAX(0, (0.08 - 0.09) / (1 - 0.09)) = MAX(0, negative) = 0
    const result = calculateAdjustedImprovementTargetForProduct(
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
    expect(result).toBe(0);
  });

  it('spec example — TP8 interim should be 0 at productBaseYear itself', () => {
    // interimTarget=(10+8)/2/100=0.09, progress=0.09 → (0.09-0.09)/(1-0.09)=0
    const result = calculateAdjustedImprovementTargetForProduct(
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
    expect(result).toBe(0);
  });

  it('spec example — TP9 final with productBaseYear=2027 → ≈7.692%', () => {
    // (0.16 - 0.09) / (1 - 0.09) = 0.07 / 0.91 ≈ 0.076923
    const result = calculateAdjustedImprovementTargetForProduct(
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
    expect(result).toBeCloseTo(0.076923077, 7);
  });
});

describe('calculateTargetEnergyForProduct', () => {
  it('should calculate per-product target energy from intensity, adjusted throughput and target (decimal)', () => {
    const result = calculateTargetEnergyForProduct(250, 5000, 0.041666667);
    expect(result).toBeCloseTo(1197916.66625, 5);
  });

  it('should return zero when adjusted throughput is zero', () => {
    const result = calculateTargetEnergyForProduct(5000, 0, 0.12);
    expect(result).toBe(0);
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
    expect(result).toBeCloseTo(26_000_000, 7);
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

    expect(result).toBeCloseTo(3_000_000, 7);
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

    expect(result).toBeCloseTo(27_000_000, 7);
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

    expect(result).toBeCloseTo(22_000_000, 7);
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

    expect(result).toBeCloseTo(26_000_000, 7);
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

    expect(result).toBe(0);
  });

  it('should handle undefined or null objects gracefully', () => {
    const energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails = {
      atLeastSeventyPercentEnergyUsed: false,
      standardFuels: undefined,
      nonStandardFuels: undefined,
    };

    const result = calculateActualEnergyTotal(energyFuelDetails);

    expect(result).toBe(0);
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

    expect(result).toBeCloseTo(0.11624, 5);
  });

  it('should calculate carbon-based weighted conversion factor', () => {
    // Carbon payload stores primary CO2 in primaryEnergy, so the weighted factor is 1
    const energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails = {
      atLeastSeventyPercentEnergyUsed: false,
      standardFuels: {
        GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '2109660' },
        NATURAL_GAS: { deliveredEnergy: '0', primaryEnergy: '912700' },
      },
      nonStandardFuels: [],
    };

    const result = calculateWeightedConversionFactor(energyFuelDetails, true);

    expect(result).toBeCloseTo(1, 7);
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

    expect(result).toBeCloseTo(0.11935, 5);
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

    expect(result).toBe(0);
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

    expect(result).toBeCloseTo(0.10046, 7);
  });
});

describe('calculateActualCo2Emissions', () => {
  it('should calculate energy-based CO2 emissions in tCO2e (divided by 1000)', () => {
    // (21000000 × 0.10046) + (5000000 × 0.18254) = 3022360 kgCO2e
    // 3022360 / 1000 = 3022.36 tCO2e
    const energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails = {
      atLeastSeventyPercentEnergyUsed: false,
      standardFuels: {
        GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '21000000' },
        NATURAL_GAS: { deliveredEnergy: '0', primaryEnergy: '5000000' },
      },
      nonStandardFuels: [],
    };

    const result = calculateActualCo2Emissions(energyFuelDetails, false);

    expect(result).toBeCloseTo(3022.36, 2);
  });

  it('should calculate carbon-based CO2 emissions in tCO2e', () => {
    // Sum of stored primary CO2 / 1000
    const energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails = {
      atLeastSeventyPercentEnergyUsed: false,
      standardFuels: {
        GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '2109660' },
        NATURAL_GAS: { deliveredEnergy: '0', primaryEnergy: '912700' },
      },
      nonStandardFuels: [],
    };

    const result = calculateActualCo2Emissions(energyFuelDetails, true);

    expect(result).toBeCloseTo(3022.36, 2);
  });

  it('should include non-standard fuels in CO2 calculation', () => {
    // (21000000 × 0.10046) + (5000000 × 0.18254) + (1000000 × 0.2) = 3222360 kgCO2e
    // 3222360 / 1000 = 3222.36 tCO2e
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

    const result = calculateActualCo2Emissions(energyFuelDetails, false);

    expect(result).toBeCloseTo(3222.36, 2);
  });

  it('should return 0 when there are no fuels with energy', () => {
    const energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails = {
      atLeastSeventyPercentEnergyUsed: false,
      standardFuels: {
        GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '0' },
        NATURAL_GAS: { deliveredEnergy: '0', primaryEnergy: '0' },
      },
      nonStandardFuels: [],
    };

    const result = calculateActualCo2Emissions(energyFuelDetails, false);

    expect(result).toBe(0);
  });

  it('should ignore zero-value fuels in CO2 calculation', () => {
    // Only GRID_ELECTRICITY: 21000000 × 0.10046 = 2109660 kgCO2e / 1000 = 2109.66 tCO2e
    const energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails = {
      atLeastSeventyPercentEnergyUsed: false,
      standardFuels: {
        GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '21000000' },
        NATURAL_GAS: { deliveredEnergy: '0', primaryEnergy: '0' },
      },
      nonStandardFuels: [],
    };

    const result = calculateActualCo2Emissions(energyFuelDetails, false);

    expect(result).toBeCloseTo(2109.66, 2);
  });

  it('should handle string primary energy values', () => {
    const energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails = {
      atLeastSeventyPercentEnergyUsed: false,
      standardFuels: {
        GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '21000000' },
        NATURAL_GAS: { deliveredEnergy: '0', primaryEnergy: '5000000' },
      },
      nonStandardFuels: [],
    };

    const result = calculateActualCo2Emissions(energyFuelDetails, false);

    expect(result).toBeCloseTo(3022.36, 2);
  });

  it('should handle mixed energy and carbon measurement modes correctly', () => {
    const energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails = {
      atLeastSeventyPercentEnergyUsed: false,
      standardFuels: {
        GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '2109660' },
      },
      nonStandardFuels: [],
    };

    const carbonBasedResult = calculateActualCo2Emissions(energyFuelDetails, true);

    expect(carbonBasedResult).toBeCloseTo(2109.66, 2);
  });
});
