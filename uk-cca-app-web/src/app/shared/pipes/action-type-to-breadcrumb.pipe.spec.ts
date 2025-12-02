import { ActionTypeToBreadcrumbPipe } from './action-type-to-breadcrumb.pipe';

describe('ActionTypeToBreadcrumbPipe', () => {
  const pipe = new ActionTypeToBreadcrumbPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should display correct breadcrumb per type', () => {
    expect(pipe.transform(null)).toBeNull();

    // Submitted
    expect(
      pipe.transform({
        submitter: 'John Bolt',
        type: 'TARGET_UNIT_ACCOUNT_CREATION_SUBMITTED',
      }),
    ).toEqual('submitted by John Bolt');

    expect(
      pipe.transform({
        submitter: 'John Bolt',
        type: 'ADMIN_TERMINATION_APPLICATION_SUBMITTED',
      }),
    ).toEqual('submitted by John Bolt');

    expect(
      pipe.transform({
        submitter: 'John Bolt',
        type: 'ADMIN_TERMINATION_WITHDRAW_APPLICATION_SUBMITTED',
      }),
    ).toEqual('submitted by John Bolt');

    expect(
      pipe.transform({
        submitter: 'John Bolt',
        type: 'ADMIN_TERMINATION_FINAL_DECISION_APPLICATION_SUBMITTED',
      }),
    ).toEqual('submitted by John Bolt');

    expect(
      pipe.transform({
        submitter: 'John Bolt',
        type: 'UNDERLYING_AGREEMENT_APPLICATION_SUBMITTED',
      }),
    ).toEqual('submitted by John Bolt');

    expect(
      pipe.transform({
        submitter: 'John Bolt',
        type: 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_SUBMITTED',
      }),
    ).toEqual('submitted by John Bolt');

    expect(
      pipe.transform({
        submitter: 'John Bolt',
        type: 'SUBSISTENCE_FEES_RUN_SUBMITTED',
      }),
    ).toEqual('submitted by John Bolt');

    expect(
      pipe.transform({
        submitter: 'John Bolt',
        type: 'FACILITY_AUDIT_PRE_AUDIT_REVIEW_SUBMITTED',
      }),
    ).toEqual('submitted by John Bolt');

    expect(
      pipe.transform({
        submitter: 'John Bolt',
        type: 'FACILITY_AUDIT_AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMITTED',
      }),
    ).toEqual('submitted by John Bolt');

    expect(
      pipe.transform({
        submitter: 'John Bolt',
        type: 'FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_SUBMITTED',
      }),
    ).toEqual('submitted by John Bolt');

    // Accepted
    expect(
      pipe.transform({
        submitter: 'John Bolt',
        type: 'UNDERLYING_AGREEMENT_APPLICATION_ACCEPTED',
      }),
    ).toEqual('accepted by John Bolt');

    expect(
      pipe.transform({
        submitter: 'John Bolt',
        type: 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_ACCEPTED',
      }),
    ).toEqual('accepted by John Bolt');

    // Rejected
    expect(
      pipe.transform({
        submitter: 'John Bolt',
        type: 'UNDERLYING_AGREEMENT_APPLICATION_REJECTED',
      }),
    ).toEqual('rejected by John Bolt');

    expect(
      pipe.transform({
        submitter: 'John Bolt',
        type: 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REJECTED',
      }),
    ).toEqual('rejected by John Bolt');

    // Activated
    expect(
      pipe.transform({
        submitter: 'John Bolt',
        type: 'UNDERLYING_AGREEMENT_APPLICATION_ACTIVATED',
      }),
    ).toEqual('activated by John Bolt');

    expect(
      pipe.transform({
        submitter: 'John Bolt',
        type: 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_ACTIVATED',
      }),
    ).toEqual('activated by John Bolt');

    // Migrated
    expect(
      pipe.transform({
        submitter: 'John Bolt',
        type: 'UNDERLYING_AGREEMENT_APPLICATION_MIGRATED',
      }),
    ).toEqual('migrated');

    expect(
      pipe.transform({
        submitter: 'John Bolt',
        type: 'SUBSISTENCE_FEES_RUN_COMPLETED',
      }),
    ).toEqual('Subsistence fees payment request run completed');

    expect(
      pipe.transform({
        submitter: 'John Bolt',
        type: 'SUBSISTENCE_FEES_RUN_COMPLETED_WITH_FAILURES',
      }),
    ).toEqual('Subsistence fees payment request run completed with failures');

    expect(
      pipe.transform({
        submitter: 'John Bolt',
        type: 'SECTOR_MOA_GENERATED',
      }),
    ).toEqual('Sector MoA generated');

    expect(
      pipe.transform({
        submitter: 'John Bolt',
        type: 'TARGET_UNIT_MOA_GENERATED',
      }),
    ).toEqual('Target Unit MoA generated');

    expect(
      pipe.transform({
        submitter: 'John Bolt',
        type: 'PERFORMANCE_DATA_SPREADSHEET_PROCESSING_SUBMITTED',
      }),
    ).toEqual('Report submission');
  });
});
