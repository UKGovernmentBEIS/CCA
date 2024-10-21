import { FacilityStatusPipe } from './facility-status.pipe';

describe('FacilityStatusPipe', () => {
  it('create an instance', () => {
    const pipe = new FacilityStatusPipe();
    expect(pipe).toBeTruthy();
  });

  it('should properly transform contact type', () => {
    const pipe = new FacilityStatusPipe();
    let transformation: string;

    transformation = pipe.transform('NEW');
    expect(transformation).toEqual('New');

    transformation = pipe.transform('LIVE');
    expect(transformation).toEqual('Live');

    transformation = pipe.transform('EXCLUDED');
    expect(transformation).toEqual('Excluded');
  });
});
