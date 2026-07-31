import { MEASUREMENT_TYPE_TO_UNIT_MAP, MeasurementUnit, transformMeasurementTypeToUnit } from '@shared/pipes';
import BigNumber from 'bignumber.js';

import {
  PerformanceDataFacilityBaselineAndTargets,
  PerformanceDataFacilityCalculatedResults,
  PerformanceDataFacilityFuelEnergyConsumption,
  PerformanceDataFacilityInputEnergyFuelDetails,
  PerformanceDataFacilityReferenceData,
  ProductVariableEnergyConsumptionData,
} from 'cca-api';

import { to7DecimalPlacesNumber, toBigNumber, toNumber } from '../../../shared/utils/number';
import { FUEL_MAP } from './table-data';
import {
  EnergyFuelRow,
  FuelReference,
  FuelRow,
  FuelTypeKey,
  ThroughputCalculationInputs,
} from './target-period-reporting-form.types';

const CO2_BASE_UNIT = MEASUREMENT_TYPE_TO_UNIT_MAP.ENERGY_KWH;

const CARBON_UNITS = new Set<MeasurementUnit>([
  MEASUREMENT_TYPE_TO_UNIT_MAP.CARBON_KG,
  MEASUREMENT_TYPE_TO_UNIT_MAP.CARBON_TONNE,
]);

export function roundHalfUpTo7Decimals(value: string | number | BigNumber | null | undefined): string {
  const bn = value instanceof BigNumber ? value : toBigNumber(value);
  if (bn.isNaN()) return '0';

  // Use toFixed to guarantee fixed-point notation (never scientific) — the API expects plain decimals.
  // Strip trailing zeros to match the original Number-based formatting (e.g. 1.5 stays "1.5", not "1.5000000").
  const fixed = bn.decimalPlaces(7, BigNumber.ROUND_HALF_UP).toFixed(7);
  return fixed.replace(/(\.\d*?[1-9])0+$/, '$1').replace(/\.0*$/, '');
}

export function resolveMeasurementUnit(referenceData?: PerformanceDataFacilityReferenceData): MeasurementUnit {
  const measurementType = referenceData?.baselineAndTargets?.measurementType;
  return measurementType ? transformMeasurementTypeToUnit(measurementType) : MEASUREMENT_TYPE_TO_UNIT_MAP.ENERGY_KWH;
}

export function resolveCalculatedResults(
  calculatedResults: PerformanceDataFacilityCalculatedResults,
): PerformanceDataFacilityCalculatedResults {
  return {
    actualEnergyCarbon: calculatedResults.actualEnergyCarbon,
    targetEnergyCarbon: calculatedResults.targetEnergyCarbon,
    energyCarbonDifference: calculatedResults.energyCarbonDifference,
    targetImprovement: calculatedResults.targetImprovement,
    weightedConversionFactor: calculatedResults.weightedConversionFactor,
    targetCo2Emissions: calculatedResults.targetCo2Emissions,
    actualCo2Emissions: calculatedResults.actualCo2Emissions,
    co2EmissionsDifference: calculatedResults.co2EmissionsDifference,
    actualImprovement: calculatedResults.actualImprovement,
    targetPeriodResultType: calculatedResults.targetPeriodResultType,
    surplusGained: calculatedResults.surplusGained,
    buyOutRequired: calculatedResults.buyOutRequired,
  };
}

function convertCo2FactorFromKWh(co2FactorPerKWh: number, unit: MeasurementUnit): BigNumber {
  const factor = new BigNumber(co2FactorPerKWh);
  switch (unit) {
    case MEASUREMENT_TYPE_TO_UNIT_MAP.ENERGY_KWH:
      return factor;
    case MEASUREMENT_TYPE_TO_UNIT_MAP.ENERGY_MWH:
      return factor.times(1000);
    case MEASUREMENT_TYPE_TO_UNIT_MAP.ENERGY_GJ:
      // Keep GJ factors aligned with backend precision used in TPR validation.
      return factor.times(1000).div(3.6).decimalPlaces(7, BigNumber.ROUND_HALF_UP);
    default:
      return factor;
  }
}

export function isCarbonMeasurementType(unit: MeasurementUnit): boolean {
  return CARBON_UNITS.has(unit);
}

function co2MeasurementUnit(unit: MeasurementUnit): MeasurementUnit {
  return isCarbonMeasurementType(unit) ? MEASUREMENT_TYPE_TO_UNIT_MAP.ENERGY_KWH : unit;
}

