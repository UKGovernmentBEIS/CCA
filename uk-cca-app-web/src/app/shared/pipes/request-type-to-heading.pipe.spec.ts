import {
  PerformanceAccountTemplateProcessingRequestMetadata,
  PerformanceDataSpreadsheetProcessingRequestMetadata,
} from 'cca-api';

import { RequestTypeToHeadingPipe } from './request-type-to-heading.pipe';

describe('RequestTypeToHeadingPipe', () => {
  const pipe = new RequestTypeToHeadingPipe();

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return correct types to headings', () => {
    expect(pipe.transform('TARGET_UNIT_ACCOUNT_CREATION')).toEqual('Account creation');
    expect(pipe.transform('TARGET_UNIT_MOA')).toEqual('Subsistence fees');
    expect(pipe.transform('SECTOR_MOA')).toEqual('Subsistence fees');
    expect(pipe.transform('UNDERLYING_AGREEMENT')).toEqual('Underlying agreement');
    expect(pipe.transform('UNDERLYING_AGREEMENT_VARIATION', { initiatorRoleType: 'REGULATOR' } as any)).toEqual(
      'Underlying agreement variation by regulator',
    );
    expect(pipe.transform('ADMIN_TERMINATION')).toEqual('Admin termination');
    expect(pipe.transform('BUY_OUT_SURPLUS_ACCOUNT_PROCESSING')).toEqual('Buy-out and surplus');
    expect(pipe.transform('CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING')).toEqual('Migration');
    expect(pipe.transform('CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING')).toEqual('Extension');
  });

  it('should transform PERFORMANCE_DATA_SPREADSHEET_PROCESSING with metadata', () => {
    const metadata: PerformanceDataSpreadsheetProcessingRequestMetadata = {
      performanceDataTargetPeriodType: 'TP6',
    };

    const result = pipe.transform('PERFORMANCE_DATA_SPREADSHEET_PROCESSING', metadata);
    expect(result).toBe('Report Submission TP6');
  });

  it('should transform PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING with metadata', () => {
    const metadata: PerformanceAccountTemplateProcessingRequestMetadata = {
      targetPeriodType: 'TP6',
    };

    const result = pipe.transform('PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING', metadata);
    expect(result).toBe('PAT Report Submission TP6 Final');
  });
});
