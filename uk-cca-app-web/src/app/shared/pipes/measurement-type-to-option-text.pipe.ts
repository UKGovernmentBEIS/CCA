import { Pipe, PipeTransform } from '@angular/core';

export const MEASUREMENT_TYPE_TO_OPTION_TEXT_MAP = {
  ENERGY_KWH: 'Energy (kWh)',
  ENERGY_MWH: 'Energy (MWh)',
  ENERGY_GJ: 'Energy (GJ)',
  CARBON_KG: 'Carbon (kg)',
  CARBON_TONNE: 'Carbon (tonne)',
} as const;

export type MeasurementTypeKey = keyof typeof MEASUREMENT_TYPE_TO_OPTION_TEXT_MAP;
export type MeasurementOptionText = (typeof MEASUREMENT_TYPE_TO_OPTION_TEXT_MAP)[MeasurementTypeKey];

export function transformMeasurementType(value: string): MeasurementOptionText {
  const text = MEASUREMENT_TYPE_TO_OPTION_TEXT_MAP[value as MeasurementTypeKey];
  if (!text) throw new Error('Invalid measurement type');
  return text;
}

@Pipe({ name: 'measurementTypeToOptionText' })
export class MeasurementTypeToOptionTextPipe implements PipeTransform {
  transform(value: string): MeasurementOptionText {
    return transformMeasurementType(value);
  }
}