export function co2ConversionFactorForMeasurement(co2FactorPerKWh: number, unit: MeasurementUnit): BigNumber {
  return convertCo2FactorFromKWh(co2FactorPerKWh, co2MeasurementUnit(unit));
}

export function calculatePrimaryEnergy(deliveredEnergy: string | number | null, primaryFactor: number): BigNumber {
  return toBigNumber(deliveredEnergy).times(primaryFactor);
}

export function primaryCarbonDisplayUnit(measurementUnit: MeasurementUnit): string {
  return measurementUnit === MEASUREMENT_TYPE_TO_UNIT_MAP.CARBON_TONNE ? 'tCO2e' : 'kgCO2e';
}

export function calculatePrimaryCarbon(
  deliveredEnergy: string | number | BigNumber | null,
  primaryFactor: number,
  co2Factor: number | BigNumber | string,
  measurementUnit: MeasurementUnit = MEASUREMENT_TYPE_TO_UNIT_MAP.CARBON_KG,
): BigNumber {
  const delivered = deliveredEnergy instanceof BigNumber ? deliveredEnergy : toBigNumber(deliveredEnergy);
  const co2 = co2Factor instanceof BigNumber ? co2Factor : toBigNumber(co2Factor);
  const primaryCarbon = delivered.times(primaryFactor).times(co2);
  return measurementUnit === MEASUREMENT_TYPE_TO_UNIT_MAP.CARBON_TONNE ? primaryCarbon.times(0.001) : primaryCarbon;
}

export function mapStandardFuelRows(
  fuelEntries: [FuelTypeKey, FuelReference][],
  standardFuels?: Record<string, PerformanceDataFacilityFuelEnergyConsumption>,
  measurementUnit: MeasurementUnit = CO2_BASE_UNIT,
): FuelRow[] {
  return fuelEntries.map(([fuelKey, fuel]) => {
    const deliveredEnergy = standardFuels?.[fuelKey]?.deliveredEnergy ?? '0';

    const co2ConversionFactor = co2ConversionFactorForMeasurement(fuel.co2ConversionFactor, measurementUnit);

    const primaryEnergy = calculatePrimaryEnergy(deliveredEnergy, fuel.primaryEnergyConversionFactor);

    const primaryCarbon = calculatePrimaryCarbon(
      deliveredEnergy,
      fuel.primaryEnergyConversionFactor,
      co2ConversionFactor,
      measurementUnit,
    );

    return {
      fuelKey,
      label: fuel.label,
      deliveredEnergy: Number(deliveredEnergy),
      co2ConversionFactor: co2ConversionFactor.toNumber(),
      primaryEnergyConversionFactor: fuel.primaryEnergyConversionFactor,
      primaryEnergy: primaryEnergy.toNumber(),
      primaryCarbon: primaryCarbon.toNumber(),
    };
  });
}

/**
 * Calculates the throughput adjustment factor for SRM facilities.
 *
 * All arguments must be DELIVERED ENERGY (not primary energy) in the facility's measuring unit.
 *
 * @param gridElectricity Delivered energy from grid electricity
 * @param nonGridElectricity Delivered energy from non-grid electricity
 * @param chpElectricity Delivered energy from CHP/dedicated generators
 * @returns The throughput adjustment factor (0-1)
 */
export function calculateThroughputAdjustmentFactor(
  gridElectricity: string | number | BigNumber,
  nonGridElectricity: string | number | BigNumber,
  chpElectricity: string | number | BigNumber,
): BigNumber {
  // All arguments must be delivered energy (see spec and data model)
  const numerator = toBigNumber(gridElectricity).plus(toBigNumber(nonGridElectricity));
  const denominator = numerator.plus(toBigNumber(chpElectricity));
  // SRM form validation prevents CHP > 0 when both grid and non-grid electricity are 0.
  return denominator.isZero() ? new BigNumber(1) : numerator.div(denominator);
}

