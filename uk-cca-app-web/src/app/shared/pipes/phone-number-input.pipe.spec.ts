import { PhoneNumberInputPipe } from '@shared/pipes';

describe('PhoneNumberPipe', () => {
  let pipe: PhoneNumberInputPipe;

  beforeEach(() => {
    pipe = new PhoneNumberInputPipe();
  });

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should format the phone number correctly when both countryCode and number are provided', () => {
    const phoneNumberDTO = { countryCode: '30', number: '6999999999' };
    expect(pipe.transform(phoneNumberDTO)).toBe('(+30)6999999999');
  });

  it('should handle missing countryCode by displaying an empty country code', () => {
    const phoneNumberDTO = { number: '6999999999' };
    expect(pipe.transform(phoneNumberDTO)).toBe('(+)6999999999');
  });

  it('should handle missing number by displaying an empty number', () => {
    const phoneNumberDTO = { countryCode: '30' };
    expect(pipe.transform(phoneNumberDTO)).toBe('(+30)');
  });

  it('should handle undefined input gracefully', () => {
    expect(pipe.transform(undefined)).toBe('');
  });

  it('should handle null input gracefully', () => {
    expect(pipe.transform(null)).toBe('');
  });

  it('should handle empty string for countryCode and number gracefully', () => {
    const phoneNumberDTO = { countryCode: '', number: '' };
    expect(pipe.transform(phoneNumberDTO)).toBe('');
  });
});
