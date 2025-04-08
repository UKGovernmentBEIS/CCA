import { Pipe, PipeTransform } from '@angular/core';

export enum FacilityStatusEnum {
  NEW = 'New',
  LIVE = 'Live',
  EXCLUDED = 'Excluded',
  INACTIVE = 'Inactive',
}

@Pipe({
  name: 'facilityStatus',
  standalone: true,
})
export class FacilityStatusPipe implements PipeTransform {
  transform(value: string): string {
    const text = FacilityStatusEnum[value];
    if (!text) throw new Error('invalid status type for facility');
    return text;
  }
}