export function buildEnergyFuelRows(
  energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails,
  measurementUnit: MeasurementUnit = CO2_BASE_UNIT,
): EnergyFuelRow[] {
  const carbonMeasurement = isCarbonMeasurementType(measurementUnit);

  const standardRows = (Object.entries(FUEL_MAP) as [FuelTypeKey, FuelReference][])
    .filter(([fuelKey]) => toNumber(energyFuelDetails?.standardFuels?.[fuelKey]?.deliveredEnergy) !== 0)
    .map(([fuelKey, fuel]) => {
      const deliveredEnergy = toNumber(energyFuelDetails?.standardFuels?.[fuelKey]?.deliveredEnergy);
      const co2ConversionFactor = co2ConversionFactorForMeasurement(fuel.co2ConversionFactor, measurementUnit);
      const primaryValue = carbonMeasurement
        ? calculatePrimaryCarbon(
            deliveredEnergy,
            fuel.primaryEnergyConversionFactor,
            co2ConversionFactor,
            measurementUnit,
          )
        : calculatePrimaryEnergy(deliveredEnergy, fuel.primaryEnergyConversionFactor);

      return {
        fuelType: fuel.label,
        co2ConversionFactor: to7DecimalPlacesNumber(co2ConversionFactor),
        deliveredEnergy,
        primaryEnergyConversionFactor: fuel.primaryEnergyConversionFactor,
        primaryEnergy: to7DecimalPlacesNumber(primaryValue),
        isCustom: false,
      };
    });

  const customRows = (energyFuelDetails?.nonStandardFuels ?? [])
    .filter((fuel) => toNumber(fuel.deliveredEnergy) !== 0)
    .map((fuel) => {
      const deliveredEnergy = toNumber(fuel.deliveredEnergy);
      const co2ConversionFactor = to7DecimalPlacesNumber(toBigNumber(fuel.conversionFactor));
      const primaryValue = carbonMeasurement
        ? calculatePrimaryCarbon(deliveredEnergy, 1, co2ConversionFactor, measurementUnit)
        : new BigNumber(deliveredEnergy);

      return {
        fuelType: fuel.name,
        co2ConversionFactor,
        deliveredEnergy,
        primaryEnergyConversionFactor: 1,
        primaryEnergy: to7DecimalPlacesNumber(primaryValue),
        isCustom: true,
      };
    });

  return [...standardRows, ...customRows];
}

export function calculateImprovementTarget(
  referenceData: PerformanceDataFacilityReferenceData,
  targetPeriodType: 'TP5' | 'TP6' | 'TP7' | 'TP8' | 'TP9',
): BigNumber {
  const improvements = referenceData?.baselineAndTargets?.improvements;

  if (targetPeriodType === 'TP8' || targetPeriodType === 'TP9') {
    const previousPeriod = targetPeriodType === 'TP8' ? 'TP7' : 'TP8';
    const currentTarget = toBigNumber(improvements?.[targetPeriodType]);
    const previousTarget = toBigNumber(improvements?.[previousPeriod]);
    const interimTarget = currentTarget.plus(previousTarget).div(2);
    return interimTarget.div(100);
  }

  return toBigNumber(improvements?.[targetPeriodType]).div(100);
}

export function calculateFacilityImprovementTarget(
  referenceData: PerformanceDataFacilityReferenceData,
  reportType: 'INTERIM' | 'FINAL',
  targetPeriodType: 'TP5' | 'TP6' | 'TP7' | 'TP8' | 'TP9',
): BigNumber {
  if (reportType === 'FINAL') {
    return toBigNumber(referenceData?.baselineAndTargets?.improvements?.[targetPeriodType]).div(100);
  }

  return calculateImprovementTarget(referenceData, targetPeriodType);
}

export function calculateAdjustedThroughput(
  actualThroughput: string | null,
  throughputAdjustmentFactor: number | BigNumber,
  usedReportingMechanism: boolean,
): BigNumber | null {
  if (!actualThroughput) return null;

  let parsedThroughput: BigNumber;
  try {
    parsedThroughput = new BigNumber(actualThroughput);
  } catch {
    return null;
  }

  if (parsedThroughput.isNaN()) return null;

  return usedReportingMechanism ? parsedThroughput.times(throughputAdjustmentFactor) : parsedThroughput;
}

/**
 * Calculate facility-level target variable energy/carbon.
 * Applies facility improvement % once to the sum of all products' (intensity × adjusted throughput).
 * Per spec: [sum(baseline_intensity × adjusted_throughput)] × (1 - improvement%)
 * Returns 0 if no variable energy exists.
 */
export function applyImprovementTarget(
  sumOfIntensityTimesAdjustedThroughput: number | BigNumber,
  facilityImprovementTarget: number | BigNumber | string,
  hasVariableEnergy: boolean,
): BigNumber {
  if (!hasVariableEnergy) return new BigNumber(0);
  const sum =
    sumOfIntensityTimesAdjustedThroughput instanceof BigNumber
      ? sumOfIntensityTimesAdjustedThroughput
      : new BigNumber(sumOfIntensityTimesAdjustedThroughput);
  const improvementPercent = toBigNumber(facilityImprovementTarget);
  return sum.times(new BigNumber(1).minus(improvementPercent));
}

