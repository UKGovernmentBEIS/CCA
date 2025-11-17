import { RequestActionInfoDTO } from 'cca-api';

import { TimelineItemLinkPipe } from './timeline-item-link.pipe';

describe('TimelineItemLinkPipe', () => {
  let pipe: TimelineItemLinkPipe;

  const requestAction: RequestActionInfoDTO = {
    id: 1,
    submitter: 'John Bolt',
    creationDate: '2021-03-29T12:26:36.000Z',
  };

  beforeAll(() => (pipe = new TimelineItemLinkPipe()));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return empty link', () => {
    const noLinkActionTypes: RequestActionInfoDTO['type'][] = [
      'UNDERLYING_AGREEMENT_APPLICATION_CANCELLED',
      'ADMIN_TERMINATION_APPLICATION_CANCELLED',
      'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_CANCELLED',
      'ADMIN_TERMINATION_PEER_REVIEW_REQUESTED',

      'RDE_ACCEPTED',
      'RDE_CANCELLED',
      'RDE_EXPIRED',

      'RFI_CANCELLED',
      'RFI_EXPIRED',

      'REQUEST_TERMINATED',

      'VERIFICATION_STATEMENT_CANCELLED',

      'SUBSISTENCE_FEES_RUN_SUBMITTED',

      'BUY_OUT_SURPLUS_RUN_SUBMITTED',

      'CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION_CANCELLED',

      'CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING_SUBMITTED',
      'CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATED',
    ];

    noLinkActionTypes.forEach((type) => {
      requestAction.type = type;
      expect(pipe.transform(requestAction)).toBeNull();
    });
  });

  it('should return link for target unit accounts creation', () => {
    requestAction.type = 'TARGET_UNIT_ACCOUNT_CREATION_SUBMITTED';
    expect(pipe.transform(requestAction, true)).toEqual(['./timeline', requestAction.id]);
  });

  it('should return link for underlying agreement submitted', () => {
    requestAction.type = 'UNDERLYING_AGREEMENT_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction, true)).toEqual(['./timeline', requestAction.id]);

    requestAction.type = 'UNDERLYING_AGREEMENT_APPLICATION_ACCEPTED';
    expect(pipe.transform(requestAction, true)).toEqual(['./timeline', requestAction.id]);

    requestAction.type = 'UNDERLYING_AGREEMENT_APPLICATION_ACTIVATED';
    expect(pipe.transform(requestAction, true)).toEqual(['./timeline', requestAction.id]);

    requestAction.type = 'UNDERLYING_AGREEMENT_APPLICATION_REJECTED';
    expect(pipe.transform(requestAction, true)).toEqual(['./timeline', requestAction.id]);

    requestAction.type = 'UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEWER_ACCEPTED';
    expect(pipe.transform(requestAction, true)).toEqual(['./timeline', requestAction.id]);
    requestAction.type = 'UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEWER_REJECTED';
    expect(pipe.transform(requestAction, true)).toEqual(['./timeline', requestAction.id]);
  });

  it('should return link for admin termination', () => {
    requestAction.type = 'ADMIN_TERMINATION_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction, true)).toEqual(['./timeline', requestAction.id]);

    requestAction.type = 'ADMIN_TERMINATION_WITHDRAW_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction, true)).toEqual(['./timeline', requestAction.id]);

    requestAction.type = 'ADMIN_TERMINATION_FINAL_DECISION_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction, true)).toEqual(['./timeline', requestAction.id]);
  });

  it('should return link for underlying agreement variation submitted', () => {
    requestAction.type = 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction, true)).toEqual(['./timeline', requestAction.id]);

    requestAction.type = 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REJECTED';
    expect(pipe.transform(requestAction, true)).toEqual(['./timeline', requestAction.id]);

    requestAction.type = 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_ACCEPTED';
    expect(pipe.transform(requestAction, true)).toEqual(['./timeline', requestAction.id]);

    requestAction.type = 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_ACTIVATED';
    expect(pipe.transform(requestAction, true)).toEqual(['./timeline', requestAction.id]);

    requestAction.type = 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEWER_ACCEPTED';
    expect(pipe.transform(requestAction, true)).toEqual(['./timeline', requestAction.id]);

    requestAction.type = 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEWER_REJECTED';
    expect(pipe.transform(requestAction, true)).toEqual(['./timeline', requestAction.id]);
  });

  it('should return link for subsistence fees run', () => {
    requestAction.type = 'SUBSISTENCE_FEES_RUN_COMPLETED';
    expect(pipe.transform(requestAction, true)).toEqual(['./timeline', requestAction.id]);

    requestAction.type = 'SUBSISTENCE_FEES_RUN_COMPLETED_WITH_FAILURES';
    expect(pipe.transform(requestAction, true)).toEqual(['./timeline', requestAction.id]);

    requestAction.type = 'SECTOR_MOA_GENERATED';
    expect(pipe.transform(requestAction, true)).toEqual(['./timeline', requestAction.id]);

    requestAction.type = 'TARGET_UNIT_MOA_GENERATED';
    expect(pipe.transform(requestAction, true)).toEqual(['./timeline', requestAction.id]);
  });

  it('should return link for buy-out and surplus batch', () => {
    requestAction.type = 'BUY_OUT_SURPLUS_RUN_COMPLETED';
    expect(pipe.transform(requestAction, true)).toEqual(['./timeline', requestAction.id]);

    requestAction.type = 'BUY_OUT_SURPLUS_RUN_COMPLETED_WITH_FAILURES';
    expect(pipe.transform(requestAction, true)).toEqual(['./timeline', requestAction.id]);

    requestAction.type = 'TP6_BUY_OUT_ACCOUNT_PROCESSING_SUBMITTED';
    expect(pipe.transform(requestAction, true)).toEqual(['./timeline', requestAction.id]);

    requestAction.type = 'TP6_SURPLUS_ACCOUNT_PROCESSING_SUBMITTED';
    expect(pipe.transform(requestAction, true)).toEqual(['./timeline', requestAction.id]);
  });

  it('should return link for Performance data and PAT', () => {
    requestAction.type = 'PERFORMANCE_DATA_SPREADSHEET_PROCESSING_SUBMITTED';
    expect(pipe.transform(requestAction, true)).toEqual(['./timeline', requestAction.id]);

    requestAction.type = 'PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING_SUBMITTED';
    expect(pipe.transform(requestAction, true)).toEqual(['./timeline', requestAction.id]);
  });

  it('should return link for CCA3 migration', () => {
    requestAction.type = 'CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_SUBMITTED';
    expect(pipe.transform(requestAction, true)).toEqual(['./timeline', requestAction.id]);
  });

  it('should return link for payment', () => {
    requestAction.type = 'PAYMENT_MARKED_AS_PAID';
    expect(pipe.transform(requestAction)).toEqual(['/payment', 'actions', requestAction.id, 'paid']);

    requestAction.type = 'PAYMENT_CANCELLED';
    expect(pipe.transform(requestAction)).toEqual(['/payment', 'actions', requestAction.id, 'cancelled']);

    requestAction.type = 'PAYMENT_MARKED_AS_RECEIVED';
    expect(pipe.transform(requestAction)).toEqual(['/payment', 'actions', requestAction.id, 'received']);

    requestAction.type = 'PAYMENT_COMPLETED';
    expect(pipe.transform(requestAction)).toEqual(['/payment', 'actions', requestAction.id, 'completed']);
  });

  it('should return link for rfi', () => {
    requestAction.type = 'RFI_SUBMITTED';
    expect(pipe.transform(requestAction, false)).toEqual(['/rfi', 'action', requestAction.id, 'rfi-submitted']);

    requestAction.type = 'RFI_RESPONSE_SUBMITTED';
    expect(pipe.transform(requestAction, false)).toEqual([
      '/rfi',
      'action',
      requestAction.id,
      'rfi-response-submitted',
    ]);
  });

  it('should return link for rde', () => {
    requestAction.type = 'RDE_SUBMITTED';
    expect(pipe.transform(requestAction, false)).toEqual(['/rde', 'action', requestAction.id, 'rde-submitted']);

    requestAction.type = 'RDE_REJECTED';
    expect(pipe.transform(requestAction, false)).toEqual([
      '/rde',
      'action',
      requestAction.id,
      'rde-response-submitted',
    ]);

    requestAction.type = 'RDE_FORCE_ACCEPTED';
    expect(pipe.transform(requestAction, false)).toEqual([
      '/rde',
      'action',
      requestAction.id,
      'rde-manual-approval-submitted',
    ]);

    requestAction.type = 'RDE_FORCE_REJECTED';
    expect(pipe.transform(requestAction, false)).toEqual([
      '/rde',
      'action',
      requestAction.id,
      'rde-manual-approval-submitted',
    ]);
  });
});
