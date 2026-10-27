import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { CCA3_CHANGE_AMOUNT_FORM, cca3ChangeAmountFormProvider } from './cca3-change-amount-form.provider';

describe('CCA3 change amount validation', () => {
  beforeEach(() =>
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), cca3ChangeAmountFormProvider],
    }),
  );

  it.each(['', '0', '00', '0.00', '-1', '-0.01'])('rejects a missing or nonpositive amount: %s', (value) => {
    const control = TestBed.inject(CCA3_CHANGE_AMOUNT_FORM).controls.amount;
    control.setValue(value);
    expect(control.invalid).toBe(true);
    expect(Object.values(control.errors)).toContain('Enter a number greater than 0 with up to 2 decimal place');
  });

  it.each(['1', '2400', '1.5', '120.50', '0.01', '999999999999999999', '999999999999999999.99'])(
    'accepts a positive amount without rounding: %s',
    (amount) => {
      const control = TestBed.inject(CCA3_CHANGE_AMOUNT_FORM).controls.amount;
      control.setValue(amount);
      expect(control.valid).toBe(true);
      expect(control.value).toBe(amount);
    },
  );

  it.each(['1.001', '0.001', '1e3', '+12', 'NaN', 'Infinity', '1,000', 'abc'])(
    'rejects invalid currency precision or format: %s',
    (amount) => {
      const control = TestBed.inject(CCA3_CHANGE_AMOUNT_FORM).controls.amount;
      control.setValue(amount);
      expect(control.invalid).toBe(true);
      expect(Object.values(control.errors)).toContain('Enter a number greater than 0 with up to 2 decimal place');
    },
  );

  it.each(['1000000000000000000', '1000000000000000000.01'])(
    'rejects amounts beyond the integer digit limit: %s',
    (amount) => {
      const control = TestBed.inject(CCA3_CHANGE_AMOUNT_FORM).controls.amount;
      control.setValue(amount);
      expect(control.invalid).toBe(true);
    },
  );

  it.each(['', ' ', '\n\t'])('requires a nonblank reason: %j', (reason) => {
    const control = TestBed.inject(CCA3_CHANGE_AMOUNT_FORM).controls.comments;
    control.setValue(reason);
    expect(control.invalid).toBe(true);
    expect(Object.values(control.errors)).toContain('Enter a reason');
  });

  it('limits comments to the API limit and allows no evidence', () => {
    const form = TestBed.inject(CCA3_CHANGE_AMOUNT_FORM);
    form.patchValue({ amount: '2400', comments: 'a'.repeat(10000) });
    expect(form.valid).toBe(true);
    form.controls.comments.setValue('a'.repeat(10001));
    expect(form.controls.comments.invalid).toBe(true);
  });
});
