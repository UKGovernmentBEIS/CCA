import { AccountStatusPipe } from './account-status.pipe';

describe('AccountStatusPipe', () => {
  const pipe = new AccountStatusPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should display correct account status', () => {
    expect(pipe.transform('REJECTED')).toEqual('Rejected');
    expect(pipe.transform('LIVE')).toEqual('Live');
    expect(pipe.transform('NEW')).toEqual('New');
    expect(pipe.transform('TERMINATED')).toEqual('Terminated');
    expect(pipe.transform('CANCELLED')).toEqual('Cancelled');
  });
});
