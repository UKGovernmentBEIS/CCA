import { SubsistenceFeesRunPaymentStatusTagColorPipe } from './subsistence-fees-run-payment-status-tag-color.pipe';

describe('SubsistenceFeesRunPaymentStatusTagColorPipe', () => {
  const pipe = new SubsistenceFeesRunPaymentStatusTagColorPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should display correct subsistence fees run payment status tag color', () => {
    expect(pipe.transform('awaiting_payment')).toEqual('yellow');
    expect(pipe.transform('paid')).toEqual('green');
    expect(pipe.transform('overpaid')).toEqual('red');
    expect(pipe.transform('cancelled')).toEqual('grey');
  });
});
