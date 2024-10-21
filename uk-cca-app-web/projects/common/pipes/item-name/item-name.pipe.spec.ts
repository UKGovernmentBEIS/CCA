import { ItemNamePipe } from './item-name.pipe';

describe('ItemNamePipe', () => {
  const pipe = new ItemNamePipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should map task types to item names', () => {
    expect(pipe.transform('UNDERLYING_AGREEMENT_APPLICATION_SUBMIT')).toEqual('Apply for underlying agreement');
    expect(pipe.transform('UNDERLYING_AGREEMENT_APPLICATION_REVIEW')).toEqual(
      'Review application for underlying agreement',
    );
    expect(pipe.transform('UNDERLYING_AGREEMENT_WAIT_FOR_REVIEW')).toEqual(
      'Application for underlying agreement sent for review',
    );
    expect(pipe.transform('UNDERLYING_AGREEMENT_APPLICATION_ACTIVATION')).toEqual('Upload target unit assent');
    expect(pipe.transform('UNDERLYING_AGREEMENT_WAIT_FOR_ACTIVATION')).toEqual(
      `Application for underlying agreement awaiting operator's assent/activation`,
    );
    expect(pipe.transform('ADMIN_TERMINATION_APPLICATION_SUBMIT')).toEqual('Admin termination');
    expect(pipe.transform('ADMIN_TERMINATION_APPLICATION_WITHDRAW')).toEqual('Withdraw admin termination');
    expect(pipe.transform('ADMIN_TERMINATION_APPLICATION_FINAL_DECISION')).toEqual('Admin termination final decision');

    expect(pipe.transform('UNDERLYING_AGREEMENT_VARIATION_SUBMIT')).toEqual('Apply to vary the underlying agreement');

    expect(pipe.transform('UNDERLYING_AGREEMENT_VARIATION_WAIT_FOR_REVIEW')).toEqual(
      'Application for underlying agreement variation sent for review',
    );

    expect(pipe.transform('UNDERLYING_AGREEMENT_VARIATION_WAIT_FOR_ACTIVATION')).toEqual(
      'Application to vary underlying agreement sent for review',
    );

    expect(pipe.transform(null)).toBeNull();
  });
});
