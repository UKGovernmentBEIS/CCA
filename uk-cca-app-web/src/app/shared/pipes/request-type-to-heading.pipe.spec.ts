import { PerformanceDataSpreadsheetProcessingRequestMetadata } from 'cca-api';

import { RequestTypeToHeadingPipe } from './request-type-to-heading.pipe';

describe('RequestTypeToHeadingPipe', () => {
  const pipe = new RequestTypeToHeadingPipe();

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return correct types to headings', () => {
    expect(pipe.transform('TARGET_UNIT_ACCOUNT_CREATION')).toEqual('Account creation');
    expect(pipe.transform('TARGET_UNIT_MOA')).toEqual('Subsistence fees');
    expect(pipe.transform('UNDERLYING_AGREEMENT')).toEqual('Underlying agreement');
    expect(pipe.transform('ADMIN_TERMINATION')).toEqual('Admin termination');
  });

  it('should transform PERFORMANCE_DATA_SPREADSHEET_PROCESSING with metadata', () => {
    const metadata: PerformanceDataSpreadsheetProcessingRequestMetadata = {
      performanceDataTargetPeriodType: 'TP6',
    };

    const result = pipe.transform('PERFORMANCE_DATA_SPREADSHEET_PROCESSING', metadata);
    expect(result).toBe('Report Submission TP6');
  });
});
