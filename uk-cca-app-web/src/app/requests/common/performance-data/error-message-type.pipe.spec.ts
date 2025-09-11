import { ErrorMessageTypePipe } from './error-message-type.pipe';

describe('ErrorMessageTypePipe', () => {
  it('create an instance', () => {
    const pipe = new ErrorMessageTypePipe();
    expect(pipe).toBeTruthy();
  });

  it('should properly transform error message type', () => {
    const pipe = new ErrorMessageTypePipe();
    let transformation: string;

    transformation = pipe.transform('GENERATE_ZIP_FAILED');
    expect(transformation).toEqual('Failed to generate main zip file');

    transformation = pipe.transform('GENERATE_CSV_FAILED');
    expect(transformation).toEqual('Failed to generate error csv file');

    transformation = pipe.transform('NO_ELIGIBLE_ACCOUNTS_FOR_TPR_REPORTING');
    expect(transformation).toEqual('No eligible accounts found in this sector for the selected target period.');
  });
});
