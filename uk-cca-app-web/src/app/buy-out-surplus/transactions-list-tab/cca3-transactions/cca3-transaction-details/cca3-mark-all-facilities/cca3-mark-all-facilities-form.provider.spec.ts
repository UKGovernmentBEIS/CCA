import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { Cca3MarkAllStatus } from './cca3-mark-all-facilities.types';
import {
  CCA3_MARK_ALL_FACILITIES_FORM,
  Cca3MarkAllFacilitiesFormModel,
  Cca3MarkAllFacilitiesFormProvider,
} from './cca3-mark-all-facilities-form.provider';

describe('Cca3MarkAllFacilitiesFormProvider', () => {
  function createForm(markAllStatus: Cca3MarkAllStatus): Cca3MarkAllFacilitiesFormModel {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        Cca3MarkAllFacilitiesFormProvider,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub({}, {}, { markAllStatus }) },
      ],
    });

    return TestBed.inject(CCA3_MARK_ALL_FACILITIES_FORM);
  }

  const daysFromToday = (days: number) => {
    const date = new Date();
    date.setDate(date.getDate() + days);
    return date as unknown as string;
  };

  describe('when marking all facilities as paid', () => {
    let form: Cca3MarkAllFacilitiesFormModel;

    beforeEach(() => (form = createForm('PAID')));

    it('should require the payment date', () => {
      expect(form.controls.paymentDate.errors).toEqual({
        required: 'Enter the date when the payment was received in the bank account',
      });
    });

    it('should reject a future payment date', () => {
      form.controls.paymentDate.setValue(daysFromToday(1));

      expect(form.controls.paymentDate.errors).toEqual({
        invalidDate: 'The date when the payment was received must be today or in the past',
      });
    });

    it('should accept a payment date in the past', () => {
      form.controls.paymentDate.setValue(daysFromToday(-1));

      expect(form.controls.paymentDate.errors).toBeNull();
      expect(form.valid).toBe(true);
    });

    it('should keep the comments and evidence files optional', () => {
      expect(form.controls.comments.errors).toBeNull();
      expect(form.controls.evidenceFiles.errors).toBeNull();
    });
  });

  describe('when marking all facilities as refunded', () => {
    let form: Cca3MarkAllFacilitiesFormModel;

    beforeEach(() => (form = createForm('REFUNDED')));

    it('should not ask for a payment date', () => {
      expect(form.controls.paymentDate.disabled).toBe(true);
      expect(form.valid).toBe(true);
    });

    it('should reject comments longer than 10000 characters', () => {
      form.controls.comments.setValue('a'.repeat(10001));

      expect(form.controls.comments.errors).toEqual({
        maxlength: 'The comments should not be more than 10000 characters',
      });
      expect(form.valid).toBe(false);
    });
  });
});
