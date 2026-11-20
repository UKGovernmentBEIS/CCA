import { FUEL_MAP } from '@requests/common/target-period-reporting/table-data';
import {
  FuelReference,
  FuelTypeKey,
} from '@requests/common/target-period-reporting/target-period-reporting-form.types';
import { primaryCarbonDisplayUnit, roundHalfUpTo7Decimals } from '@requests/common/target-period-reporting/utils';
import { MEASUREMENT_TYPE_TO_UNIT_MAP, MeasurementUnit } from '@shared/pipes';
import { toBigNumber } from '@shared/utils';
import BigNumber from 'bignumber.js';

import {
  EmissionsTradingSchemeDataInputEnergyFuelDetails,
  PerformanceDataFacilityFuelEnergyConsumption,
  PerformanceDataFacilityNonStandardFuel,
} from 'cca-api';

import { EtsFuelRowInput, EtsFuelSummaryRow, EtsMeasurementType } from './ets-reporting.types';

const EXCLUDED_FUELS = new Set<FuelTypeKey>([
  'GRID_ELECTRICITY',
  'NON_GRID_ELECTRICITY',
  'NITROGEN_COOLING',
  'CARBON_DIOXIDE_COOLING',
]);

// ETS presentation follows its mockups; keys, order and factors remain shared with TPR.
const ETS_FUEL_LABELS: Partial<Record<FuelTypeKey, string>> = {
  NATURAL_GAS: 'Natural gas',
  GAS_DIESEL_OIL: 'Gas oil / diesel',
  FUEL_OIL: 'Fuel oil',
  PETROLEUM_COKE: 'Petroleum coke',
  REFINERY_GAS: 'Refinery gas',
};

export const ETS_FUEL_ENTRIES = (Object.entries(FUEL_MAP) as [FuelTypeKey, FuelReference][]).filter(
  ([key]) => !EXCLUDED_FUELS.has(key),
);

export function getEtsFuelLabel(fuelKey: FuelTypeKey | undefined, fallback: string): string {
  return ETS_FUEL_LABELS[fuelKey] ?? FUEL_MAP[fuelKey]?.label ?? fallback;
}

export function isEtsStandardFuelName(name: string): boolean {
  const normalisedName = name.trim().toLowerCase();
  return ETS_FUEL_ENTRIES.some(
    ([key, reference]) => getEtsFuelLabel(key, reference.label).toLowerCase() === normalisedName,
  );
}

// Keep division independent of any application-wide BigNumber configuration, matching the API.
const FactorDecimal = BigNumber.clone({ DECIMAL_PLACES: 20, ROUNDING_MODE: BigNumber.ROUND_HALF_UP });

/** Plain decimal strings keep persisted BigDecimal scientific notation usable in decimal inputs. */
export function normaliseEtsDecimal(value: string | null | undefined): string | null | undefined {
  if (value == null || value.trim() === '') return value;
  try {
    const decimal = new BigNumber(value);
    return decimal.isFinite() ? decimal.toFixed() : value;
  } catch {
    return value;
  }
}

export function getEtsMeasurementUnit(measurementType: EtsMeasurementType): MeasurementUnit {
  return MEASUREMENT_TYPE_TO_UNIT_MAP[measurementType];
}

export function isEtsCarbonMeasurement(measurementType: EtsMeasurementType): boolean {
  return measurementType === 'CARBON_KG' || measurementType === 'CARBON_TONNE';
}

export function getEtsPrimaryUnit(measurementType: EtsMeasurementType): string {
  const unit = getEtsMeasurementUnit(measurementType);
  return isEtsCarbonMeasurement(measurementType) ? primaryCarbonDisplayUnit(unit) : unit;
}

export function getEtsDeliveredUnit(measurementType: EtsMeasurementType): MeasurementUnit {
  return isEtsCarbonMeasurement(measurementType) ? 'kWh' : getEtsMeasurementUnit(measurementType);
}

export function etsCo2ConversionFactor(baseFactor: number, measurementType: EtsMeasurementType): BigNumber {
  const factor = new FactorDecimal(baseFactor);
  if (measurementType === 'ENERGY_GJ') return new BigNumber(factor.times(1000).div('3.6').toFixed());
  return new BigNumber((measurementType === 'ENERGY_MWH' ? factor.times(1000) : factor).toFixed());
}

