import {
  PerformanceDataFacilityBaselineAndTargets,
  PerformanceDataFacilityFuelEnergyConsumption,
  PerformanceDataFacilityInputEnergyFuelDetails,
  PerformanceDataFacilityReferenceData,
} from 'cca-api';

import { FUEL_MAP } from './table-data';
import {
  EnergyFuelRow,
  FuelReference,
  FuelRow,
  FuelTypeKey,
  ThroughputCalculationInputs,
} from './target-period-reporting-form.types';

export const toNumber = (v?: string | number | null): number => (v == null ? 0 : Number(v));

const CO2_BASE_UNIT = 'kWh';

function convertCo2FactorFromKWh(co2FactorPerKWh: number, measurementUnit: string): number {
  switch (measurementUnit) {
    case CO2_BASE_UNIT:
      return co2FactorPerKWh;
    case 'MWh':
      return co2FactorPerKWh * 1000;
    case 'GJ':
      return (co2FactorPerKWh * 1000) / 3.6;
    default:
      return co2FactorPerKWh;
  }
}

function isCarbonMeasurementType(measurementType: string): boolean {
  return ['kg', 'tonne'].includes(measurementType);
}

function co2MeasurementUnit(measurementType: string): string {
  return isCarbonMeasurementType(measurementType) ? 'kWh' : measurementType;
}

export function co2ConversionFactorForMeasurement(co2FactorPerKWh: number, measurementType: string): number {
  return convertCo2FactorFromKWh(co2FactorPerKWh, co2MeasurementUnit(measurementType));
}

export function calculatePrimaryEnergy(deliveredEnergy: string | number | null, primaryFactor: number): number {
  return toNumber(deliveredEnergy) * primaryFactor;
}

export function calculatePrimaryCarbon(
  deliveredEnergy: string | number | null,
  primaryFactor: number,
  co2Factor: number,
): number {
  return toNumber(deliveredEnergy) * primaryFactor * co2Factor;
}

