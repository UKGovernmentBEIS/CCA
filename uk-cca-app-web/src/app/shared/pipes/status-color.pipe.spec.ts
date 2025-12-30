import { StatusColorPipe } from './status-color.pipe';

describe('StatusColorPipe', () => {
  const pipe = new StatusColorPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should display correct subsistence fees run payment status tag color', () => {
    expect(pipe.transform('in_progress')).toEqual('blue');
    expect(pipe.transform('awaiting_refund')).toEqual('blue');

    expect(pipe.transform('completed')).toEqual('green');
    expect(pipe.transform('migrated')).toEqual('green');
    expect(pipe.transform('paid')).toEqual('green');
    expect(pipe.transform('approved')).toEqual('green');
    expect(pipe.transform('live')).toEqual('green');
    expect(pipe.transform('accepted')).toEqual('green');

    expect(pipe.transform('completed_with_failures')).toEqual('red');
    expect(pipe.transform('terminated')).toEqual('red');
    expect(pipe.transform('rejected')).toEqual('red');
    expect(pipe.transform('withdrawn')).toEqual('red');
    expect(pipe.transform('overpaid')).toEqual('red');

    expect(pipe.transform('refunded')).toEqual('turquoise');

    expect(pipe.transform('awaiting_payment')).toEqual('yellow');

    expect(pipe.transform('under_appeal')).toEqual('orange');

    expect(pipe.transform('not_required')).toEqual('grey');
    expect(pipe.transform('closed')).toEqual('grey');
    expect(pipe.transform('new')).toEqual('grey');

    expect(pipe.transform('cancelled')).toEqual('red');
  });
});
