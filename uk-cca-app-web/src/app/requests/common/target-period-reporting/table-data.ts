import { transformFuelType } from './pipes/fuel-type.pipe';
import { FuelReference, FuelTypeKey } from './target-period-reporting-form.types';

export const FUEL_MAP = {
  GRID_ELECTRICITY: {
    label: transformFuelType('GRID_ELECTRICITY'),
    co2ConversionFactor: 0.10046,
    primaryEnergyConversionFactor: 2.1,
  },
  NON_GRID_ELECTRICITY: {
    label: transformFuelType('NON_GRID_ELECTRICITY'),
    co2ConversionFactor: 0,
    primaryEnergyConversionFactor: 1,
  },
  NATURAL_GAS: {
    label: transformFuelType('NATURAL_GAS'),
    co2ConversionFactor: 0.18254,
    primaryEnergyConversionFactor: 1,
  },
  LPG: {
    label: transformFuelType('LPG'),
    co2ConversionFactor: 0.21449,
    primaryEnergyConversionFactor: 1,
  },
  GAS_DIESEL_OIL: {
    label: transformFuelType('GAS_DIESEL_OIL'),
    co2ConversionFactor: 0.25679,
    primaryEnergyConversionFactor: 1,
  },
  FUEL_OIL: {
    label: transformFuelType('FUEL_OIL'),
    co2ConversionFactor: 0.26803,
    primaryEnergyConversionFactor: 1,
  },
  KEROSENE: {
    label: transformFuelType('KEROSENE'),
    co2ConversionFactor: 0.24677,
    primaryEnergyConversionFactor: 1,
  },
  COAL: {
    label: transformFuelType('COAL'),
    co2ConversionFactor: 0.32463,
    primaryEnergyConversionFactor: 1,
  },
  COKE: {
    label: transformFuelType('COKE'),
    co2ConversionFactor: 0.429,
    primaryEnergyConversionFactor: 1,
  },
  PETROL: {
    label: transformFuelType('PETROL'),
    co2ConversionFactor: 0.22719,
    primaryEnergyConversionFactor: 1,
  },
  NITROGEN_COOLING: {
    label: transformFuelType('NITROGEN_COOLING'),
    co2ConversionFactor: 0.10046,
    primaryEnergyConversionFactor: 2.1,
  },
  CARBON_DIOXIDE_COOLING: {
    label: transformFuelType('CARBON_DIOXIDE_COOLING'),
    co2ConversionFactor: 0.10046,
    primaryEnergyConversionFactor: 2.1,
  },
  ETHANE: {
    label: transformFuelType('ETHANE'),
    co2ConversionFactor: 0.19983,
    primaryEnergyConversionFactor: 1,
  },
  NAPHTHA: {
    label: transformFuelType('NAPHTHA'),
    co2ConversionFactor: 0.23651,
    primaryEnergyConversionFactor: 1,
  },
  PETROLEUM_COKE: {
    label: transformFuelType('PETROLEUM_COKE'),
    co2ConversionFactor: 0.34095,
    primaryEnergyConversionFactor: 1,
  },
  REFINERY_GAS: {
    label: transformFuelType('REFINERY_GAS'),
    co2ConversionFactor: 0.18324,
    primaryEnergyConversionFactor: 1,
  },
} satisfies Record<FuelTypeKey, FuelReference>;
