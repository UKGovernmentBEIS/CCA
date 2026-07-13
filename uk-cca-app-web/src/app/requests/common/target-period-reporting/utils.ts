import { MEASUREMENT_TYPE_TO_UNIT_MAP, MeasurementUnit, transformMeasurementTypeToUnit } from '@shared/pipes';

import {
  PerformanceDataFacilityBaselineAndTargets,
  PerformanceDataFacilityCalculatedResults,
  PerformanceDataFacilityFuelEnergyConsumption,
  PerformanceDataFacilityInputEnergyFuelDetails,
  PerformanceDataFacilityReferenceData,
  ProductVariableEnergyConsumptionData,
} from 'cca-api';

import { toNumber } from '../../../shared/utils/number';
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

export function roundHalfUpTo7Decimals(value: string | number | undefined): string {
  const numericValue = Number(value ?? 0);
  const factor = 10 ** 7;

  // Use true half-up behavior (away from zero), including negative values.
  const roundedNumber =
    numericValue >= 0
      ? Math.round(numericValue * factor) / factor
      : -Math.round(Math.abs(numericValue) * factor) / factor;

  const rounded = roundedNumber.toFixed(7);
  return rounded.replace(/(\.\d*?[1-9])0+$/, '$1').replace(/\.0*$/, '');
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

function convertCo2FactorFromKWh(co2FactorPerKWh: number, unit: MeasurementUnit): number {
  switch (unit) {
    case MEASUREMENT_TYPE_TO_UNIT_MAP.ENERGY_KWH:
      return co2FactorPerKWh;
    case MEASUREMENT_TYPE_TO_UNIT_MAP.ENERGY_MWH:
      return co2FactorPerKWh * 1000;
    case MEASUREMENT_TYPE_TO_UNIT_MAP.ENERGY_GJ:
      // Keep GJ factors aligned with backend precision used in TPR validation.
      return Math.round(((co2FactorPerKWh * 1000) / 3.6) * 10 ** 7) / 10 ** 7;
    default:
      return co2FactorPerKWh;
  }
}

export function isCarbonMeasurementType(unit: MeasurementUnit): boolean {
  return CARBON_UNITS.has(unit);
}

function co2MeasurementUnit(unit: MeasurementUnit): MeasurementUnit {
  return isCarbonMeasurementType(unit) ? MEASUREMENT_TYPE_TO_UNIT_MAP.ENERGY_KWH : unit;
}

export function co2ConversionFactorForMeasurement(co2FactorPerKWh: number, unit: MeasurementUnit): number {
  return convertCo2FactorFromKWh(co2FactorPerKWh, co2MeasurementUnit(unit));
}

export function calculatePrimaryEnergy(deliveredEnergy: string | number | null, primaryFactor: number): number {
  return toNumber(deliveredEnergy) * primaryFactor;
}

export function primaryCarbonDisplayUnit(measurementUnit: MeasurementUnit): string {
  return measurementUnit === MEASUREMENT_TYPE_TO_UNIT_MAP.CARBON_TONNE ? 'tCO2e' : 'kgCO2e';
}

export function calculatePrimaryCarbon(
  deliveredEnergy: string | number | null,
  primaryFactor: number,
  co2Factor: number,
  measurementUnit: MeasurementUnit = MEASUREMENT_TYPE_TO_UNIT_MAP.CARBON_KG,
): number {
  const primaryCarbon = toNumber(deliveredEnergy) * primaryFactor * co2Factor;
  return measurementUnit === MEASUREMENT_TYPE_TO_UNIT_MAP.CARBON_TONNE ? primaryCarbon * 0.001 : primaryCarbon;
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
      co2ConversionFactor,
      primaryEnergyConversionFactor: fuel.primaryEnergyConversionFactor,
      primaryEnergy,
      primaryCarbon,
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
  gridElectricity: number,
  nonGridElectricity: number,
  chpElectricity: number,
): number {
  // All arguments must be delivered energy (see spec and data model)
  const numerator = gridElectricity + nonGridElectricity;
  const denominator = numerator + chpElectricity;
  // SRM form validation prevents CHP > 0 when both grid and non-grid electricity are 0.
  return denominator === 0 ? 1 : numerator / denominator;
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
        co2ConversionFactor,
        deliveredEnergy,
        primaryEnergyConversionFactor: fuel.primaryEnergyConversionFactor,
        primaryEnergy: primaryValue,
        isCustom: false,
      };
    });

  const customRows = (energyFuelDetails?.nonStandardFuels ?? [])
    .filter((fuel) => toNumber(fuel.deliveredEnergy) !== 0)
    .map((fuel) => {
      const deliveredEnergy = toNumber(fuel.deliveredEnergy);
      const co2ConversionFactor = toNumber(fuel.conversionFactor);
      const primaryValue = carbonMeasurement
        ? calculatePrimaryCarbon(deliveredEnergy, 1, co2ConversionFactor, measurementUnit)
        : deliveredEnergy;

      return {
        fuelType: fuel.name,
        co2ConversionFactor,
        deliveredEnergy,
        primaryEnergyConversionFactor: 1,
        primaryEnergy: primaryValue,
        isCustom: true,
      };
    });

  return [...standardRows, ...customRows];
}

