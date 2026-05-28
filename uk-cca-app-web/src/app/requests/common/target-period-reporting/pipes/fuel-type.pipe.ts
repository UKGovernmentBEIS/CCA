import { Pipe, type PipeTransform } from '@angular/core';

import { FUEL_LABELS, type FuelTypeKey } from '../target-period-reporting-form.types';

export function transformFuelType(value: FuelTypeKey): (typeof FUEL_LABELS)[keyof typeof FUEL_LABELS] {
  return FUEL_LABELS[value];
}

@Pipe({ name: 'fuelType' })
export class FuelTypePipe implements PipeTransform {
  transform = transformFuelType;
}
