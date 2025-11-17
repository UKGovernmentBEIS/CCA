import { Pipe, PipeTransform } from '@angular/core';

import { AdminTerminationReasonDetails } from 'cca-api';

export const adminTerminationReasonDetailsMap: Record<AdminTerminationReasonDetails['reason'], string> = {
  DATA_NOT_PROVIDED: 'Actual baseline data for greenfield site not provided',
  NOT_SIGN_AGREEMENT: 'Did not sign agreement',
  SITE_CLOSURE_SCHEME: 'Site closure/withdraw from scheme',
  TRANSFER_OF_OWNERSHIP: 'Transfer of ownership',
  FAILURE_TO_COMPLY: 'Failure to comply with an obligation imposed on the account holder under this agreement',
  FAILURE_TO_AGREE: 'Failure to agree a variation in a target',
  FAILURE_TO_PAY: 'Failure to pay any financial penalty imposed on the account holder by the administrator',
};

export function transformAdminTerminationReason(reason: AdminTerminationReasonDetails['reason']): string {
  return reason ? adminTerminationReasonDetailsMap[reason] : '';
}

@Pipe({ name: 'adminTerminationReason' })
export class AdminTerminationReasonPipe implements PipeTransform {
  transform = transformAdminTerminationReason;
}
