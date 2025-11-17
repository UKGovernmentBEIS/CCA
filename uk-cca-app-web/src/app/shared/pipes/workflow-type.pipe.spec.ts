import { WorkflowTypePipe } from './workflow-type.pipe';

const pipe = new WorkflowTypePipe();

describe('WorkflowTypePipe', () => {
  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform "CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING" to "CCA2 extension"', () => {
    expect(pipe.transform('CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING')).toBe('CCA2 extension');
  });

  it('should transform "CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING" to "CCA3 Migration"', () => {
    expect(pipe.transform('CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING')).toBe('CCA3 Migration');
  });

  it('should transform "TARGET_UNIT_ACCOUNT_CREATION" to "Account creation"', () => {
    expect(pipe.transform('TARGET_UNIT_ACCOUNT_CREATION')).toBe('Account creation');
  });

  it('should transform "UNDERLYING_AGREEMENT" to "Underlying agreement"', () => {
    expect(pipe.transform('UNDERLYING_AGREEMENT')).toBe('Underlying agreement');
  });

  it('should transform "UNDERLYING_AGREEMENT_VARIATION" to "Underlying agreement variation"', () => {
    expect(pipe.transform('UNDERLYING_AGREEMENT_VARIATION')).toBe('Underlying agreement variation');
  });

  it('should transform "ADMIN_TERMINATION" to "Admin Termination"', () => {
    expect(pipe.transform('ADMIN_TERMINATION')).toBe('Admin Termination');
  });

  it('should transform "TARGET_UNIT_MOA" to "Subsistence fees"', () => {
    expect(pipe.transform('TARGET_UNIT_MOA')).toBe('Subsistence fees');
  });

  it('should transform "SECTOR_MOA" to "Subsistence fees"', () => {
    expect(pipe.transform('SECTOR_MOA')).toBe('Subsistence fees');
  });

  it('should transform "BUY_OUT_SURPLUS_ACCOUNT_PROCESSING" to "Buy-out and surplus"', () => {
    expect(pipe.transform('BUY_OUT_SURPLUS_ACCOUNT_PROCESSING')).toBe('Buy-out and surplus');
  });

  it('should transform "FACILITY_AUDIT" to "Facility Audit"', () => {
    expect(pipe.transform('FACILITY_AUDIT')).toBe('Facility Audit');
  });

  it('should transform "PERFORMANCE_DATA_SPREADSHEET_PROCESSING" to "Performance Data"', () => {
    expect(pipe.transform('PERFORMANCE_DATA_SPREADSHEET_PROCESSING')).toBe('Performance Data');
  });

  it('should transform "PERFORMANCE_ACCOUNT_TEMPLATE_DATA_PROCESSING" to "PAT"', () => {
    expect(pipe.transform('PERFORMANCE_ACCOUNT_TEMPLATE_DATA_PROCESSING')).toBe('PAT');
  });
});
