import { TargetPeriodOutcomePipe } from './target-period-outcome.pipe';

describe('TargetPeriodOutcomePipe', () => {
  it('create an instance', () => {
    const pipe = new TargetPeriodOutcomePipe();
    expect(pipe).toBeTruthy();
  });

  it('should properly transform the target period outcome', () => {
    const pipe = new TargetPeriodOutcomePipe();
    let transformation: string;

    transformation = pipe.transform('TARGET_MET');
    expect(transformation).toEqual('Target met');

    transformation = pipe.transform('BUY_OUT_REQUIRED');
    expect(transformation).toEqual('Buy-out required');

    transformation = pipe.transform('SURPLUS_USED_BUY_OUT_REQUIRED');
    expect(transformation).toEqual('Surplus used buy-out required');

    transformation = pipe.transform('SURPLUS_USED');
    expect(transformation).toEqual('Surplus used');
  });
});
