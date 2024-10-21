import { ActionTypeToBreadcrumbPipe } from './action-type-to-breadcrumb.pipe';

describe('ActionTypeToBreadcrumbPipe', () => {
  const pipe = new ActionTypeToBreadcrumbPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should display correct breadcrumb per type', () => {
    expect(pipe.transform(null)).toBeNull();

    expect(
      pipe.transform({
        submitter: 'John Bolt',
        type: 'TARGET_UNIT_ACCOUNT_CREATION_SUBMITTED',
      }),
    ).toEqual('submitted by John Bolt');

    expect(
      pipe.transform({
        submitter: 'John Bolt',
        type: 'ADMIN_TERMINATION_APPLICATION_SUBMITTED',
      }),
    ).toEqual('submitted by John Bolt');

    expect(
      pipe.transform({
        submitter: 'John Bolt',
        type: 'ADMIN_TERMINATION_WITHDRAW_APPLICATION_SUBMITTED',
      }),
    ).toEqual('submitted by John Bolt');

    expect(
      pipe.transform({
        submitter: 'John Bolt',
        type: 'ADMIN_TERMINATION_FINAL_DECISION_APPLICATION_SUBMITTED',
      }),
    ).toEqual('submitted by John Bolt');

    expect(
      pipe.transform({
        submitter: 'John Bolt',
        type: 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_SUBMITTED',
      }),
    ).toEqual('submitted by John Bolt');
  });
});
