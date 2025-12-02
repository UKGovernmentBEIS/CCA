import { Pipe, PipeTransform } from '@angular/core';

import { RequestDetailsDTO } from 'cca-api';

@Pipe({ name: 'workflowType' })
export class WorkflowTypePipe implements PipeTransform {
  transform(type: RequestDetailsDTO['requestType']): string {
    switch (type) {
      case 'CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING':
        return 'CCA2 extension';

      case 'CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING':
        return 'CCA3 Migration';

      case 'TARGET_UNIT_ACCOUNT_CREATION':
        return 'Account creation';

      case 'UNDERLYING_AGREEMENT':
        return 'Underlying agreement';

      case 'UNDERLYING_AGREEMENT_VARIATION':
        return 'Underlying agreement variation';

      case 'ADMIN_TERMINATION':
        return 'Admin Termination';

      case 'TARGET_UNIT_MOA':
      case 'SECTOR_MOA':
        return 'Subsistence fees';

      case 'BUY_OUT_SURPLUS_ACCOUNT_PROCESSING':
        return 'Buy-out and surplus';

      case 'FACILITY_AUDIT':
        return 'Facility audit';

      case 'PERFORMANCE_DATA_SPREADSHEET_PROCESSING':
        return 'Performance Data';

      case 'PERFORMANCE_ACCOUNT_TEMPLATE_DATA_PROCESSING':
        return 'PAT';

      default:
        return '';
    }
  }
}
