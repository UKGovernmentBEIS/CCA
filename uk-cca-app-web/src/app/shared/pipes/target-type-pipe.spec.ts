import { TargetTypePipe } from './target-type.pipe';

describe('TargetTypePipe', () => {
  let pipe: TargetTypePipe;

  beforeEach(() => {
    pipe = new TargetTypePipe();
  });

  it('should transform "ABSOLUTE" to "Absolute"', () => {
    expect(pipe.transform('ABSOLUTE')).toBe('Absolute');
  });

  it('should transform "RELATIVE" to "Relative"', () => {
    expect(pipe.transform('RELATIVE')).toBe('Relative');
  });

  it('should transform "NOVEM_ENERGY" to "Novem energy"', () => {
    expect(pipe.transform('NOVEM_ENERGY')).toBe('Novem energy');
  });

  it('should transform "NOVEM_CARBON" to "Novem Carbon"', () => {
    expect(pipe.transform('NOVEM_CARBON')).toBe('Novem Carbon');
  });

  it('should throw an error for invalid input', () => {
    // @ts-expect-error: testing error path
    expect(() => pipe.transform('INVALID')).toThrow('invalid targetType. received INVALID');
  });
});