export function mapStandardFuelRows(
  fuelEntries: [FuelTypeKey, FuelReference][],
  standardFuels?: Record<string, PerformanceDataFacilityFuelEnergyConsumption>,
  measurementType = CO2_BASE_UNIT,
): FuelRow[] {
  return fuelEntries.map(([fuelKey, fuel]) => {
    const deliveredEnergy = standardFuels?.[fuelKey]?.deliveredEnergy ?? '0';
    const co2ConversionFactor = co2ConversionFactorForMeasurement(fuel.co2ConversionFactor, measurementType);

    const primaryEnergy = calculatePrimaryEnergy(deliveredEnergy, fuel.primaryEnergyConversionFactor);

    const primaryCarbon = calculatePrimaryCarbon(
      deliveredEnergy,
      fuel.primaryEnergyConversionFactor,
      co2ConversionFactor,
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

export function calculateThroughputAdjustmentFactor(
  gridElectricity: number,
  nonGridElectricity: number,
  chpElectricity: number,
): number {
  const numerator = gridElectricity + nonGridElectricity;
  const denominator = numerator + chpElectricity;
  // SRM form validation prevents CHP > 0 when both grid and non-grid electricity are 0.
  return denominator === 0 ? 1 : numerator / denominator;
}

export function buildEnergyFuelRows(
  energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails,
  measurementType = CO2_BASE_UNIT,
): EnergyFuelRow[] {
  const carbonMeasurement = isCarbonMeasurementType(measurementType);

  const standardRows = (Object.entries(FUEL_MAP) as [FuelTypeKey, FuelReference][])
    .filter(([fuelKey]) => toNumber(energyFuelDetails?.standardFuels?.[fuelKey]?.deliveredEnergy) !== 0)
    .map(([fuelKey, fuel]) => {
      const deliveredEnergy = toNumber(energyFuelDetails?.standardFuels?.[fuelKey]?.deliveredEnergy);
      const co2ConversionFactor = co2ConversionFactorForMeasurement(fuel.co2ConversionFactor, measurementType);
      const primaryValue = carbonMeasurement
        ? calculatePrimaryCarbon(deliveredEnergy, fuel.primaryEnergyConversionFactor, co2ConversionFactor)
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
        ? calculatePrimaryCarbon(deliveredEnergy, 1, co2ConversionFactor)
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

export function calculateThroughputValues(inputs: ThroughputCalculationInputs) {
  const improvementTarget = calculateFacilityImprovementTarget(
    inputs.referenceData,
    inputs.reportType,
    inputs.targetPeriodType,
  );

  const throughputAdjustmentFactor = calculateThroughputAdjustmentFactor(
    toNumber(inputs.performanceData?.energyFuelDetails?.standardFuels?.['GRID_ELECTRICITY']?.deliveredEnergy),
    toNumber(inputs.performanceData?.energyFuelDetails?.standardFuels?.['NON_GRID_ELECTRICITY']?.deliveredEnergy),
    toNumber(inputs.performanceData?.energyFuelDetails?.electricitySuppliedFromCHP),
  );

  const baselineEnergyIntensity =
    inputs.referenceData?.baselineAndTargets?.baselineEnergyCarbonIntensity != null
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

export function calculateWeightedConversionFactor(
  energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails,
  isCarbonMeasurement: boolean,
): number {
  const actualEnergyTotal = calculateActualEnergyTotal(energyFuelDetails);

  if (actualEnergyTotal === 0) return 0;

  if (isCarbonMeasurement) {
    // Carbon-based: primaryEnergy already stores primary CO2 in the saved payload
    let totalPrimaryCo2 = 0;

    if (energyFuelDetails?.standardFuels && FUEL_MAP) {
      Object.entries(energyFuelDetails.standardFuels).forEach(([fuelKey, fuel]) => {
        if (FUEL_MAP[fuelKey as FuelTypeKey]) {
          const primaryCo2 = toNumber(fuel?.primaryEnergy);
          if (primaryCo2 !== 0) totalPrimaryCo2 += primaryCo2;
        }
      });
    }

    if (energyFuelDetails?.nonStandardFuels) {
      energyFuelDetails.nonStandardFuels.forEach((fuel) => {
        const primaryCo2 = toNumber(fuel?.primaryEnergy);
        if (primaryCo2 !== 0) totalPrimaryCo2 += primaryCo2;
      });
    }

    return totalPrimaryCo2 / actualEnergyTotal;
  }

  // Energy-based: sum of (primary energy x CO2 conversion factor) / actual energy total
  let weightedSum = 0;

  if (energyFuelDetails?.standardFuels && FUEL_MAP) {
    Object.entries(energyFuelDetails.standardFuels).forEach(([fuelKey, fuel]) => {
      const fuelRef = FUEL_MAP[fuelKey as FuelTypeKey];

      if (fuelRef) {
        const primaryEnergy = toNumber(fuel?.primaryEnergy);

        if (primaryEnergy !== 0) {
          const co2Factor = fuelRef.co2ConversionFactor;
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

export function calculateActualCo2Emissions(
  energyFuelDetails: PerformanceDataFacilityInputEnergyFuelDetails,
  isCarbonMeasurement: boolean,
): number {
  if (isCarbonMeasurement) {
    // Carbon-based: primaryEnergy already stores primary CO2 in the saved payload
    let totalPrimaryCo2 = 0;

    if (energyFuelDetails?.standardFuels && FUEL_MAP) {
      Object.entries(energyFuelDetails.standardFuels).forEach(([fuelKey, fuel]) => {
        if (FUEL_MAP[fuelKey as FuelTypeKey]) {
          const primaryCo2 = toNumber(fuel?.primaryEnergy);
          if (primaryCo2 !== 0) totalPrimaryCo2 += primaryCo2;
        }
      });
    }

    if (energyFuelDetails?.nonStandardFuels) {
      energyFuelDetails.nonStandardFuels.forEach((fuel) => {
        const primaryCo2 = toNumber(fuel?.primaryEnergy);
        if (primaryCo2 !== 0) totalPrimaryCo2 += primaryCo2;
      });
    }

    return totalPrimaryCo2 / 1000;
  }

  // Energy-based: sum of (primary energy x CO2 conversion factor) / 1000
  let totalWeightedEnergy = 0;

  if (energyFuelDetails?.standardFuels && FUEL_MAP) {
    Object.entries(energyFuelDetails.standardFuels).forEach(([fuelKey, fuel]) => {
      const fuelRef = FUEL_MAP[fuelKey as FuelTypeKey];

      if (fuelRef) {
        const primaryEnergy = toNumber(fuel?.primaryEnergy);

        if (primaryEnergy !== 0) {
          const co2Factor = fuelRef.co2ConversionFactor;
          totalWeightedEnergy += primaryEnergy * co2Factor;
        }
      }
    });
  }

  if (energyFuelDetails?.nonStandardFuels) {
    energyFuelDetails.nonStandardFuels.forEach((fuel) => {
      const primaryEnergy = toNumber(fuel?.primaryEnergy);

      if (primaryEnergy !== 0) {
        const co2Factor = toNumber(fuel?.conversionFactor);
        totalWeightedEnergy += primaryEnergy * co2Factor;
      }
    });
  }

  return totalWeightedEnergy / 1000;
}
