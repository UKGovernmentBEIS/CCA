import {
  calculatePrimaryCarbon,
  calculateThroughputAdjustmentFactor,
  EnergyFuelRow,
  FUEL_MAP,
  FuelTypeKey,
  isCarbonMeasurementType,
} from '@requests/common';
import { MEASUREMENT_TYPE_TO_UNIT_MAP, MeasurementType, MeasurementUnit } from '@shared/pipes';
import { toNumber } from '@shared/utils';

import { PerformanceDataFacilityEnergyFuelDetails } from 'cca-api';

const FUEL_MAP_ORDER: Record<string, number> = Object.fromEntries(
  Object.keys(FUEL_MAP).map((key, index) => [key, index]),
);

export function resolveMeasurementTypeUnit(measurementType?: MeasurementType): MeasurementUnit {
  return measurementType ? MEASUREMENT_TYPE_TO_UNIT_MAP[measurementType] : MEASUREMENT_TYPE_TO_UNIT_MAP.ENERGY_KWH;
}

export function resolveCo2FactorUnit(measurementUnit: MeasurementUnit): MeasurementUnit {
  return isCarbonMeasurementType(measurementUnit) ? MEASUREMENT_TYPE_TO_UNIT_MAP.ENERGY_KWH : measurementUnit;
}

export function buildSubmittedEnergyFuelRows(
  energyFuelDetails?: PerformanceDataFacilityEnergyFuelDetails,
  measurementUnit: MeasurementUnit = MEASUREMENT_TYPE_TO_UNIT_MAP.ENERGY_KWH,
): EnergyFuelRow[] {
  const carbonMeasurement = isCarbonMeasurementType(measurementUnit);

  const rows = (energyFuelDetails?.fuels ?? [])
    .filter((fuel) => toNumber(fuel.deliveredEnergy) !== 0)
    .map((fuel) => {
      const deliveredEnergy = toNumber(fuel.deliveredEnergy);
      const co2ConversionFactor = toNumber(fuel.conversionFactor);
      const primaryEnergyConversionFactor = toNumber(fuel.primaryConversionFactor);

      return {
        fuelType: fuel.name,
        co2ConversionFactor,
        deliveredEnergy,
        primaryEnergyConversionFactor,
        primaryEnergy: carbonMeasurement
          ? calculatePrimaryCarbon(deliveredEnergy, primaryEnergyConversionFactor, co2ConversionFactor, measurementUnit)
          : toNumber(fuel.primaryEnergy),
        isCustom: !fuel.fixedConversionFactorCode,
        _sortKey: fuel.fixedConversionFactorCode as FuelTypeKey | undefined,
      };
    });

  return rows
    .sort((a, b) => {
      const orderA = a._sortKey ? (FUEL_MAP_ORDER[a._sortKey] ?? Infinity) : Infinity;
      const orderB = b._sortKey ? (FUEL_MAP_ORDER[b._sortKey] ?? Infinity) : Infinity;
      return orderA - orderB;
    })
    .map(({ _sortKey: _, ...row }) => row);
}

export function calculateSubmittedThroughputAdjustmentFactor(
  energyFuelDetails?: PerformanceDataFacilityEnergyFuelDetails,
): number {
  const fuels = energyFuelDetails?.fuels ?? [];

  const gridElectricity = toNumber(
    fuels.find((fuel) => fuel.fixedConversionFactorCode === 'GRID_ELECTRICITY')?.deliveredEnergy,
  );

  const nonGridElectricity = toNumber(
    fuels.find((fuel) => fuel.fixedConversionFactorCode === 'NON_GRID_ELECTRICITY')?.deliveredEnergy,
  );

  const chpElectricity = toNumber(energyFuelDetails?.electricitySuppliedFromCHP);

  return calculateThroughputAdjustmentFactor(gridElectricity, nonGridElectricity, chpElectricity);
}
