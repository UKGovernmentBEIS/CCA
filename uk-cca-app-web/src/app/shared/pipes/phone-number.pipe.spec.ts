import { PhoneNumberPipe } from './phone-number.pipe';

describe('PhoneNumberPipe', () => {
  const pipe = new PhoneNumberPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return the country code and calling code', () => {
    expect(pipe.transform('30')).toEqual('GR (30)');
  });

  it('should return invalid country if country is not found', () => {
    expect(pipe.transform('12')).toEqual('ZZ (12)');
  });
});
