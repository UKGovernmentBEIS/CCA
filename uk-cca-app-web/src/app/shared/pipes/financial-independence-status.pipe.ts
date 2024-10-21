import { Pipe, PipeTransform } from '@angular/core';

import { TargetUnitAccountDetailsDTO } from 'cca-api';

export const financialIndependenceStatusTypeMap: Record<
  TargetUnitAccountDetailsDTO['financialIndependenceStatus'],
  string
> = {
  NON_FINANCIALLY_INDEPENDENT: 'Non-financially independent',
  FINANCIALLY_INDEPENDENT: 'Financially independent',
};

@Pipe({ name: 'financialIndependenceStatus', standalone: true, pure: true })
export class FinancialIndependenceStatusPipe implements PipeTransform {
  transform(value: TargetUnitAccountDetailsDTO['financialIndependenceStatus']): string {
    return value ? financialIndependenceStatusTypeMap[value] : '';
  }
}
