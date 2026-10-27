import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { FacilityBuyOutSurplusTransactionDTO } from 'cca-api';

import { mockFacilityTransactionFee, mockFacilityTransactionRefund } from '../../../testing/cca3-facility-mock-data';
import {
  CCA3_CHANGE_STATUS_FORM,
  Cca3ChangeStatusFormModel,
  Cca3ChangeStatusFormProvider,
} from './cca3-change-status-form.provider';

describe('Cca3ChangeStatusFormProvider', () => {
  function createForm(transactionDetails: FacilityBuyOutSurplusTransactionDTO): Cca3ChangeStatusFormModel {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        Cca3ChangeStatusFormProvider,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub({}, {}, { transactionDetails }) },
      ],
    });

    return TestBed.inject(CCA3_CHANGE_STATUS_FORM);
  }

  it('should preselect awaiting payment for buy-out transactions', () => {
    expect(createForm(mockFacilityTransactionFee).value.status).toBe('AWAITING_PAYMENT');
  });

  it('should preselect awaiting refund for refund transactions', () => {
    expect(createForm(mockFacilityTransactionRefund).value.status).toBe('AWAITING_REFUND');
  });

  it('should require the payment date only when a buy-out is marked as paid', () => {
    const form = createForm(mockFacilityTransactionFee);

    expect(form.controls.paymentDate.disabled).toBe(true);

    form.controls.status.setValue('PAID');

    expect(form.controls.paymentDate.enabled).toBe(true);
    expect(form.controls.paymentDate.errors).toEqual({ required: 'Please enter a date' });
  });

  it('should require the appeal date and comments only when a buy-out is under appeal', () => {
    const form = createForm(mockFacilityTransactionFee);

    expect(form.controls.dateOfAppeal.disabled).toBe(true);
    expect(form.controls.appealComments.disabled).toBe(true);

    form.controls.status.setValue('UNDER_APPEAL');

    expect(form.controls.dateOfAppeal.errors).toEqual({ required: 'Please enter a date' });
    expect(form.controls.appealComments.errors).toEqual({ required: 'Please enter a comment' });

    form.controls.status.setValue('AWAITING_PAYMENT');

    expect(form.controls.dateOfAppeal.disabled).toBe(true);
    expect(form.controls.appealComments.disabled).toBe(true);
  });

  it.each(['PAID', 'UNDER_APPEAL'] as const)(
    'should keep the buy-out only fields disabled for refund transactions when the status is %s',
    (status) => {
      const form = createForm(mockFacilityTransactionRefund);

      form.controls.status.setValue(status);

      expect(form.controls.paymentDate.disabled).toBe(true);
      expect(form.controls.dateOfAppeal.disabled).toBe(true);
      expect(form.controls.appealComments.disabled).toBe(true);
    },
  );

  it('should reject future payment and appeal dates', () => {
    const form = createForm(mockFacilityTransactionFee);
    const tomorrow = new Date(Date.now() + 24 * 60 * 60 * 1000) as unknown as string;

    form.controls.status.setValue('PAID');
    form.controls.paymentDate.setValue(tomorrow);

    expect(form.controls.paymentDate.errors).toEqual({ invalidDate: 'The payment date cannot be a future date.' });

    form.controls.status.setValue('UNDER_APPEAL');
    form.controls.dateOfAppeal.setValue(tomorrow);

    expect(form.controls.dateOfAppeal.errors).toEqual({ invalidDate: 'The appeal date cannot be a future date.' });
  });
});
