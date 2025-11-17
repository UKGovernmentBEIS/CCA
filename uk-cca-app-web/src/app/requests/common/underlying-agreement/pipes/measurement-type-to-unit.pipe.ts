import { Pipe, PipeTransform } from '@angular/core';

export enum MeasurementTypeToUnitEnum {
  ENERGY_KWH = 'kWh',
  ENERGY_MWH = 'MWh',
  ENERGY_GJ = 'GJ',
  CARBON_KG = 'kg',
  CARBON_TONNE = 'tonne',
}

export function transformMeasurementTypeToUnit(value: keyof typeof MeasurementTypeToUnitEnum): string {
  const text = MeasurementTypeToUnitEnum[value];
  if (!text) throw new Error('Invalid measurement type');
  return text;
}

@Pipe({ name: 'measurementTypeToUnit' })
export class MeasurementTypeToUnitPipe implements PipeTransform {
  transform = transformMeasurementTypeToUnit;
}
