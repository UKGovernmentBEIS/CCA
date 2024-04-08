import { ItemActionTypePipe } from './item-action-type.pipe';

describe('ItemActionTypePipe', () => {
  let pipe: ItemActionTypePipe;

  beforeAll(() => (pipe = new ItemActionTypePipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform action types', () => {
    expect(pipe.transform('PAYMENT_MARKED_AS_PAID')).toEqual('Payment marked as paid (BACS)');
    expect(pipe.transform('PAYMENT_CANCELLED')).toEqual('Payment task cancelled');
    expect(pipe.transform('PAYMENT_MARKED_AS_RECEIVED')).toEqual('Payment marked as received');
    expect(pipe.transform('PAYMENT_COMPLETED')).toEqual('Payment confirmed via GOV.UK pay');

    expect(pipe.transform('RDE_ACCEPTED')).toEqual('Deadline extension date accepted');
    expect(pipe.transform('RDE_CANCELLED')).toEqual('Deadline extension date rejected');
    expect(pipe.transform('RDE_REJECTED')).toEqual('Deadline extension date rejected');
    expect(pipe.transform('RDE_FORCE_REJECTED')).toEqual('Deadline extension date rejected');
    expect(pipe.transform('RDE_EXPIRED')).toEqual('Deadline extension date expired');
    expect(pipe.transform('RDE_FORCE_ACCEPTED')).toEqual('Deadline extension date approved');
    expect(pipe.transform('RDE_SUBMITTED')).toEqual('Deadline extension date requested');

    expect(pipe.transform('RFI_CANCELLED')).toEqual('Request for information withdrawn');
    expect(pipe.transform('RFI_EXPIRED')).toEqual('Request for information expired');
    expect(pipe.transform('RFI_RESPONSE_SUBMITTED')).toEqual('Request for information responded');
    expect(pipe.transform('RFI_SUBMITTED')).toEqual('Request for information sent');
    expect(pipe.transform('REQUEST_TERMINATED')).toEqual('Workflow terminated by the system');

    expect(pipe.transform(undefined)).toEqual('Approved Application');
  });
});
