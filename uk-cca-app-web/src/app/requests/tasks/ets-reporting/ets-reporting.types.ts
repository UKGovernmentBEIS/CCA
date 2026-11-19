import { EmissionsTradingSchemeDataInputDetails, FacilityTargetComposition, RequestTaskPayload } from 'cca-api';

export const ETS_ENERGY_FUEL_DETAILS_SUBTASK = 'etsEnergyFuelDetails';

export type EtsMeasurementType = FacilityTargetComposition['measurementType'];

/** Temporary mirror of the Java submit task payload until its schema is registered in Swagger. */
export interface EtsReportingTaskPayload extends RequestTaskPayload {
  targetYear: number;
  uketsId?: string;
  measurementType: EtsMeasurementType;
  emissionsTradingSchemeDataInputDetails?: EmissionsTradingSchemeDataInputDetails;
  sectionsCompleted: Record<string, string>;
}

export interface EtsFuelRowInput {
  fuelKey: string | null;
  fuelType: string;
  co2ConversionFactor: string | number;
  deliveredEnergy: string | number | null;
  primaryEnergyConversionFactor: string;
  isCustom: boolean;
}

export interface EtsFuelSummaryRow extends EtsFuelRowInput {
  primaryEnergy: string;
  nonStandardFuelIndex?: number;
}
