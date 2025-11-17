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
    expect(pipe.transform('UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW')).toEqual(
      'Peer review application for underlying agreement',
    );
    expect(pipe.transform('UNDERLYING_AGREEMENT_WAIT_FOR_PEER_REVIEW')).toEqual(
      'Application for underlying agreement sent to peer reviewer',
    );

    expect(pipe.transform('ADMIN_TERMINATION_APPLICATION_SUBMIT')).toEqual('Admin termination');
    expect(pipe.transform('ADMIN_TERMINATION_APPLICATION_WITHDRAW')).toEqual('Withdraw admin termination');
    expect(pipe.transform('ADMIN_TERMINATION_APPLICATION_FINAL_DECISION')).toEqual('Admin termination final decision');
    expect(pipe.transform('ADMIN_TERMINATION_APPLICATION_PEER_REVIEW')).toEqual(
      'Peer review admin termination request',
    );
    expect(pipe.transform('ADMIN_TERMINATION_WAIT_FOR_PEER_REVIEW')).toEqual('Admin termination sent for peer review');

    expect(pipe.transform('UNDERLYING_AGREEMENT_VARIATION_SUBMIT')).toEqual('Apply to vary the underlying agreement');
    expect(pipe.transform('UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW')).toEqual(
      'Review underlying agreement variation',
    );

    expect(pipe.transform('UNDERLYING_AGREEMENT_VARIATION_WAIT_FOR_REVIEW')).toEqual(
      'Application for underlying agreement variation sent for review',
    );

    expect(pipe.transform('UNDERLYING_AGREEMENT_VARIATION_WAIT_FOR_ACTIVATION')).toEqual(
      'Application to vary underlying agreement sent for review',
    );

    expect(pipe.transform('UNDERLYING_AGREEMENT_VARIATION_ACTIVATION')).toEqual(
      'Upload target unit assent on variation',
    );

    expect(pipe.transform('UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEW')).toEqual(
      'Peer review application for underlying agreement variation',
    );

    expect(pipe.transform('UNDERLYING_AGREEMENT_VARIATION_WAIT_FOR_PEER_REVIEW')).toEqual(
      'Application for underlying agreement variation sent to peer reviewer',
    );

    expect(pipe.transform('PERFORMANCE_DATA_DOWNLOAD_SUBMIT')).toEqual(
      'Download target period reporting (TPR) spreadsheets',
    );
    expect(pipe.transform('PERFORMANCE_DATA_UPLOAD_SUBMIT')).toEqual(
      'Target period reporting (TPR) spreadsheets upload',
    );
    expect(pipe.transform('PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_SUBMIT')).toEqual(
      'Performance account template (PAT) upload',
    );

    expect(pipe.transform('CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION')).toEqual(
      'Upload target unit assent',
    );

    expect(pipe.transform('PRE_AUDIT_REVIEW_SUBMIT')).toEqual('Pre-audit review');

    expect(pipe.transform(null)).toBeNull();
  });
});
