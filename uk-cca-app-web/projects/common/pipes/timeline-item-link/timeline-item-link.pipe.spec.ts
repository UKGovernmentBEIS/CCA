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

      'RDE_ACCEPTED',
      'RDE_CANCELLED',
      'RDE_EXPIRED',

      'RFI_CANCELLED',
      'RFI_EXPIRED',

      'REQUEST_TERMINATED',

      'VERIFICATION_STATEMENT_CANCELLED',

      'SUBSISTENCE_FEES_RUN_SUBMITTED',
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
