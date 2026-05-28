import { PerformanceDataFacilityInputData, PerformanceDataFacilityReferenceData } from 'cca-api';

export const TPR_FORM_ENERGY_FUEL_DETAILS_SUBTASK = 'tprEnergyFuelDetails';
export const TPR_FORM_THROUGHPUT_DETAILS_SUBTASK = 'tprThroughputDetails';

export type FuelTypeKey =
  | 'GRID_ELECTRICITY'
  | 'NON_GRID_ELECTRICITY'
  | 'NATURAL_GAS'
  | 'LPG'
  | 'GAS_DIESEL_OIL'
  | 'FUEL_OIL'
  | 'KEROSENE'
  | 'COAL'
  | 'COKE'
  | 'PETROL'
  | 'NITROGEN_COOLING'
  | 'CARBON_DIOXIDE_COOLING'
  | 'ETHANE'
  | 'NAPHTHA'
  | 'PETROLEUM_COKE'
  | 'REFINERY_GAS';

export const FUEL_LABELS = {
  GRID_ELECTRICITY: 'Grid electricity and electricity from combustion of a renewable fuel',
  NON_GRID_ELECTRICITY: 'Non-grid electricity from renewable sources (PV, hydro and wind)',
  NATURAL_GAS: 'Natural gas',
  LPG: 'LPG',
  GAS_DIESEL_OIL: 'Gas oil/Diesel',
  FUEL_OIL: 'Fuel Oil',
  KEROSENE: 'Kerosene',
  COAL: 'Coal',
  COKE: 'Coke',
  PETROL: 'Petrol',
  NITROGEN_COOLING: 'Nitrogen cooling',
  CARBON_DIOXIDE_COOLING: 'Carbon dioxide cooling',
  ETHANE: 'Ethane',
  NAPHTHA: 'Naphtha',
  PETROLEUM_COKE: 'Petroleum Coke',
  REFINERY_GAS: 'Refinery Gas',
} as const satisfies Record<FuelTypeKey, string>;

export type FuelReference = {
  label: (typeof FUEL_LABELS)[keyof typeof FUEL_LABELS];
  co2ConversionFactor: number;
  primaryEnergyConversionFactor: number;
};

export type EnergyFuelRow = {
  fuelType: string;
  co2ConversionFactor: number;
  deliveredEnergy: number;
  primaryEnergyConversionFactor: number;
  primaryEnergy: number;
  isCustom: boolean;
};

export type FuelRow = {
  fuelKey: FuelTypeKey;
  label:
    | 'LPG'
    | 'Grid electricity and electricity from combustion of a renewable fuel'
    | 'Non-grid electricity from renewable sources (PV, hydro and wind)'
    | 'Natural gas'
    | 'Gas oil/Diesel'
    | 'Fuel Oil'
    | 'Kerosene'
    | 'Coal'
    | 'Coke'
    | 'Petrol'
    | 'Nitrogen cooling'
    | 'Carbon dioxide cooling'
    | 'Ethane'
    | 'Naphtha'
    | 'Petroleum Coke'
    | 'Refinery Gas';
  deliveredEnergy: number;
  co2ConversionFactor: number;
  primaryEnergyConversionFactor: number;
  primaryEnergy: number;
  primaryCarbon: number;
};

export type ThroughputCalculationInputs = {
  referenceData: PerformanceDataFacilityReferenceData;
  performanceData: PerformanceDataFacilityInputData;
  reportType: 'INTERIM' | 'FINAL';
  targetPeriodType: 'TP5' | 'TP6' | 'TP7' | 'TP8' | 'TP9';
  actualThroughput: string | null;
};
