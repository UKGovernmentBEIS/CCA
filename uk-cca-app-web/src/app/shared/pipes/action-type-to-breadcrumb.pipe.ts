import { Pipe, PipeTransform } from '@angular/core';

import { RequestActionDTO } from 'cca-api';

@Pipe({ name: 'actionTypeToBreadcrumb', pure: true })
export class ActionTypeToBreadcrumbPipe implements PipeTransform {
  transform(requestAction: RequestActionDTO): string | null {
    switch (requestAction?.type) {
      case 'TARGET_UNIT_ACCOUNT_CREATION_SUBMITTED':
      case 'ADMIN_TERMINATION_APPLICATION_SUBMITTED':
      case 'ADMIN_TERMINATION_WITHDRAW_APPLICATION_SUBMITTED':
      case 'ADMIN_TERMINATION_FINAL_DECISION_APPLICATION_SUBMITTED':
      case 'UNDERLYING_AGREEMENT_APPLICATION_SUBMITTED':
      case 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_SUBMITTED':
      case 'SUBSISTENCE_FEES_RUN_SUBMITTED':
        return `submitted by ${requestAction.submitter}`;

      case 'UNDERLYING_AGREEMENT_APPLICATION_ACCEPTED':
      case 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_ACCEPTED':
        return `accepted by ${requestAction.submitter}`;

      case 'UNDERLYING_AGREEMENT_APPLICATION_REJECTED':
      case 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REJECTED':
        return `rejected by ${requestAction.submitter}`;

      case 'UNDERLYING_AGREEMENT_APPLICATION_ACTIVATED':
      case 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_ACTIVATED':
        return `activated by ${requestAction.submitter}`;

      case 'UNDERLYING_AGREEMENT_APPLICATION_MIGRATED':
        return 'migrated';

      case 'PERFORMANCE_DATA_SPREADSHEET_PROCESSING_SUBMITTED':
        return 'Report submission';

      case 'SUBSISTENCE_FEES_RUN_COMPLETED':
        return 'Subsistence fees payment request run completed';
      case 'SUBSISTENCE_FEES_RUN_COMPLETED_WITH_FAILURES':
        return 'Subsistence fees payment request run completed with failures';
      case 'SECTOR_MOA_GENERATED':
        return 'Sector MoA generated';
      case 'TARGET_UNIT_MOA_GENERATED':
        return 'Target Unit MoA generated';

      default:
        return null;
    }
  }
}