export function mapEtsStandardFuelRows(
  measurementType: EtsMeasurementType,
  standardFuels?: Record<string, PerformanceDataFacilityFuelEnergyConsumption>,
): EtsFuelRowInput[] {
  return ETS_FUEL_ENTRIES.map(([fuelKey, reference]) => ({
    fuelKey,
    fuelType: getEtsFuelLabel(fuelKey, reference.label),
    co2ConversionFactor: etsCo2ConversionFactor(reference.co2ConversionFactor, measurementType).toFixed(),
    deliveredEnergy: normaliseEtsDecimal(standardFuels?.[fuelKey]?.deliveredEnergy) ?? '0',
    primaryEnergyConversionFactor: String(reference.primaryEnergyConversionFactor),
    isCustom: false,
  }));
}

function rowAmounts(measurementType: EtsMeasurementType, row: EtsFuelRowInput) {
  const reference = row.isCustom ? null : ETS_FUEL_ENTRIES.find(([key]) => key === row.fuelKey)?.[1];
  if (!row.isCustom && !reference) throw new Error('Unknown UK ETS fuel type');
  const primaryFactor = reference ? reference.primaryEnergyConversionFactor : 1;
  const co2Factor = reference
    ? etsCo2ConversionFactor(reference.co2ConversionFactor, measurementType)
    : toBigNumber(row.co2ConversionFactor);
  const primaryEnergy = toBigNumber(row.deliveredEnergy).times(primaryFactor);
  const carbonKg = primaryEnergy.times(co2Factor);
  const carbonTonnes = carbonKg.times('0.001');
  const primaryAmount = isEtsCarbonMeasurement(measurementType)
    ? measurementType === 'CARBON_TONNE'
      ? carbonTonnes
      : carbonKg
    : primaryEnergy;
  return { primaryAmount, carbonTonnes };
}

export function calculateEtsPrimaryAmount(measurementType: EtsMeasurementType, row: EtsFuelRowInput): BigNumber {
  return rowAmounts(measurementType, row).primaryAmount;
}

export function createEtsFuelDetails(
  measurementType: EtsMeasurementType,
  rows: EtsFuelRowInput[],
): EmissionsTradingSchemeDataInputEnergyFuelDetails {
  const standardFuels: Record<string, PerformanceDataFacilityFuelEnergyConsumption> = {};
  const nonStandardFuels: PerformanceDataFacilityNonStandardFuel[] = [];
  let totalPrimary = new BigNumber(0);
  let totalCarbon = new BigNumber(0);

  rows.forEach((row) => {
    if (!row.isCustom && !toBigNumber(row.deliveredEnergy).isGreaterThan(0)) return;
    const { primaryAmount, carbonTonnes } = rowAmounts(measurementType, row);
    totalPrimary = totalPrimary.plus(primaryAmount);
    totalCarbon = totalCarbon.plus(carbonTonnes);
    const consumption: PerformanceDataFacilityFuelEnergyConsumption = {
      deliveredEnergy: roundHalfUpTo7Decimals(row.deliveredEnergy),
      primaryEnergy: roundHalfUpTo7Decimals(primaryAmount),
    };
    if (row.isCustom) {
      nonStandardFuels.push({
        ...consumption,
        name: row.fuelType.trim(),
        conversionFactor: roundHalfUpTo7Decimals(row.co2ConversionFactor),
      });
    } else {
      standardFuels[row.fuelKey] = consumption;
    }
  });

  return {
    standardFuels,
    nonStandardFuels,
    totalPrimaryEnergy: roundHalfUpTo7Decimals(totalPrimary),
    totalCo2Emissions: roundHalfUpTo7Decimals(totalCarbon),
  };
}

export function buildEtsFuelSummaryRows(
  measurementType: EtsMeasurementType,
  details?: EmissionsTradingSchemeDataInputEnergyFuelDetails,
): EtsFuelSummaryRow[] {
  const customRows = (details?.nonStandardFuels ?? []).map<EtsFuelRowInput & { nonStandardFuelIndex: number }>(
    (fuel, nonStandardFuelIndex) => ({
      fuelKey: null,
      fuelType: fuel.name,
      co2ConversionFactor: fuel.conversionFactor,
      deliveredEnergy: fuel.deliveredEnergy,
      primaryEnergyConversionFactor: '1',
      isCustom: true,
      nonStandardFuelIndex,
    }),
  );
  return [...mapEtsStandardFuelRows(measurementType, details?.standardFuels), ...customRows]
    .filter((row) => {
      const delivered = toBigNumber(row.deliveredEnergy);
      return delivered.isFinite() && delivered.isGreaterThan(0);
    })
    .map((row) => ({
      ...row,
      co2ConversionFactor: roundHalfUpTo7Decimals(row.co2ConversionFactor),
      deliveredEnergy: roundHalfUpTo7Decimals(row.deliveredEnergy),
      primaryEnergy: roundHalfUpTo7Decimals(calculateEtsPrimaryAmount(measurementType, row)),
    }));
}
