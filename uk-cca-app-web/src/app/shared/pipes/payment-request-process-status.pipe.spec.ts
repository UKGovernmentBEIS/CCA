import { PaymentRequestProcessStatusPipe } from './payment-request-process-status.pipe';

describe('PaymentRequestProcessStatusPipe', () => {
  const pipe = new PaymentRequestProcessStatusPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should display correct payment process status', () => {
    expect(pipe.transform('in_progress')).toEqual('In progress');
    expect(pipe.transform('completed')).toEqual('Completed');
    expect(pipe.transform('completed_with_failures')).toEqual('Completed with failures');
  });
});
