import { Pipe, PipeTransform } from '@angular/core';

import { TargetUnitAccountDetailsDTO } from 'cca-api';

export const operatorTypeMap: Record<TargetUnitAccountDetailsDTO['operatorType'], string> = {
  LIMITED_COMPANY: 'Limited company',
  PARTNERSHIP: 'Partnership',
  SOLE_TRADER: 'Sole trader',
  NONE: 'None',
};

export const operatorTypeOptions = [
  {
    text: transformOperatorType('LIMITED_COMPANY'),
    value: 'LIMITED_COMPANY',
  },
  {
    text: transformOperatorType('PARTNERSHIP'),
    value: 'PARTNERSHIP',
  },
  {
    text: transformOperatorType('SOLE_TRADER'),
    value: 'SOLE_TRADER',
  },
  {
    text: 'None of the above',
    value: 'NONE',
  },
];

export function transformOperatorType(value: TargetUnitAccountDetailsDTO['operatorType']): string {
  return value ? operatorTypeMap[value] : '';
}

@Pipe({ name: 'operatorType', standalone: true, pure: true })
export class OperatorTypePipe implements PipeTransform {
  transform = transformOperatorType;
}
