import { FinancialIndependenceStatusPipe } from '@shared/pipes';

describe('FinancialIndependenceStatusPipe', () => {
  const pipe = new FinancialIndependenceStatusPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should map task types to item names', () => {
    expect(pipe.transform('NON_FINANCIALLY_INDEPENDENT')).toEqual('Non-financially independent');
    expect(pipe.transform('FINANCIALLY_INDEPENDENT')).toEqual('Financially independent');
    expect(pipe.transform(null)).toEqual('');
  });
});
