import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { FacilityBuyOutSurplusTransactionDTO } from 'cca-api';

import { MAX_RECEIVED_AMOUNT_MESSAGE, NEGATIVE_RECEIVED_AMOUNT_MESSAGE } from './cca3-received-amount.validators';
import { CCA3_RECEIVED_AMOUNT_FORM, cca3ReceivedAmountFormProvider } from './cca3-received-amount-form.provider';

describe('CCA3 received amount validation', () => {
  let transaction: FacilityBuyOutSurplusTransactionDTO;

  beforeEach(() => {
    transaction = { receivedAmount: '500.10', expectedAmount: '600' };
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        cca3ReceivedAmountFormProvider,
        { provide: ActivatedRoute, useValue: { snapshot: { data: { transactionDetails: transaction } } } },
      ],
    });
  });

  it.each([
    '',
    '0',
    '0.00',
    '-1',
    '-0.01',
    '1.001',
    '1e3',
    '+12',
    'NaN',
    'Infinity',
    '1,000',
    'abc',
    '1000000000000000000.01',
  ])('rejects a missing, nonpositive or incorrectly formatted amount: %s', (value) => {
    const control = TestBed.inject(CCA3_RECEIVED_AMOUNT_FORM).controls.adjustmentAmount;
    control.setValue(value);
    expect(control.invalid).toBe(true);
  });

  it.each(['1', '0.01', '120.50', '999999999999999999.99'])('preserves a valid decimal amount: %s', (value) => {
    const control = TestBed.inject(CCA3_RECEIVED_AMOUNT_FORM).controls.adjustmentAmount;
    control.setValue(value);
    expect(control.valid).toBe(true);
    expect(control.value).toBe(value);
  });

  it('defaults to Add and changes the date requirement without clearing an entered date', () => {
    const form = TestBed.inject(CCA3_RECEIVED_AMOUNT_FORM);
    expect(form.controls.changeType.value).toBe('add');
    expect(form.controls.paymentDate.hasError('required')).toBe(true);

    form.patchValue({ adjustmentAmount: '1', comments: 'Payment received' });
    expect(form.invalid).toBe(true);

    form.controls.changeType.setValue('subtract');
    expect(form.controls.paymentDate.valid).toBe(true);
    expect(form.valid).toBe(true);

    form.controls.changeType.setValue('add');
    expect(form.controls.paymentDate.hasError('required')).toBe(true);
    expect(form.invalid).toBe(true);

    form.controls.changeType.setValue('subtract');
    const date = new Date(Date.UTC(2025, 0, 15));
    form.controls.paymentDate.setValue(date);
    form.controls.changeType.setValue('add');
    expect(form.controls.paymentDate.value).toEqual(date);
    expect(form.controls.paymentDate.valid).toBe(true);
  });

  it.each(['add', 'subtract'] as const)('allows today but rejects future dates for %s', (changeType) => {
    const form = TestBed.inject(CCA3_RECEIVED_AMOUNT_FORM);
    form.controls.changeType.setValue(changeType);
    form.controls.paymentDate.setValue(new Date());
    expect(form.controls.paymentDate.valid).toBe(true);
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    form.controls.paymentDate.setValue(tomorrow);
    expect(form.controls.paymentDate.hasError('invalidDate')).toBe(true);
  });

  it.each(['', ' ', '\n\t'])('requires nonblank comments: %j', (value) => {
    const control = TestBed.inject(CCA3_RECEIVED_AMOUNT_FORM).controls.comments;
    control.setValue(value);
    expect(control.invalid).toBe(true);
    expect(Object.values(control.errors)).toContain('Enter a comment');
  });

  it('enforces the API comments limit and keeps evidence optional', () => {
    const form = TestBed.inject(CCA3_RECEIVED_AMOUNT_FORM);
    form.patchValue({ changeType: 'subtract', adjustmentAmount: '1', comments: 'a'.repeat(10000) });
    expect(form.valid).toBe(true);
    form.controls.comments.setValue('a'.repeat(10001));
    expect(form.controls.comments.invalid).toBe(true);
  });

  it('allows subtraction to exactly zero, rejects a negative total and permits overpayment', () => {
    const form = TestBed.inject(CCA3_RECEIVED_AMOUNT_FORM);
    form.patchValue({ changeType: 'subtract', adjustmentAmount: '500.10', comments: 'Correction' });
    expect(form.valid).toBe(true);
    form.controls.adjustmentAmount.setValue('500.11');
    expect(form.errors).toEqual({ negativeReceivedAmount: NEGATIVE_RECEIVED_AMOUNT_MESSAGE });
    form.patchValue({ changeType: 'add', adjustmentAmount: '1000', paymentDate: new Date() });
    expect(form.valid).toBe(true);
  });

  it.each(['0', undefined])('prevents subtraction when the current received amount is %s', (receivedAmount) => {
    transaction.receivedAmount = receivedAmount;
    const form = TestBed.inject(CCA3_RECEIVED_AMOUNT_FORM);
    form.patchValue({ changeType: 'subtract', adjustmentAmount: '0.01', comments: 'Correction' });
    expect(form.errors).toEqual({ negativeReceivedAmount: NEGATIVE_RECEIVED_AMOUNT_MESSAGE });
  });

  it('checks the total limit without losing a penny and revalidates when the change type changes', () => {
    transaction.receivedAmount = '999999999999999999.98';
    const form = TestBed.inject(CCA3_RECEIVED_AMOUNT_FORM);
    form.patchValue({ adjustmentAmount: '0.01', paymentDate: new Date(), comments: 'Payment received' });
    expect(form.valid).toBe(true);

    form.controls.adjustmentAmount.setValue('0.02');
    expect(form.errors).toEqual({ maxReceivedAmount: MAX_RECEIVED_AMOUNT_MESSAGE });

    form.controls.changeType.setValue('subtract');
    expect(form.valid).toBe(true);
    form.controls.adjustmentAmount.setValue('999999999999999999.99');
    expect(form.errors).toEqual({ negativeReceivedAmount: NEGATIVE_RECEIVED_AMOUNT_MESSAGE });
    form.controls.adjustmentAmount.setValue('999999999999999999.98');
    expect(form.valid).toBe(true);
  });

  it('allows the selected calendar day before UTC midnight and rejects the next day', () => {
    vi.useFakeTimers();
    try {
      vi.setSystemTime(new Date(2025, 0, 15, 0, 30));
      const form = TestBed.inject(CCA3_RECEIVED_AMOUNT_FORM);
      form.controls.paymentDate.setValue(new Date(Date.UTC(2025, 0, 15)));
      expect(form.controls.paymentDate.valid).toBe(true);
      form.controls.paymentDate.setValue(new Date(Date.UTC(2025, 0, 16)));
      expect(form.controls.paymentDate.hasError('invalidDate')).toBe(true);
    } finally {
      vi.useRealTimers();
    }
  });
});
