import { Pipe, PipeTransform } from '@angular/core';

import { Facility } from 'cca-api';

export enum FacilityStatusEnum {
  NEW = 'New',
  LIVE = 'Live',
  EXCLUDED = 'Excluded',
}

@Pipe({
  name: 'facilityStatus',
  standalone: true,
})
export class FacilityStatusPipe implements PipeTransform {
  transform(value: Facility['status']): string {
    const text = FacilityStatusEnum[value];
    if (!text) throw new Error('invalid status type for facility');
    return text;
  }
}
