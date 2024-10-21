import { ApplicationReasonTypePipe } from './application-reason-type.pipe';

describe('ApplicationReasonTypePipe', () => {
  it('create an instance', () => {
    const pipe = new ApplicationReasonTypePipe();
    expect(pipe).toBeTruthy();
  });

  it('should properly transform application reason type', () => {
    const pipe = new ApplicationReasonTypePipe();
    let transformation: string;

    transformation = pipe.transform('NEW_AGREEMENT');
    expect(transformation).toEqual('New agreement');

    transformation = pipe.transform('CHANGE_OF_OWNERSHIP');
    expect(transformation).toEqual('Change of ownership');
  });
});
