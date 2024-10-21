import { RequestTypeToHeadingPipe } from './request-type-to-heading.pipe';

describe('RequestTypeToHeadingPipe', () => {
  const pipe = new RequestTypeToHeadingPipe();

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return correct types to headings', () => {
    expect(pipe.transform('TARGET_UNIT_ACCOUNT_CREATION')).toEqual('Account creation');
    expect(pipe.transform('UNDERLYING_AGREEMENT')).toEqual('Underlying agreement');
    expect(pipe.transform('ADMIN_TERMINATION')).toEqual('Admin termination');
  });
});
