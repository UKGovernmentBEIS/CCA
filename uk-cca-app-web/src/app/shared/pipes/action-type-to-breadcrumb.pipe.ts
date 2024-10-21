import { Pipe, PipeTransform } from '@angular/core';

import { RequestActionDTO } from 'cca-api';

@Pipe({
  name: 'actionTypeToBreadcrumb',
  standalone: true,
  pure: true,
})
export class ActionTypeToBreadcrumbPipe implements PipeTransform {
  transform(requestAction: RequestActionDTO): string | null {
    switch (requestAction?.type) {
      case 'TARGET_UNIT_ACCOUNT_CREATION_SUBMITTED':
      case 'ADMIN_TERMINATION_APPLICATION_SUBMITTED':
      case 'ADMIN_TERMINATION_WITHDRAW_APPLICATION_SUBMITTED':
      case 'ADMIN_TERMINATION_FINAL_DECISION_APPLICATION_SUBMITTED':
      case 'UNDERLYING_AGREEMENT_APPLICATION_SUBMITTED':
      case 'UNDERLYING_AGREEMENT_APPLICATION_ACCEPTED':
      case 'UNDERLYING_AGREEMENT_APPLICATION_REJECTED':
      case 'UNDERLYING_AGREEMENT_APPLICATION_ACTIVATED':
      case 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_SUBMITTED':
        return `submitted by ${requestAction.submitter}`;

      default:
        return null;
    }
  }
}
