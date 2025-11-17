import { Pipe, PipeTransform } from '@angular/core';

import { AdminTerminationFinalDecisionReasonDetails } from 'cca-api';

export const adminTerminationFinalDecisionTypeMap: Record<
  AdminTerminationFinalDecisionReasonDetails['finalDecisionType'],
  string
> = {
  TERMINATE_AGREEMENT: 'Terminate agreement',
  WITHDRAW_TERMINATION: 'Withdraw termination',
};

export function transformAdminTerminationFinalDecisionType(
  type: AdminTerminationFinalDecisionReasonDetails['finalDecisionType'],
): string {
  return type ? adminTerminationFinalDecisionTypeMap[type] : '';
}

@Pipe({ name: 'finalDecisionType' })
export class FinalDecisionTypePipe implements PipeTransform {
  transform = transformAdminTerminationFinalDecisionType;
}
