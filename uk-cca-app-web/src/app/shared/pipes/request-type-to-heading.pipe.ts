import { Pipe, PipeTransform } from '@angular/core';

import {
  PerformanceAccountTemplateProcessingRequestMetadata,
  PerformanceDataSpreadsheetProcessingRequestMetadata,
} from 'cca-api';
import { RequestMetadata } from 'cca-api';

@Pipe({ name: 'requestTypeToHeading', pure: true })
export class RequestTypeToHeadingPipe implements PipeTransform {
  transform(value: string, metadata?: RequestMetadata): string {
    switch (value) {
      case 'TARGET_UNIT_ACCOUNT_CREATION':
        return 'Account creation';

      case 'TARGET_UNIT_MOA':
      case 'SECTOR_MOA':
        return 'Subsistence fees';

      case 'UNDERLYING_AGREEMENT':
        return 'Underlying agreement';

      case 'UNDERLYING_AGREEMENT_VARIATION':
        return `Underlying agreement variation${metadata?.['initiatorRoleType'] === 'REGULATOR' ? ' by regulator' : ''}`;

      case 'ADMIN_TERMINATION':
        return 'Admin termination';

      case 'BUY_OUT_SURPLUS_ACCOUNT_PROCESSING':
        return 'Buy-out and surplus';

      case 'PERFORMANCE_DATA_SPREADSHEET_PROCESSING':
        return `Report Submission ${(metadata as PerformanceDataSpreadsheetProcessingRequestMetadata).performanceDataTargetPeriodType}`;

      case 'PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING':
        return `PAT Report Submission ${(metadata as PerformanceAccountTemplateProcessingRequestMetadata).targetPeriodType} Final`;

      case 'CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING':
        return 'Migration';

      case 'CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING':
        return 'Extension';

      case 'CCA2_TERMINATION_ACCOUNT_PROCESSING':
        return 'CCA2 end';

      case 'NON_COMPLIANCE':
        return 'Non-Compliance';
    }
  }
}
