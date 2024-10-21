import { OperatorTypePipe } from '@shared/pipes';

describe('OperatorTypePipe', () => {
  const pipe = new OperatorTypePipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should map operator types to item names', () => {
    expect(pipe.transform('LIMITED_COMPANY')).toEqual('Limited company');
    expect(pipe.transform('PARTNERSHIP')).toEqual('Partnership');
    expect(pipe.transform('SOLE_TRADER')).toEqual('Sole trader');
    expect(pipe.transform('NONE')).toEqual('None');
    expect(pipe.transform(null)).toEqual('');
  });
});
