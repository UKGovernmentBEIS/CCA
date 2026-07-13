import { Pipe, PipeTransform } from '@angular/core';

export const MEASUREMENT_TYPE_TO_UNIT_MAP = {
  ENERGY_KWH: 'kWh',
  ENERGY_MWH: 'MWh',
  ENERGY_GJ: 'GJ',
  CARBON_KG: 'kg',
  CARBON_TONNE: 'tonne',
} as const;

export type MeasurementType = keyof typeof MEASUREMENT_TYPE_TO_UNIT_MAP;
export type MeasurementUnit = (typeof MEASUREMENT_TYPE_TO_UNIT_MAP)[MeasurementType];

export function transformMeasurementTypeToUnit(value: string): MeasurementUnit {
  const unit = MEASUREMENT_TYPE_TO_UNIT_MAP[value as MeasurementType];
  if (!unit) throw new Error('Invalid measurement type');
  return unit;
}

@Pipe({ name: 'measurementTypeToUnit' })
export class MeasurementTypeToUnitPipe implements PipeTransform {
  transform(value: string): MeasurementUnit {
    return transformMeasurementTypeToUnit(value);
  }
}
