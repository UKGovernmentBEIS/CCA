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
    expect(pipe.transform('UNDERLYING_AGREEMENT_APPLICATION_REJECTED')).toEqual(
      'Underlying agreement application rejected',
    );
    expect(pipe.transform('UNDERLYING_AGREEMENT_APPLICATION_ACCEPTED')).toEqual(
      'Underlying agreement application accepted',
    );
    expect(pipe.transform('UNDERLYING_AGREEMENT_APPLICATION_MIGRATED')).toEqual(
      'Underlying agreement application migrated',
    );
    expect(pipe.transform('UNDERLYING_AGREEMENT_APPLICATION_ACTIVATED')).toEqual(
      'Underlying agreement application activated',
    );
    expect(pipe.transform('UNDERLYING_AGREEMENT_APPLICATION_CANCELLED')).toEqual(
      'Underlying agreement application cancelled',
    );

    expect(pipe.transform('ADMIN_TERMINATION_APPLICATION_SUBMITTED')).toEqual('Admin termination submitted');
    expect(pipe.transform('ADMIN_TERMINATION_WITHDRAW_APPLICATION_SUBMITTED')).toEqual(
      'Admin termination withdrawn submitted',
    );
    expect(pipe.transform('ADMIN_TERMINATION_FINAL_DECISION_APPLICATION_SUBMITTED')).toEqual(
      'Admin termination final decision submitted',
    );
    expect(pipe.transform('ADMIN_TERMINATION_APPLICATION_CANCELLED')).toEqual('Admin termination cancelled');

    expect(pipe.transform('UNDERLYING_AGREEMENT_VARIATION_APPLICATION_SUBMITTED')).toEqual(
      'Underlying agreement variation application submitted',
    );
    expect(pipe.transform('UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REJECTED')).toEqual(
      'Underlying agreement variation application rejected',
    );
    expect(pipe.transform('UNDERLYING_AGREEMENT_VARIATION_APPLICATION_ACCEPTED')).toEqual(
      'Underlying agreement variation application accepted',
    );
    expect(pipe.transform('UNDERLYING_AGREEMENT_VARIATION_APPLICATION_ACTIVATED')).toEqual(
      'Underlying agreement variation activated',
    );
    expect(pipe.transform('UNDERLYING_AGREEMENT_VARIATION_APPLICATION_CANCELLED')).toEqual(
      'Underlying agreement variation application cancelled',
    );

    expect(pipe.transform('PERFORMANCE_DATA_SPREADSHEET_PROCESSING_SUBMITTED')).toEqual('Performance report submitted');

    expect(pipe.transform('SUBSISTENCE_FEES_RUN_SUBMITTED')).toEqual('Subsistence fees payment request run submitted');
    expect(pipe.transform('SUBSISTENCE_FEES_RUN_COMPLETED')).toEqual('Subsistence fees payment request run completed');
    expect(pipe.transform('SUBSISTENCE_FEES_RUN_COMPLETED_WITH_FAILURES')).toEqual(
      'Subsistence fees payment request run completed with failures',
    );
    expect(pipe.transform('SECTOR_MOA_GENERATED')).toEqual('Sector MoA generated');
    expect(pipe.transform('TARGET_UNIT_MOA_GENERATED')).toEqual('Subsistence fees payment request received');

    expect(pipe.transform(undefined)).toEqual('Approved Application');
  });
});
