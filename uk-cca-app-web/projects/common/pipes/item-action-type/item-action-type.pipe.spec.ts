import { ItemActionTypePipe } from './item-action-type.pipe';

describe('ItemActionTypePipe', () => {
  let pipe: ItemActionTypePipe;

  beforeAll(() => (pipe = new ItemActionTypePipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform action types', () => {
    expect(pipe.transform('TARGET_UNIT_ACCOUNT_CREATION_SUBMITTED')).toEqual('Target unit account submitted');

    expect(pipe.transform('UNDERLYING_AGREEMENT_APPLICATION_SUBMITTED')).toEqual(
      'Underlying agreement application submitted',
    );

    expect(pipe.transform('ADMIN_TERMINATION_APPLICATION_SUBMITTED')).toEqual('Admin termination submitted');
    expect(pipe.transform('ADMIN_TERMINATION_WITHDRAW_APPLICATION_SUBMITTED')).toEqual(
      'Admin termination withdrawn submitted',
    );
    expect(pipe.transform('ADMIN_TERMINATION_FINAL_DECISION_APPLICATION_SUBMITTED')).toEqual(
      'Admin termination final decision submitted',
    );

    expect(pipe.transform('UNDERLYING_AGREEMENT_VARIATION_APPLICATION_SUBMITTED')).toEqual(
      'Underlying agreement variation application submitted',
    );

    expect(pipe.transform(undefined)).toEqual('Approved Application');
  });
});
