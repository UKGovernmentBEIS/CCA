import { IncludesPipe } from './include.pipe';

describe('Include pipe', () => {
  const pipe = new IncludesPipe();
  it('should include NONE', () => {
    const array = ['NONE', 'EXECUTE', 'VIEW_ONLY'];
    expect(pipe.transform(array, 'NONE')).toBeTruthy();
  });
  it('should not include READ_ONLY', () => {
    const array = ['NONE', 'EXECUTE', 'VIEW_ONLY'];
    expect(pipe.transform(array, 'READ_ONLY')).toBeFalsy();
  });
});