export function calculateAdjustedImprovementTarget(
  referenceData: PerformanceDataFacilityReferenceData,
  reportType: 'INTERIM' | 'FINAL',
  targetPeriodType: 'TP5' | 'TP6' | 'TP7' | 'TP8' | 'TP9',
  facilityBaseYear: number,
  productBaseYear: number,
): BigNumber {
  const improvements = referenceData?.baselineAndTargets?.improvements;

  const facilityTarget = calculateFacilityImprovementTarget(referenceData, reportType, targetPeriodType);

  if (!facilityBaseYear || !productBaseYear || productBaseYear === facilityBaseYear) {
    return facilityTarget;
  }

  const facilityTargetTp7 = toBigNumber(improvements?.TP7).div(100);
  const facilityTargetTp8 = toBigNumber(improvements?.TP8).div(100);
  const facilityTargetTp9 = toBigNumber(improvements?.TP9).div(100);

  const tp7ProgressYears = Math.min(productBaseYear, 2026) - facilityBaseYear;
  const tp7Years = 2026 - facilityBaseYear;
  const tp8ProgressYears = Math.max(Math.min(productBaseYear, 2028) - 2026, 0);
  const tp8Years = 2028 - 2026;
  const tp9ProgressYears = Math.max(Math.min(productBaseYear, 2030) - 2028, 0);
  const tp9Years = 2030 - 2028;

  let totalProgressAtProductBaseYear = new BigNumber(0);

  if (tp7Years > 0) {
    totalProgressAtProductBaseYear = totalProgressAtProductBaseYear.plus(
      new BigNumber(tp7ProgressYears).div(tp7Years).times(facilityTargetTp7),
    );
  }

  if (tp8Years > 0) {
    totalProgressAtProductBaseYear = totalProgressAtProductBaseYear.plus(
      new BigNumber(tp8ProgressYears).div(tp8Years).times(facilityTargetTp8.minus(facilityTargetTp7)),
    );
  }

  if (tp9Years > 0) {
    totalProgressAtProductBaseYear = totalProgressAtProductBaseYear.plus(
      new BigNumber(tp9ProgressYears).div(tp9Years).times(facilityTargetTp9.minus(facilityTargetTp8)),
    );
  }

  const denominatorPart = new BigNumber(1).minus(totalProgressAtProductBaseYear);
  if (denominatorPart.isLessThanOrEqualTo(0)) return new BigNumber(0);

  const result = facilityTarget.minus(totalProgressAtProductBaseYear).div(denominatorPart);
  return result.isNegative() ? new BigNumber(0) : result;
}

export function calculateProductTargetEnergy(
  baselineEnergyIntensity: string | number | BigNumber | null,
  adjustedThroughput: number | BigNumber,
  improvementTarget: number | BigNumber,
): BigNumber {
  const intensity = toBigNumber(baselineEnergyIntensity);
  const throughput = toBigNumber(adjustedThroughput);
  const target = toBigNumber(improvementTarget);
  return intensity.times(throughput).times(new BigNumber(1).minus(target));
}

export function resolveProductEnergyCarbonIntensity(
  product: ProductVariableEnergyConsumptionData | undefined,
): BigNumber {
  if (!product) return new BigNumber(0);

  if (product.energyCarbonIntensity != null) {
    return toBigNumber(product.energyCarbonIntensity);
  }

  const throughput = toBigNumber(product.throughput);
  return throughput.isGreaterThan(0) ? toBigNumber(product.energy).div(throughput) : new BigNumber(0);
}

