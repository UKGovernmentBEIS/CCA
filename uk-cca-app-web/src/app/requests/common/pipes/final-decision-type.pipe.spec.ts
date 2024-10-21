import { FinalDecisionTypePipe } from './final-decision-type.pipe';

describe('FinalDecisionTypePipe', () => {
  const pipe = new FinalDecisionTypePipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should map final decision type to name', () => {
    expect(pipe.transform('TERMINATE_AGREEMENT')).toEqual('Terminate agreement');
    expect(pipe.transform('WITHDRAW_TERMINATION')).toEqual('Withdraw termination');
    expect(pipe.transform(null)).toEqual('');
  });
});