export function calculateImprovementTarget(
  referenceData: PerformanceDataFacilityReferenceData,
  targetPeriodType: 'TP5' | 'TP6' | 'TP7' | 'TP8' | 'TP9',
): number {
  const improvements = referenceData?.baselineAndTargets?.improvements;

  if (targetPeriodType === 'TP8' || targetPeriodType === 'TP9') {
    const previousPeriod = targetPeriodType === 'TP8' ? 'TP7' : 'TP8';
    const currentTarget = Number(improvements[targetPeriodType]);
    const previousTarget = Number(improvements[previousPeriod]);
    const interimTarget = (currentTarget + previousTarget) / 2;
    return interimTarget / 100;
  }

  return toNumber(improvements?.[targetPeriodType]) / 100;
}

export function calculateFacilityImprovementTarget(
  referenceData: PerformanceDataFacilityReferenceData,
  reportType: 'INTERIM' | 'FINAL',
  targetPeriodType: 'TP5' | 'TP6' | 'TP7' | 'TP8' | 'TP9',
): number {
  if (reportType === 'FINAL') {
    return toNumber(referenceData?.baselineAndTargets?.improvements?.[targetPeriodType]) / 100;
  }

  return calculateImprovementTarget(referenceData, targetPeriodType);
}

export function calculateAdjustedThroughput(
  actualThroughput: string | null,
  throughputAdjustmentFactor: number,
  usedReportingMechanism: boolean,
): number | null {
  const parsedThroughput = actualThroughput ? parseFloat(actualThroughput) : null;
  if (parsedThroughput === null) return null;

  return usedReportingMechanism ? throughputAdjustmentFactor * parsedThroughput : parsedThroughput;
}

/**
 * Calculate facility-level target variable energy/carbon.
 * Applies facility improvement % once to the sum of all products' (intensity × adjusted throughput).
 * Per spec: [sum(baseline_intensity × adjusted_throughput)] × (1 - improvement%)
 * Returns 0 if no variable energy exists.
 */
export function calculateFacilityTargetVariableEnergy(
  sumOfIntensityTimesAdjustedThroughput: number,
  facilityImprovementTarget: number | string,
  hasVariableEnergy: boolean,
): number {
  if (!hasVariableEnergy) return 0;
  const improvementPercent = toNumber(facilityImprovementTarget);
  return sumOfIntensityTimesAdjustedThroughput * (1 - improvementPercent);
}

export function calculateAdjustedImprovementTargetForProduct(
  referenceData: PerformanceDataFacilityReferenceData,
  reportType: 'INTERIM' | 'FINAL',
  targetPeriodType: 'TP5' | 'TP6' | 'TP7' | 'TP8' | 'TP9',
  facilityBaseYear: number,
  productBaseYear: number,
): number {
  const facilityTarget = calculateFacilityImprovementTarget(referenceData, reportType, targetPeriodType);

  if (!facilityBaseYear || !productBaseYear || productBaseYear <= facilityBaseYear) {
    return facilityTarget;
  }

  const improvements = referenceData?.baselineAndTargets?.improvements;
  const facilityTargetTp7 = toNumber(improvements?.TP7) / 100;
  const facilityTargetTp8 = toNumber(improvements?.TP8) / 100;
  const facilityTargetTp9 = toNumber(improvements?.TP9) / 100;

  const tp7ProgressYears = Math.min(productBaseYear, 2026) - facilityBaseYear;
  const tp7Years = 2026 - facilityBaseYear;
  const tp8ProgressYears = Math.max(Math.min(productBaseYear, 2028) - 2026, 0);
  const tp8Years = 2028 - 2026;
  const tp9ProgressYears = Math.max(Math.min(productBaseYear, 2030) - 2028, 0);
  const tp9Years = 2030 - 2028;

  let totalProgressAtProductBaseYear = 0;

  if (tp7Years > 0) {
    totalProgressAtProductBaseYear += (tp7ProgressYears / tp7Years) * facilityTargetTp7;
  }

  if (tp8Years > 0) {
    totalProgressAtProductBaseYear += (tp8ProgressYears / tp8Years) * (facilityTargetTp8 - facilityTargetTp7);
  }

  if (tp9Years > 0) {
    totalProgressAtProductBaseYear += (tp9ProgressYears / tp9Years) * (facilityTargetTp9 - facilityTargetTp8);
  }

  const denominatorPart = 1 - totalProgressAtProductBaseYear;
  if (denominatorPart <= 0) return 0;

  return Math.max(0, (facilityTarget - totalProgressAtProductBaseYear) / denominatorPart);
}