export function calculateThroughputValues(inputs: ThroughputCalculationInputs) {
  const improvementTarget = calculateFacilityImprovementTarget(
    inputs.referenceData,
    inputs.reportType,
    inputs.targetPeriodType,
  );

  const energyFuelDetails = inputs.performanceData?.energyFuelDetails;

  const throughputAdjustmentFactor = calculateThroughputAdjustmentFactor(
    energyFuelDetails?.standardFuels?.['GRID_ELECTRICITY']?.deliveredEnergy ?? '0',
    energyFuelDetails?.standardFuels?.['NON_GRID_ELECTRICITY']?.deliveredEnergy ?? '0',
    energyFuelDetails?.electricitySuppliedFromCHP ?? '0',
  );

  const baselineVariableEnergy = toBigNumber(inputs.referenceData?.baselineAndTargets?.baselineVariableEnergy);
  const baselineTotalThroughput = toBigNumber(inputs.referenceData?.baselineAndTargets?.totalThroughput);

  let baselineEnergyIntensity: BigNumber | null = null;

  if (baselineVariableEnergy.isGreaterThan(0) && baselineTotalThroughput.isGreaterThan(0)) {
    baselineEnergyIntensity = baselineVariableEnergy.div(baselineTotalThroughput);
  } else if (inputs.referenceData?.baselineAndTargets?.baselineEnergyCarbonIntensity != null) {
    baselineEnergyIntensity = toBigNumber(inputs.referenceData.baselineAndTargets.baselineEnergyCarbonIntensity);
  }

  const adjustedThroughput = calculateAdjustedThroughput(
    inputs.actualThroughput,
    throughputAdjustmentFactor,
    inputs.referenceData?.baselineAndTargets?.usedReportingMechanism ?? false,
  );

  const hasVariableEnergy = inputs.referenceData?.baselineAndTargets?.variableEnergyType != null;

  let targetVariableEnergy: BigNumber | null = null;

  // For totals-only, calculate only if both intensity and throughput are available
  if (baselineEnergyIntensity && adjustedThroughput != null) {
    const sumOfIntensityTimesAdjustedThroughput = baselineEnergyIntensity.times(adjustedThroughput);
    targetVariableEnergy = applyImprovementTarget(
      sumOfIntensityTimesAdjustedThroughput,
      improvementTarget,
      hasVariableEnergy,
    );
  }

  return {
    improvementTarget,
    throughputAdjustmentFactor,
    baselineEnergyIntensity,
    adjustedThroughput,
    targetVariableEnergy,
  };
}

export function decideVariableEnergyType(
  variableEnergyType: PerformanceDataFacilityBaselineAndTargets['variableEnergyType'] | null,
): PerformanceDataFacilityBaselineAndTargets['variableEnergyType'] {
  return variableEnergyType === 'BY_PRODUCT' ? 'BY_PRODUCT' : 'TOTALS';
}

export function calculateActualEnergyTotal(
  energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails,
): BigNumber {
  let total = new BigNumber(0);

  // Sum primary energy from standard fuels
  if (energyFuelDetails?.standardFuels) {
    Object.values(energyFuelDetails.standardFuels).forEach((fuel) => {
      const primaryEnergy = toBigNumber(fuel?.primaryEnergy);
      if (!primaryEnergy.isZero()) total = total.plus(primaryEnergy);
    });
  }

  // Sum primary energy from non-standard fuels
  if (energyFuelDetails?.nonStandardFuels) {
    energyFuelDetails.nonStandardFuels.forEach((fuel) => {
      const primaryEnergy = toBigNumber(fuel?.primaryEnergy);
      if (!primaryEnergy.isZero()) total = total.plus(primaryEnergy);
    });
  }

  return total;
}

// sum delivered energy × primary energy factor for all fuels
function sumDeliveredTimesPrimaryFactor(energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails): BigNumber {
  let total = new BigNumber(0);

  if (energyFuelDetails?.standardFuels && FUEL_MAP) {
    Object.entries(energyFuelDetails.standardFuels).forEach(([fuelKey, fuel]) => {
      const fuelRef = FUEL_MAP[fuelKey as FuelTypeKey];
      if (fuelRef) {
        const delivered = toBigNumber(fuel?.deliveredEnergy);
        const factor = new BigNumber(fuelRef.primaryEnergyConversionFactor);
        if (!delivered.isZero()) total = total.plus(delivered.times(factor));
      }
    });
  }

  if (energyFuelDetails?.nonStandardFuels) {
    energyFuelDetails.nonStandardFuels.forEach((fuel) => {
      const delivered = toBigNumber(fuel?.deliveredEnergy);
      const factor = toBigNumber(fuel?.conversionFactor);
      if (!delivered.isZero()) total = total.plus(delivered.times(factor));
    });
  }

  return total;
}

/**
 * Calculates the weighted conversion factor for the facility.
 *
 * For carbon-based measurement: numerator = sum of primary CO2 (not energy!), denominator = sum of delivered energy × primary energy factor.
 * For energy-based measurement: numerator = sum of (primary energy × CO2 factor), denominator = actual energy total.
 *
 * The denominator for carbon-based must NOT be sum of primary CO2; it must be delivered × primary factor (see spec).
 */
