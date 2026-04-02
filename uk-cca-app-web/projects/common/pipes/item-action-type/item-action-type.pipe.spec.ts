import { ItemActionTypePipe } from './item-action-type.pipe';

describe('ItemActionTypePipe', () => {
  const pipe = new ItemActionTypePipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform action types', () => {
    expect(pipe.transform('TARGET_UNIT_ACCOUNT_CREATION_SUBMITTED')).toEqual('Target unit account submitted');

    expect(pipe.transform('UNDERLYING_AGREEMENT_APPLICATION_SUBMITTED')).toEqual(
      'Underlying agreement application submitted',
    );
    expect(pipe.transform('UNDERLYING_AGREEMENT_APPLICATION_REJECTED')).toEqual(
      'Underlying agreement application rejected',
    );
    expect(pipe.transform('UNDERLYING_AGREEMENT_APPLICATION_ACCEPTED')).toEqual(
      'Underlying agreement application accepted',
    );
    expect(pipe.transform('UNDERLYING_AGREEMENT_APPLICATION_MIGRATED')).toEqual(
      'Underlying agreement application migrated',
    );
    expect(pipe.transform('UNDERLYING_AGREEMENT_APPLICATION_ACTIVATED')).toEqual(
      'Underlying agreement application activated',
    );
    expect(pipe.transform('UNDERLYING_AGREEMENT_APPLICATION_CANCELLED')).toEqual(
      'Underlying agreement application cancelled',
    );

    expect(pipe.transform('UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_SUBMITTED')).toEqual(
      'Underlying agreement variation proposed',
    );
    expect(pipe.transform('UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_COMPLETED')).toEqual(
      'Underlying agreement variation completed',
    );
    expect(pipe.transform('UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_ACTIVATED')).toEqual(
      'Underlying agreement variation activated',
    );
    expect(pipe.transform('UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW_REQUESTED')).toEqual(
      'Peer review requested',
    );
    expect(pipe.transform('UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEWER_ACCEPTED')).toEqual(
      'Peer review agreement submitted',
    );
    expect(pipe.transform('UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEWER_REJECTED')).toEqual(
      'Peer review disagreement submitted',
    );

    expect(pipe.transform('ADMIN_TERMINATION_APPLICATION_SUBMITTED')).toEqual('Admin termination submitted');
    expect(pipe.transform('ADMIN_TERMINATION_WITHDRAW_APPLICATION_SUBMITTED')).toEqual(
      'Admin termination withdrawn submitted',
    );
    expect(pipe.transform('ADMIN_TERMINATION_FINAL_DECISION_APPLICATION_SUBMITTED')).toEqual(
      'Admin termination final decision submitted',
    );
    expect(pipe.transform('ADMIN_TERMINATION_APPLICATION_CANCELLED')).toEqual('Admin termination cancelled');
    expect(pipe.transform('ADMIN_TERMINATION_PEER_REVIEW_REQUESTED')).toEqual('Peer review requested');
    expect(pipe.transform('ADMIN_TERMINATION_APPLICATION_PEER_REVIEWER_ACCEPTED')).toEqual('Peer review agreement');
    expect(pipe.transform('ADMIN_TERMINATION_APPLICATION_PEER_REVIEWER_REJECTED')).toEqual('Peer review disagreement');

    expect(pipe.transform('UNDERLYING_AGREEMENT_VARIATION_APPLICATION_SUBMITTED')).toEqual(
      'Underlying agreement variation application submitted',
    );
    expect(pipe.transform('UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REJECTED')).toEqual(
      'Underlying agreement variation application rejected',
    );
    expect(pipe.transform('UNDERLYING_AGREEMENT_VARIATION_APPLICATION_ACCEPTED')).toEqual(
      'Underlying agreement variation application accepted',
    );
    expect(pipe.transform('UNDERLYING_AGREEMENT_VARIATION_APPLICATION_ACTIVATED')).toEqual(
      'Underlying agreement variation activated',
    );
    expect(pipe.transform('UNDERLYING_AGREEMENT_VARIATION_APPLICATION_CANCELLED')).toEqual(
      'Underlying agreement variation application cancelled',
    );
    expect(pipe.transform('UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW_REQUESTED')).toEqual('Peer review requested');
    expect(pipe.transform('UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEWER_ACCEPTED')).toEqual('Peer review agreement');
    expect(pipe.transform('UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEWER_REJECTED')).toEqual(
      'Peer review disagreement',
    );

    expect(pipe.transform('UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEW_REQUESTED')).toEqual(
      'Peer review requested',
    );
    expect(pipe.transform('UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEWER_ACCEPTED')).toEqual(
      'Peer review agreement',
    );
    expect(pipe.transform('UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEWER_REJECTED')).toEqual(
      'Peer review disagreement',
    );

    expect(pipe.transform('PERFORMANCE_DATA_SPREADSHEET_PROCESSING_SUBMITTED')).toEqual('Performance report submitted');
    expect(pipe.transform('PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING_SUBMITTED')).toEqual('PAT report submitted');

    expect(pipe.transform('SUBSISTENCE_FEES_RUN_SUBMITTED')).toEqual('Subsistence fees payment request run submitted');
    expect(pipe.transform('SUBSISTENCE_FEES_RUN_COMPLETED')).toEqual('Subsistence fees payment request run completed');
    expect(pipe.transform('SUBSISTENCE_FEES_RUN_COMPLETED_WITH_FAILURES')).toEqual(
      'Subsistence fees payment request run completed with failures',
    );
    expect(pipe.transform('SECTOR_MOA_GENERATED')).toEqual('Sector MoA generated');
    expect(pipe.transform('TARGET_UNIT_MOA_GENERATED')).toEqual('Subsistence fees payment request received');

    expect(pipe.transform('BUY_OUT_SURPLUS_RUN_SUBMITTED')).toEqual('Buy-out and surplus batch run submitted');
    expect(pipe.transform('BUY_OUT_SURPLUS_RUN_COMPLETED')).toEqual('Buy-out and surplus batch run completed');
    expect(pipe.transform('BUY_OUT_SURPLUS_RUN_COMPLETED_WITH_FAILURES')).toEqual(
      'Buy-out and surplus batch run completed with failures',
    );

    expect(pipe.transform('CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION_CANCELLED')).toEqual(
      'CCA3 agreement cancelled',
    );
    expect(pipe.transform('CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATED')).toEqual(
      'CCA3 agreement activated',
    );

    expect(pipe.transform('FACILITY_AUDIT_PRE_AUDIT_REVIEW_SUBMITTED')).toEqual('Pre-audit review completed');
    expect(pipe.transform('FACILITY_AUDIT_AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMITTED')).toEqual(
      'Audit details and corrective actions completed',
    );
    expect(pipe.transform('FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_SUBMITTED')).toEqual(
      'Track corrective actions completed',
    );
    expect(pipe.transform('FACILITY_AUDIT_CANCELLED')).toEqual('Audit facility cancelled');

    expect(pipe.transform('REQUEST_TERMINATED')).toEqual('Workflow terminated by the system');

    expect(pipe.transform('CCA2_TERMINATION_ACCOUNT_PROCESSING_SUBMITTED_UNDERLYING_AGREEMENT_TERMINATED')).toEqual(
      'CCA2 Underlying agreement terminated',
    );

    expect(pipe.transform(undefined)).toEqual('Approved Application');
  });
});
