import { Pipe, PipeTransform } from '@angular/core';

import { EligibilityDetailsAndAuthorisation } from 'cca-api';

export enum CaNameEnum {
  ENVIRONMENT_AGENCY = 'Environment Agency (England)',
  SCOTTISH_ENVIRONMENT_PROTECTION_AGENCY = 'Scottish Environment Protection Agency (Scotland)',
  DEPARTMENT_OF_AGRICULTURE_ENVIRONMENT_AND_RURAL_AFFAIRS = 'Department of Agriculture, Environment and Rural Affairs (Northern Ireland)',
  NATURAL_RESOURCES_WALES = 'Natural Resources Wales (Wales)',
  OTHER = 'Other',
}

@Pipe({
  name: 'caName',
  standalone: true,
})
export class CaNamePipe implements PipeTransform {
  transform(value: EligibilityDetailsAndAuthorisation['regulatorName']): string {
    const text = CaNameEnum[value];
    if (!text) throw new Error('invalid ca type');
    return text;
  }
}
