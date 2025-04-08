import { Pipe, PipeTransform } from '@angular/core';

import { PerformanceDataSpreadsheetProcessingRequestMetadata } from 'cca-api';
import { RequestMetadata } from 'cca-api';

@Pipe({ name: 'requestTypeToHeading', pure: true, standalone: true })
export class RequestTypeToHeadingPipe implements PipeTransform {
  transform(value: string, metadata?: RequestMetadata): any {
    switch (value) {
      case 'TARGET_UNIT_ACCOUNT_CREATION':
        return 'Account creation';

      case 'TARGET_UNIT_MOA':
        return 'Subsistence fees';

      case 'UNDERLYING_AGREEMENT':
        return 'Underlying agreement';

      case 'ADMIN_TERMINATION':
        return 'Admin termination';

      case 'PERFORMANCE_DATA_SPREADSHEET_PROCESSING':
        return `Report Submission ${(metadata as PerformanceDataSpreadsheetProcessingRequestMetadata).performanceDataTargetPeriodType}`;
    }
  }
}
