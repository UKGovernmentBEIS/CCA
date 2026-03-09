import { RequestActionDTO } from 'cca-api';

import { ItemActionHeaderPipe } from './item-action-header.pipe';

describe('ItemActionHeaderPipe', () => {
  let pipe: ItemActionHeaderPipe;

  const baseRequestAction: Omit<RequestActionDTO, 'type'> = {
    id: 1,
    payload: {},
    submitter: 'John Bolt',
    creationDate: '2021-03-29T12:26:36.000Z',
  };

  beforeAll(() => (pipe = new ItemActionHeaderPipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should display the submitter of the request action', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'TARGET_UNIT_ACCOUNT_CREATION_SUBMITTED',
      }),
    ).toEqual('Target unit account submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'UNDERLYING_AGREEMENT_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Underlying agreement application submitted by John Bolt');
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'UNDERLYING_AGREEMENT_APPLICATION_REJECTED',
      }),
    ).toEqual('Underlying agreement application rejected by John Bolt');
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'UNDERLYING_AGREEMENT_APPLICATION_ACCEPTED',
      }),
    ).toEqual('Underlying agreement application accepted by John Bolt');
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'UNDERLYING_AGREEMENT_APPLICATION_MIGRATED',
      }),
    ).toEqual('Underlying agreement application migrated');
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'UNDERLYING_AGREEMENT_APPLICATION_ACTIVATED',
      }),
    ).toEqual('Underlying agreement application activated by John Bolt');
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'UNDERLYING_AGREEMENT_APPLICATION_CANCELLED',
      }),
    ).toEqual('Underlying agreement application cancelled by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'ADMIN_TERMINATION_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Admin termination submitted by John Bolt');
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'ADMIN_TERMINATION_WITHDRAW_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Admin termination withdrawn submitted by John Bolt');
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'ADMIN_TERMINATION_FINAL_DECISION_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Admin termination final decision submitted by John Bolt');
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'ADMIN_TERMINATION_APPLICATION_CANCELLED',
      }),
    ).toEqual('Admin termination cancelled by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Underlying agreement variation application submitted by John Bolt');
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REJECTED',
      }),
    ).toEqual('Underlying agreement variation application rejected by John Bolt');
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_ACCEPTED',
      }),
    ).toEqual('Underlying agreement variation application accepted by John Bolt');
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_ACTIVATED',
      }),
    ).toEqual('Underlying agreement variation activated by John Bolt');
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_CANCELLED',
      }),
    ).toEqual('Underlying agreement variation application cancelled by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Underlying agreement variation proposed by John Bolt');
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_COMPLETED',
      }),
    ).toEqual('Underlying agreement variation completed by John Bolt');
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_ACTIVATED',
      }),
    ).toEqual('Underlying agreement variation activated by John Bolt');
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW_REQUESTED',
      }),
    ).toEqual('Peer review requested by John Bolt');
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEWER_ACCEPTED',
      }),
    ).toEqual('Peer review agreement submitted by John Bolt');
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEWER_REJECTED',
      }),
    ).toEqual('Peer review disagreement submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PERFORMANCE_DATA_SPREADSHEET_PROCESSING_SUBMITTED',
      }),
    ).toEqual('Performance report submitted by John Bolt');
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING_SUBMITTED',
      }),
    ).toEqual('PAT report submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'SUBSISTENCE_FEES_RUN_SUBMITTED',
      }),
    ).toEqual('Subsistence fees payment request run submitted by John Bolt');
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'SUBSISTENCE_FEES_RUN_COMPLETED',
      }),
    ).toEqual('Subsistence fees payment request run completed');
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'SUBSISTENCE_FEES_RUN_COMPLETED_WITH_FAILURES',
      }),
    ).toEqual('Subsistence fees payment request run completed with failures');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'SECTOR_MOA_GENERATED',
      }),
    ).toEqual('Sector MoA generated by John Bolt');
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'TARGET_UNIT_MOA_GENERATED',
      }),
    ).toEqual('Subsistence fees payment request received');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'BUY_OUT_SURPLUS_RUN_SUBMITTED',
      }),
    ).toEqual('Buy-out and surplus batch run submitted by John Bolt');
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'BUY_OUT_SURPLUS_RUN_COMPLETED',
      }),
    ).toEqual('Buy-out and surplus batch run completed');
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'BUY_OUT_SURPLUS_RUN_COMPLETED_WITH_FAILURES',
      }),
    ).toEqual('Buy-out and surplus batch run completed with failures');
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'TP6_BUY_OUT_ACCOUNT_PROCESSING_SUBMITTED',
      }),
    ).toEqual('Buy-out fee calculated');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'TP6_SURPLUS_ACCOUNT_PROCESSING_SUBMITTED',
      }),
    ).toEqual('Surplus calculated');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_SUBMITTED',
      }),
    ).toEqual('CCA3 migration completed by System user');
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION_CANCELLED',
      }),
    ).toEqual('CCA3 agreement cancelled by John Bolt');
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATED',
      }),
    ).toEqual('CCA3 agreement activated by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING_SUBMITTED',
      }),
    ).toEqual('CCA2 agreement extended by System user');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'FACILITY_AUDIT_PRE_AUDIT_REVIEW_SUBMITTED',
      }),
    ).toEqual('Pre-audit review completed by John Bolt');
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'FACILITY_AUDIT_AUDIT_DETAILS_CORRECTIVE_ACTIONS_SUBMITTED',
      }),
    ).toEqual('Audit details and corrective actions completed by John Bolt');
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'FACILITY_AUDIT_TRACK_CORRECTIVE_ACTIONS_SUBMITTED',
      }),
    ).toEqual('Track corrective actions completed by John Bolt');
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'FACILITY_AUDIT_CANCELLED',
      }),
    ).toEqual('Audit facility cancelled by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'CCA2_TERMINATION_ACCOUNT_PROCESSING_SUBMITTED',
      }),
    ).toEqual('CCA2 Underlying agreement terminated');
  });

  it('should display the approved application title', () => {
    expect(pipe.transform({})).toEqual('Approved Application');
  });
});