export function calculateWeightedConversionFactor(
  energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails,
  isCarbonMeasurement: boolean,
  measurementUnit: MeasurementUnit = CO2_BASE_UNIT,
): BigNumber {
  if (isCarbonMeasurement) {
    // Carbon-based: numerator = sum of primary CO2, denominator = sum of delivered × primary factor
    let totalPrimaryCo2 = new BigNumber(0);

    if (energyFuelDetails?.standardFuels && FUEL_MAP) {
      Object.entries(energyFuelDetails.standardFuels).forEach(([fuelKey, fuel]) => {
        const fuelRef = FUEL_MAP[fuelKey as FuelTypeKey];
        if (fuelRef) {
          const delivered = toBigNumber(fuel?.deliveredEnergy);
          const primaryFactor = new BigNumber(fuelRef.primaryEnergyConversionFactor);

          const co2ConversionFactor = co2ConversionFactorForMeasurement(fuelRef.co2ConversionFactor, measurementUnit);

          // For carbon-based, primaryEnergy field is used to store primary CO2 in the saved payload, but we recalculate for clarity
          const primaryCo2 = delivered.times(primaryFactor).times(co2ConversionFactor);
          if (measurementUnit === MEASUREMENT_TYPE_TO_UNIT_MAP.CARBON_TONNE) {
            totalPrimaryCo2 = totalPrimaryCo2.plus(primaryCo2.times(0.001));
          } else {
            totalPrimaryCo2 = totalPrimaryCo2.plus(primaryCo2);
          }
        }
      });
    }

    if (energyFuelDetails?.nonStandardFuels) {
      energyFuelDetails.nonStandardFuels.forEach((fuel) => {
        const delivered = toBigNumber(fuel?.deliveredEnergy);
        const co2ConversionFactor = toBigNumber(fuel?.conversionFactor);
        // Non-standard fuels always use primary factor of 1
        const primaryCo2 = delivered.times(co2ConversionFactor);
        if (measurementUnit === MEASUREMENT_TYPE_TO_UNIT_MAP.CARBON_TONNE) {
          totalPrimaryCo2 = totalPrimaryCo2.plus(primaryCo2.times(0.001));
        } else {
          totalPrimaryCo2 = totalPrimaryCo2.plus(primaryCo2);
        }
      });
    }

    // The denominator must be delivered × primary factor, not sum of primary CO2
    const denominator = sumDeliveredTimesPrimaryFactor(energyFuelDetails);
    if (denominator.isZero()) {
      return totalPrimaryCo2.isZero() ? new BigNumber(0) : new BigNumber(1);
    }
    return totalPrimaryCo2.div(denominator);
  }

  // Energy-based: sum of (primary energy x CO2 conversion factor) / actual energy total
  const actualEnergyTotal = calculateActualEnergyTotal(energyFuelDetails);
  if (actualEnergyTotal.isZero()) return new BigNumber(0);
  let weightedSum = new BigNumber(0);

  if (energyFuelDetails?.standardFuels && FUEL_MAP) {
    Object.entries(energyFuelDetails.standardFuels).forEach(([fuelKey, fuel]) => {
      const fuelRef = FUEL_MAP[fuelKey as FuelTypeKey];

      if (fuelRef) {
        const primaryEnergy = toBigNumber(fuel?.primaryEnergy);

        if (!primaryEnergy.isZero()) {
          const co2Factor = co2ConversionFactorForMeasurement(fuelRef.co2ConversionFactor, measurementUnit);
          weightedSum = weightedSum.plus(primaryEnergy.times(co2Factor));
        }
      }
    });
  }

  if (energyFuelDetails?.nonStandardFuels) {
    energyFuelDetails.nonStandardFuels.forEach((fuel) => {
      const primaryEnergy = toBigNumber(fuel?.primaryEnergy);

      if (!primaryEnergy.isZero()) {
        const co2Factor = toBigNumber(fuel?.conversionFactor);
        weightedSum = weightedSum.plus(primaryEnergy.times(co2Factor));
      }
    });
  }

  return weightedSum.div(actualEnergyTotal);
}

export function resolveFacilityBaselineYear(products: ProductVariableEnergyConsumptionData[]): number | null {
  return products.length > 0 ? Math.min(...products.map((p) => p.baselineYear)) : null;
}
