import { Pipe, PipeTransform } from '@angular/core';

import { FacilityDetails } from 'cca-api';

export enum ApplicationReasonTypeEnum {
  NEW_AGREEMENT = 'New entrant',
  CHANGE_OF_OWNERSHIP = 'Change of ownership',
}

@Pipe({ name: 'applicationReasonType' })
export class ApplicationReasonTypePipe implements PipeTransform {
  transform(value: FacilityDetails['applicationReason']): string {
    const text = ApplicationReasonTypeEnum[value];
    if (!text) throw new Error('invalid application reason');
    return text;
  }
}
