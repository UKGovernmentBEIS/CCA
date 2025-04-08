import { PaymentRequestStatusTagColorPipe } from './payment-request-status-tag-color.pipe';

describe('PaymentRequestStatusTagColorPipe', () => {
  const pipe = new PaymentRequestStatusTagColorPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should display correct payment request status tag color', () => {
    expect(pipe.transform('in_progress')).toEqual('blue');
    expect(pipe.transform('completed')).toEqual('green');
    expect(pipe.transform('completed_with_failures')).toEqual('red');
  });
});
