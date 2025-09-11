import { StatusPipe } from './status.pipe';

describe('StatusPipe', () => {
  const pipe = new StatusPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return proper values', () => {
    expect(pipe.transform('in_progress')).toEqual('In progress');
    expect(pipe.transform('in-progress')).toEqual('In progress');
    expect(pipe.transform('completed')).toEqual('Completed');
    expect(pipe.transform('completed_with_failures')).toEqual('Completed with failures');
    expect(pipe.transform('cancelled')).toEqual('Cancelled');
    expect(pipe.transform('awaiting_payment')).toEqual('Awaiting payment');
    expect(pipe.transform('awaiting_refund')).toEqual('Awaiting refund');
    expect(pipe.transform('paid')).toEqual('Paid');
    expect(pipe.transform('overpaid')).toEqual('Overpaid');
    expect(pipe.transform('approved')).toEqual('Approved');
    expect(pipe.transform('rejected')).toEqual('Rejected');
    expect(pipe.transform('accepted')).toEqual('Accepted');
    expect(pipe.transform('undecided')).toEqual('Undecided');
    expect(pipe.transform('withdrawn')).toEqual('Withdrawn');
    expect(pipe.transform('migrated')).toEqual('Migrated');
    expect(pipe.transform('refunded')).toEqual('Refunded');
    expect(pipe.transform('not_required')).toEqual('Not required');
    expect(pipe.transform('under_appeal')).toEqual('Under appeal');
    expect(pipe.transform('terminated')).toEqual('Terminated');
    expect(pipe.transform('live')).toEqual('Live');
    expect(pipe.transform('new')).toEqual('New');
    expect(pipe.transform('excluded')).toEqual('Excluded');
    expect(pipe.transform('inactive')).toEqual('Inactive');
    expect(pipe.transform('non_financially_independent')).toEqual('Non-financially independent');
    expect(pipe.transform('financially_independent')).toEqual('Financially independent');
  });
});
