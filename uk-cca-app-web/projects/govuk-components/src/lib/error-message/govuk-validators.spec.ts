import { FormControl } from '@angular/forms';

import { GovukValidators } from './govuk-validators';

describe('GovukValidators', () => {
  it('should create an instance', () => {
    expect(new GovukValidators()).toBeTruthy();
  });

  it('should build a validator with custom message', () => {
    const validator = GovukValidators.builder('This is an error', () => ({ custom: true }));

    expect(validator(new FormControl())).toEqual({ custom: 'This is an error' });
  });

  it('should override angular validators', () => {
    const errorMessage = 'This is an error';

    expect(GovukValidators.required(errorMessage)(new FormControl())).toEqual({ required: errorMessage });
    expect(GovukValidators.requiredTrue(errorMessage)(new FormControl())).toEqual({ required: errorMessage });
    expect(GovukValidators.min(5, errorMessage)(new FormControl(3))).toEqual({ min: errorMessage });
    expect(GovukValidators.max(5, errorMessage)(new FormControl(6))).toEqual({ max: errorMessage });
    expect(GovukValidators.email(errorMessage)(new FormControl('asd'))).toEqual({ email: errorMessage });
    expect(GovukValidators.minLength(5, errorMessage)(new FormControl('long'))).toEqual({ minlength: errorMessage });
    expect(GovukValidators.maxLength(5, errorMessage)(new FormControl('long one'))).toEqual({
      maxlength: errorMessage,
    });
    expect(GovukValidators.pattern('/[0-9]+/', errorMessage)(new FormControl('let'))).toEqual({
      pattern: errorMessage,
    });
    expect(GovukValidators.maxIntegerAndDecimalsValidator(12, 5)(new FormControl('1231512512616746.45346563'))).toEqual(
      {
        pattern: 'Enter a number up to 12 integer and 5 decimal places',
      },
    );
    expect(GovukValidators.maxDecimalsValidator(5)(new FormControl(6.45346563))).toEqual({
      pattern: 'Enter a number up to 5 decimal places',
    });
  });

  it('should validate numbers within an exclusive range and decimal limit with a single error', () => {
    const message = 'Enter a numerical value, between - 100 and 100 with up to 3 decimal places';
    const validator = GovukValidators.numberInExclusiveRangeWithMaxDecimals(-100, 100, 3, message);
    const error = { numberInExclusiveRangeWithMaxDecimals: message };

    expect(validator(new FormControl(null))).toBeNull();
    expect(validator(new FormControl(''))).toBeNull();
    expect(validator(new FormControl('-99.999'))).toBeNull();
    expect(validator(new FormControl('0'))).toBeNull();
    expect(validator(new FormControl('99.999'))).toBeNull();

    expect(validator(new FormControl('-100'))).toEqual(error);
    expect(validator(new FormControl('100'))).toEqual(error);
    expect(validator(new FormControl('-100.000'))).toEqual(error);
    expect(validator(new FormControl('100.000'))).toEqual(error);
    expect(validator(new FormControl('99.9999'))).toEqual(error);
    expect(validator(new FormControl('text'))).toEqual(error);
  });
});