export function calculateTargetEnergyForProduct(
  baselineEnergyIntensity: string | number | null,
  adjustedThroughput: number,
  improvementTarget: number,
): number {
  const intensity = toNumber(baselineEnergyIntensity);
  return intensity * adjustedThroughput * (1 - toNumber(improvementTarget));
}

export function resolveProductEnergyCarbonIntensity(product: ProductVariableEnergyConsumptionData | undefined): number {
  if (!product) return 0;

  if (product.energyCarbonIntensity != null) {
    return toNumber(product.energyCarbonIntensity);
  }

  const throughput = toNumber(product.throughput);
  return throughput > 0 ? toNumber(product.energy) / throughput : 0;
}

export function calculateThroughputValues(inputs: ThroughputCalculationInputs) {
  const improvementTarget = calculateFacilityImprovementTarget(
    inputs.referenceData,
    inputs.reportType,
    inputs.targetPeriodType,
  );

  const energyFuelDetails = inputs.performanceData?.energyFuelDetails;
  let gridDelivered = 0;
  let nonGridDelivered = 0;
  let chpDelivered = 0;

  if (energyFuelDetails) {
    gridDelivered = toNumber(energyFuelDetails.standardFuels?.['GRID_ELECTRICITY']?.deliveredEnergy);
    nonGridDelivered = toNumber(energyFuelDetails.standardFuels?.['NON_GRID_ELECTRICITY']?.deliveredEnergy);
    chpDelivered = toNumber(energyFuelDetails.electricitySuppliedFromCHP);
  }

  const throughputAdjustmentFactor = calculateThroughputAdjustmentFactor(gridDelivered, nonGridDelivered, chpDelivered);

  const baselineVariableEnergy = toNumber(inputs.referenceData?.baselineAndTargets?.baselineVariableEnergy);
  const baselineTotalThroughput = toNumber(inputs.referenceData?.baselineAndTargets?.totalThroughput);

  const baselineEnergyIntensity =
    baselineVariableEnergy > 0 && baselineTotalThroughput > 0
      ? baselineVariableEnergy / baselineTotalThroughput
      : inputs.referenceData?.baselineAndTargets?.baselineEnergyCarbonIntensity != null
        ? toNumber(inputs.referenceData.baselineAndTargets.baselineEnergyCarbonIntensity)
        : null;

  const adjustedThroughput = calculateAdjustedThroughput(
    inputs.actualThroughput,
    throughputAdjustmentFactor,
    inputs.referenceData?.baselineAndTargets?.usedReportingMechanism ?? false,
  );

  const hasVariableEnergy = inputs.referenceData?.baselineAndTargets?.variableEnergyType != null;

  let targetVariableEnergy: number | null = null;

  // For totals-only, calculate only if both intensity and throughput are available
  if (baselineEnergyIntensity != null && adjustedThroughput != null) {
    const sumOfIntensityTimesAdjustedThroughput = baselineEnergyIntensity * adjustedThroughput;
    targetVariableEnergy = calculateFacilityTargetVariableEnergy(
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

export function calculateActualEnergyTotal(energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails): number {
  let total = 0;

  // Sum primary energy from standard fuels
  if (energyFuelDetails?.standardFuels) {
    Object.values(energyFuelDetails.standardFuels).forEach((fuel) => {
      const primaryEnergy = toNumber(fuel?.primaryEnergy);
      if (primaryEnergy !== 0) total += primaryEnergy;
    });
  }

  // Sum primary energy from non-standard fuels
  if (energyFuelDetails?.nonStandardFuels) {
    energyFuelDetails.nonStandardFuels.forEach((fuel) => {
      const primaryEnergy = toNumber(fuel?.primaryEnergy);
      if (primaryEnergy !== 0) total += primaryEnergy;
    });
  }

  return total;
}

// sum delivered energy × primary energy factor for all fuels
function sumDeliveredTimesPrimaryFactor(energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails): number {
  let total = 0;

  if (energyFuelDetails?.standardFuels && FUEL_MAP) {
    Object.entries(energyFuelDetails.standardFuels).forEach(([fuelKey, fuel]) => {
      const fuelRef = FUEL_MAP[fuelKey as FuelTypeKey];
      if (fuelRef) {
        const delivered = toNumber(fuel?.deliveredEnergy);
        const factor = fuelRef.primaryEnergyConversionFactor;
        if (delivered !== 0) total += delivered * factor;
      }
    });
  }

  if (energyFuelDetails?.nonStandardFuels) {
    energyFuelDetails.nonStandardFuels.forEach((fuel) => {
      const delivered = toNumber(fuel?.deliveredEnergy);
      const factor = toNumber(fuel?.conversionFactor);
      if (delivered !== 0) total += delivered * factor;
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
): number {
  if (isCarbonMeasurement) {
    // Carbon-based: numerator = sum of primary CO2, denominator = sum of delivered × primary factor
    let totalPrimaryCo2 = 0;

    if (energyFuelDetails?.standardFuels && FUEL_MAP) {
      Object.entries(energyFuelDetails.standardFuels).forEach(([fuelKey, fuel]) => {
        if (FUEL_MAP[fuelKey as FuelTypeKey]) {
          const delivered = toNumber(fuel?.deliveredEnergy);
          const primaryFactor = FUEL_MAP[fuelKey as FuelTypeKey].primaryEnergyConversionFactor;

          const co2ConversionFactor = co2ConversionFactorForMeasurement(
            FUEL_MAP[fuelKey as FuelTypeKey].co2ConversionFactor,
            measurementUnit,
          );

          // For carbon-based, primaryEnergy field is used to store primary CO2 in the saved payload, but we recalculate for clarity
          const primaryCo2 = calculatePrimaryCarbon(delivered, primaryFactor, co2ConversionFactor, measurementUnit);
          if (primaryCo2 !== 0) totalPrimaryCo2 += primaryCo2;
        }
      });
    }

    if (energyFuelDetails?.nonStandardFuels) {
      energyFuelDetails.nonStandardFuels.forEach((fuel) => {
        const delivered = toNumber(fuel?.deliveredEnergy);
        const primaryFactor = 1; // Non-standard fuels always use 1
        const co2ConversionFactor = toNumber(fuel?.conversionFactor);
        const primaryCo2 = calculatePrimaryCarbon(delivered, primaryFactor, co2ConversionFactor, measurementUnit);
        if (primaryCo2 !== 0) totalPrimaryCo2 += primaryCo2;
      });
    }

    // The denominator must be delivered × primary factor, not sum of primary CO2
    const denominator = sumDeliveredTimesPrimaryFactor(energyFuelDetails);
    if (denominator === 0) {
      return totalPrimaryCo2 === 0 ? 0 : 1;
    }
    return totalPrimaryCo2 / denominator;
  }

  // Energy-based: sum of (primary energy x CO2 conversion factor) / actual energy total
  const actualEnergyTotal = calculateActualEnergyTotal(energyFuelDetails);
  if (actualEnergyTotal === 0) return 0;
  let weightedSum = 0;

  if (energyFuelDetails?.standardFuels && FUEL_MAP) {
    Object.entries(energyFuelDetails.standardFuels).forEach(([fuelKey, fuel]) => {
      const fuelRef = FUEL_MAP[fuelKey as FuelTypeKey];

      if (fuelRef) {
        const primaryEnergy = toNumber(fuel?.primaryEnergy);

        if (primaryEnergy !== 0) {
          const co2Factor = co2ConversionFactorForMeasurement(fuelRef.co2ConversionFactor, measurementUnit);
          weightedSum += primaryEnergy * co2Factor;
        }
      }
    });
  }

  if (energyFuelDetails?.nonStandardFuels) {
    energyFuelDetails.nonStandardFuels.forEach((fuel) => {
      const primaryEnergy = toNumber(fuel?.primaryEnergy);

      if (primaryEnergy !== 0) {
        const co2Factor = toNumber(fuel?.conversionFactor);
        weightedSum += primaryEnergy * co2Factor;
      }
    });
  }

  return weightedSum / actualEnergyTotal;
}
