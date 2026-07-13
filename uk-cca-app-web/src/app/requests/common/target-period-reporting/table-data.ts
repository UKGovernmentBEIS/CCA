import { FUEL_LABELS, FuelReference, FuelTypeKey } from './target-period-reporting-form.types';

export const FUEL_MAP = {
  GRID_ELECTRICITY: {
    label: FUEL_LABELS.GRID_ELECTRICITY,
    co2ConversionFactor: 0.10046,
    primaryEnergyConversionFactor: 2.1,
  },
  NON_GRID_ELECTRICITY: {
    label: FUEL_LABELS.NON_GRID_ELECTRICITY,
    co2ConversionFactor: 0,
    primaryEnergyConversionFactor: 1,
  },
  NATURAL_GAS: {
    label: FUEL_LABELS.NATURAL_GAS,
    co2ConversionFactor: 0.18254,
    primaryEnergyConversionFactor: 1,
  },
  LPG: {
    label: FUEL_LABELS.LPG,
    co2ConversionFactor: 0.21449,
    primaryEnergyConversionFactor: 1,
  },
  GAS_DIESEL_OIL: {
    label: FUEL_LABELS.GAS_DIESEL_OIL,
    co2ConversionFactor: 0.25679,
    primaryEnergyConversionFactor: 1,
  },
  FUEL_OIL: {
    label: FUEL_LABELS.FUEL_OIL,
    co2ConversionFactor: 0.26816,
    primaryEnergyConversionFactor: 1,
  },
  KEROSENE: {
    label: FUEL_LABELS.KEROSENE,
    co2ConversionFactor: 0.24677,
    primaryEnergyConversionFactor: 1,
  },
  COAL: {
    label: FUEL_LABELS.COAL,
    co2ConversionFactor: 0.32463,
    primaryEnergyConversionFactor: 1,
  },
  COKE: {
    label: FUEL_LABELS.COKE,
    co2ConversionFactor: 0.429,
    primaryEnergyConversionFactor: 1,
  },
  PETROL: {
    label: FUEL_LABELS.PETROL,
    co2ConversionFactor: 0.22719,
    primaryEnergyConversionFactor: 1,
  },
  NITROGEN_COOLING: {
    label: FUEL_LABELS.NITROGEN_COOLING,
    co2ConversionFactor: 0.10046,
    primaryEnergyConversionFactor: 2.1,
  },
  CARBON_DIOXIDE_COOLING: {
    label: FUEL_LABELS.CARBON_DIOXIDE_COOLING,
    co2ConversionFactor: 0.10046,
    primaryEnergyConversionFactor: 2.1,
  },
  ETHANE: {
    label: FUEL_LABELS.ETHANE,
    co2ConversionFactor: 0.19983,
    primaryEnergyConversionFactor: 1,
  },
  NAPHTHA: {
    label: FUEL_LABELS.NAPHTHA,
    co2ConversionFactor: 0.23651,
    primaryEnergyConversionFactor: 1,
  },
  PETROLEUM_COKE: {
    label: FUEL_LABELS.PETROLEUM_COKE,
    co2ConversionFactor: 0.34095,
    primaryEnergyConversionFactor: 1,
  },
  REFINERY_GAS: {
    label: FUEL_LABELS.REFINERY_GAS,
    co2ConversionFactor: 0.18324,
    primaryEnergyConversionFactor: 1,
  },
} satisfies Record<FuelTypeKey, FuelReference>;
