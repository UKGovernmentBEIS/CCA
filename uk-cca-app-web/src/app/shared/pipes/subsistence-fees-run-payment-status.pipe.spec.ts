import { SubsistenceFeesRunPaymentStatusPipe } from './subsistence-fees-run-payment-status.pipe';

describe('SubsistenceFeesRunPaymentStatusPipe', () => {
  const pipe = new SubsistenceFeesRunPaymentStatusPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should display correct subsistence fees run payment status', () => {
    expect(pipe.transform('awaiting_payment')).toEqual('Awaiting payment');
    expect(pipe.transform('paid')).toEqual('Paid');
    expect(pipe.transform('overpaid')).toEqual('Overpaid');
    expect(pipe.transform('cancelled')).toEqual('Cancelled');
  });
});
