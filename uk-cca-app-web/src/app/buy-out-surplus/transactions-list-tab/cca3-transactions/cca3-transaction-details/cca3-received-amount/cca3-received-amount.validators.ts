import { FormControl, ValidatorFn } from '@angular/forms';

import { GovukValidators } from '@netz/govuk-components';
import BigNumber from 'bignumber.js';

import type { Cca3ReceivedAmountForm } from './cca3-received-amount-form.provider';

export const NEGATIVE_RECEIVED_AMOUNT_MESSAGE = 'The total received amount must be greater than or equal to zero.';
export const MAX_RECEIVED_AMOUNT_MESSAGE = 'The total received amount must not exceed 999,999,999,999,999,999.99.';

export function paymentDateRequiredValidator(changeType: FormControl<'add' | 'subtract'>): ValidatorFn {
  const required = GovukValidators.required('Enter a date');
  return (control) => (changeType.value === 'add' ? required(control) : null);
}

export function paymentDateValidator(): ValidatorFn {
  return (control) => {
    const today = new Date();
    // The date input uses UTC midnight for the selected calendar day.
    const tomorrow = Date.UTC(today.getFullYear(), today.getMonth(), today.getDate() + 1);
    return control.value && control.value.getTime() >= tomorrow
      ? { invalidDate: 'The payment date cannot be a future date.' }
      : null;
  };
}

export function receivedAmountTotalValidator(receivedAmount: string): ValidatorFn {
  return (control) => {
    const form = control as Cca3ReceivedAmountForm;
    if (form.controls.adjustmentAmount.invalid) return null;

    const { changeType, adjustmentAmount } = form.getRawValue();
    const amount = new BigNumber(adjustmentAmount);
    const received = new BigNumber(receivedAmount);

    if (changeType === 'subtract') {
      return received.minus(amount).isNegative() ? { negativeReceivedAmount: NEGATIVE_RECEIVED_AMOUNT_MESSAGE } : null;
    }

    return received.plus(amount).gte('1000000000000000000') ? { maxReceivedAmount: MAX_RECEIVED_AMOUNT_MESSAGE } : null;
  };
}
