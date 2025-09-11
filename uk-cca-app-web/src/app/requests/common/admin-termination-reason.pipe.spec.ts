import { AdminTerminationReasonPipe } from './admin-termination-reason.pipe';

describe('AdminTerminationReasonPipe', () => {
  const pipe = new AdminTerminationReasonPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should map termination reason types to item names', () => {
    expect(pipe.transform('DATA_NOT_PROVIDED')).toEqual('Actual baseline data for greenfield site not provided');
    expect(pipe.transform('NOT_SIGN_AGREEMENT')).toEqual('Did not sign agreement');
    expect(pipe.transform('SITE_CLOSURE_SCHEME')).toEqual('Site closure/withdraw from scheme');
    expect(pipe.transform('TRANSFER_OF_OWNERSHIP')).toEqual('Transfer of ownership');

    expect(pipe.transform('FAILURE_TO_COMPLY')).toEqual(
      'Failure to comply with an obligation imposed on the account holder under this agreement',
    );

    expect(pipe.transform('FAILURE_TO_AGREE')).toEqual('Failure to agree a variation in a target');

    expect(pipe.transform('FAILURE_TO_PAY')).toEqual(
      'Failure to pay any financial penalty imposed on the account holder by the administrator',
    );

    expect(pipe.transform(null)).toEqual('');
  });
});
