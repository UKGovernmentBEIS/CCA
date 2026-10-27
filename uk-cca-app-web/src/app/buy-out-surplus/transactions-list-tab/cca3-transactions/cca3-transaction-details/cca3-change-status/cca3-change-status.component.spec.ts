import { HttpErrorResponse, provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { config, of, throwError } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';
import { Mock } from 'vitest';

import { BuyOutSurplusTransactionUpdateControllerService, FacilityBuyOutSurplusTransactionDTO } from 'cca-api';

import {
  mockFacilityTransactionDetailsPageOne,
  mockFacilityTransactionFee,
  mockFacilityTransactionRefund,
} from '../../../testing/cca3-facility-mock-data';
import { Cca3TransactionDetailsStore } from '../cca3-transaction-details.store';
import { Cca3ChangeStatusComponent } from './cca3-change-status.component';

describe('Cca3ChangeStatusComponent', () => {
  let component: Cca3ChangeStatusComponent;
  let fixture: ComponentFixture<Cca3ChangeStatusComponent>;
  let store: Cca3TransactionDetailsStore;
  let router: Router;
  let activatedRoute: ActivatedRouteStub;
  let mockUpdateService: { updateBuyOutSurplusTransactionDetailsPaymentStatus: Mock };

  const [firstFacility, secondFacility] = mockFacilityTransactionDetailsPageOne.transactionDetails;

  // the date input holds Date objects, although the generated API model types the dates as strings. It reads the
  // local day of the written value and emits it back as UTC midnight, so a fixed past day keeps the specs stable.
  const enteredDate = new Date(2026, 4, 1) as unknown as string;
  const submittedDate = new Date(Date.UTC(2026, 4, 1)) as unknown as string;

  async function setup(transactionDetails: FacilityBuyOutSurplusTransactionDTO) {
    mockUpdateService = {
      updateBuyOutSurplusTransactionDetailsPaymentStatus: vi.fn().mockReturnValue(of(null)),
    };

    activatedRoute = new ActivatedRouteStub({ transactionId: `${transactionDetails.id}` }, {}, { transactionDetails });

    await TestBed.configureTestingModule({
      imports: [Cca3ChangeStatusComponent],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        Cca3TransactionDetailsStore,
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: BuyOutSurplusTransactionUpdateControllerService, useValue: mockUpdateService },
      ],
    }).compileComponents();

    store = TestBed.inject(Cca3TransactionDetailsStore);
    store.updateSelectedFacilities(firstFacility, true);
    store.updateSelectedFacilities(secondFacility, true);

    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);

    fixture = TestBed.createComponent(Cca3ChangeStatusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  const summaryRows = (): Record<string, string> =>
    Object.fromEntries(
      Array.from<HTMLElement>(fixture.nativeElement.querySelectorAll('dt')).map((key) => [
        key.textContent.trim(),
        key.nextElementSibling?.textContent.trim(),
      ]),
    );

  const statusLabels = (): string[] =>
    Array.from<HTMLLabelElement>(fixture.nativeElement.querySelectorAll('.govuk-radios__label')).map((label) =>
      label.textContent.trim(),
    );

  const submittedPayload = () => mockUpdateService.updateBuyOutSurplusTransactionDetailsPaymentStatus.mock.calls[0][1];

  describe('for a buy-out transaction', () => {
    beforeEach(() => setup(mockFacilityTransactionFee));

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show the transaction ID and the number of selected facilities', () => {
      expect(summaryRows()).toEqual({
        'Transaction ID': mockFacilityTransactionFee.transactionCode,
        'Selected facilities': '2',
      });
    });

    it('should show the buy-out statuses', () => {
      expect(statusLabels()).toEqual(['Awaiting payment', 'Under appeal', 'Not required', 'Paid']);
    });

    it('should preselect awaiting payment', () => {
      expect(component.form.value.status).toBe('AWAITING_PAYMENT');
    });

    it('should show that no evidence has been uploaded yet', () => {
      expect(fixture.nativeElement.textContent).toContain('No files have been uploaded yet');
    });

    it('should update the selected facilities and navigate to the confirmation page', () => {
      component.form.controls.status.setValue('UNDER_APPEAL');
      component.form.patchValue({ dateOfAppeal: enteredDate, appealComments: 'Appeal lodged', evidenceFiles: [] });

      component.onSubmit();

      expect(mockUpdateService.updateBuyOutSurplusTransactionDetailsPaymentStatus).toHaveBeenCalledWith(
        mockFacilityTransactionFee.id,
        expect.objectContaining({
          status: 'UNDER_APPEAL',
          comments: 'Appeal lodged',
          dateOfAppeal: submittedDate,
          detailIds: [firstFacility.id, secondFacility.id],
        }),
      );
      expect(store.state.selectedFacilities.size).toBe(0);
      expect(router.navigate).toHaveBeenCalledWith(['confirmation'], { relativeTo: activatedRoute, replaceUrl: true });
    });

    it('should send the payment date and the paid comments when marking the facilities as paid', () => {
      component.form.controls.status.setValue('PAID');
      component.form.patchValue({ paymentDate: enteredDate, paidComments: 'Received in full' });

      component.onSubmit();

      expect(submittedPayload()).toEqual(
        expect.objectContaining({ status: 'PAID', paymentDate: submittedDate, comments: 'Received in full' }),
      );
      expect(submittedPayload().dateOfAppeal).toBeUndefined();
    });

    it('should require comments when the facilities are not required to pay', () => {
      component.form.controls.status.setValue('NOT_REQUIRED');
      fixture.detectChanges();

      expect(component.form.controls.notRequiredComments.errors).toEqual({ required: 'Please enter a comment' });

      component.form.patchValue({ notRequiredComments: 'Exempt facility' });
      component.onSubmit();

      expect(submittedPayload()).toEqual(
        expect.objectContaining({ status: 'NOT_REQUIRED', comments: 'Exempt facility' }),
      );
    });

    it('should reject comments longer than 10000 characters', () => {
      component.form.patchValue({ awaitingPaymentComments: 'a'.repeat(10001) });

      expect(component.form.controls.awaitingPaymentComments.errors).toEqual({
        maxlength: 'The comments should not be more than 10000 characters',
      });
    });

    it('should show an error and keep the selection when a buy-out batch run is in progress', () => {
      mockUpdateService.updateBuyOutSurplusTransactionDetailsPaymentStatus.mockReturnValue(
        throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'BS1004' } })),
      );

      component.onSubmit();
      fixture.detectChanges();

      expect(fixture.nativeElement.textContent).toContain(
        'You cannot make any adjustments while the buy-out batch run is in progress',
      );
      expect(store.state.selectedFacilities.size).toBe(2);
      expect(router.navigate).not.toHaveBeenCalled();
    });

    it('should leave any other error to the global error handling', async () => {
      const error = new HttpErrorResponse({ status: 400, error: { code: 'NOTFOUND1001' } });
      const onUnhandledError = vi.fn();
      const previousHandler = config.onUnhandledError;
      config.onUnhandledError = onUnhandledError;

      mockUpdateService.updateBuyOutSurplusTransactionDetailsPaymentStatus.mockReturnValue(throwError(() => error));

      component.onSubmit();
      await new Promise((resolve) => setTimeout(resolve));
      fixture.detectChanges();
      config.onUnhandledError = previousHandler;

      expect(onUnhandledError).toHaveBeenCalledWith(error);
      expect(fixture.nativeElement.querySelector('cca-error-summary')).toBeNull();
      expect(store.state.selectedFacilities.size).toBe(2);
      expect(router.navigate).not.toHaveBeenCalled();
    });
  });

  describe('for a refund transaction', () => {
    beforeEach(() => setup(mockFacilityTransactionRefund));

    it('should show the refund statuses', () => {
      expect(statusLabels()).toEqual(['Awaiting refund', 'Not required', 'Refunded']);
    });

    it('should preselect awaiting refund', () => {
      expect(component.form.value.status).toBe('AWAITING_REFUND');
    });

    it('should send the refunded comments without any date', () => {
      component.form.controls.status.setValue('REFUNDED');
      component.form.patchValue({ refundedComments: 'Refund issued' });

      component.onSubmit();

      expect(submittedPayload()).toEqual(expect.objectContaining({ status: 'REFUNDED', comments: 'Refund issued' }));
      expect(submittedPayload().paymentDate).toBeUndefined();
      expect(submittedPayload().dateOfAppeal).toBeUndefined();
    });
  });
});
