import { Pipe, PipeTransform } from '@angular/core';

export enum MeasurementTypeEnum {
  ENERGY_KWH = 'Energy (kWh)',
  ENERGY_MWH = 'Energy (MWh)',
  ENERGY_GJ = 'Energy (GJ)',
  CARBON_KG = 'Carbon (kg)',
  CARBON_TONNE = 'Carbon (tonne)',
}

export function transformMeasurementType(value: keyof typeof MeasurementTypeEnum): string {
  const text = MeasurementTypeEnum[value];
  if (!text) throw new Error('Invalid measurement type');
  return text;
}

@Pipe({
  name: 'measurementTypeToOptionText',
  standalone: true,
})
export class MeasurementTypeToOptionTextPipe implements PipeTransform {
  transform = transformMeasurementType;
}
