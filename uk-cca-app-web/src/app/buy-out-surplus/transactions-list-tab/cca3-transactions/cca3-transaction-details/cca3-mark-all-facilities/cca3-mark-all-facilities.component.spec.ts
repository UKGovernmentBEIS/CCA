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
  mockFacilityTransactionPaid,
  mockFacilityTransactionRefund,
} from '../../../testing/cca3-facility-mock-data';
import { Cca3TransactionDetailsStore } from '../cca3-transaction-details.store';
import { Cca3MarkAllFacilitiesComponent } from './cca3-mark-all-facilities.component';
import { Cca3MarkAllStatus } from './cca3-mark-all-facilities.types';

describe('Cca3MarkAllFacilitiesComponent', () => {
  let component: Cca3MarkAllFacilitiesComponent;
  let fixture: ComponentFixture<Cca3MarkAllFacilitiesComponent>;
  let store: Cca3TransactionDetailsStore;
  let router: Router;
  let activatedRoute: ActivatedRouteStub;
  let mockUpdateService: { markAllBuyOutSurplusTransactionDetailsPaymentStatus: Mock };

  const [firstFacility] = mockFacilityTransactionDetailsPageOne.transactionDetails;

  // the date input holds Date objects, although the generated API model types the dates as strings. It reads the
  // local day of the written value and emits it back as UTC midnight, so a fixed past day keeps the specs stable.
  const enteredDate = new Date(2026, 4, 1) as unknown as string;
  const submittedDate = new Date(Date.UTC(2026, 4, 1)) as unknown as string;

  const evidenceFile = { uuid: 'evidence-uuid', file: { name: 'evidence.pdf' } as File };

  const pendingRefund: FacilityBuyOutSurplusTransactionDTO = {
    ...mockFacilityTransactionRefund,
    expectedMarkedFacilities: 4,
    actualMarkedFacilities: 1,
  };

  async function setup(transactionDetails: FacilityBuyOutSurplusTransactionDTO, markAllStatus: Cca3MarkAllStatus) {
    mockUpdateService = {
      markAllBuyOutSurplusTransactionDetailsPaymentStatus: vi.fn().mockReturnValue(of(null)),
    };

    activatedRoute = new ActivatedRouteStub(
      { transactionId: `${transactionDetails.id}` },
      {},
      { transactionDetails, markAllStatus },
    );

    await TestBed.configureTestingModule({
      imports: [Cca3MarkAllFacilitiesComponent],
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

    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);

    fixture = TestBed.createComponent(Cca3MarkAllFacilitiesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  const element = (): HTMLElement => fixture.nativeElement;

  const summaryRows = (): Record<string, string> =>
    Object.fromEntries(
      Array.from<HTMLElement>(element().querySelectorAll('dt')).map((key) => [
        key.textContent.trim(),
        key.nextElementSibling?.textContent.trim(),
      ]),
    );

  const heading = () => element().querySelector('h1')?.textContent.trim();
  const intro = () => element().querySelector('p.govuk-hint')?.textContent.trim();
  const warning = () => element().querySelector('.govuk-warning-text__text')?.textContent.replace(/\s+/g, ' ').trim();
  const dateInput = () => element().querySelector('.govuk-date-input');

  const submitForm = () => {
    element().querySelector('form').dispatchEvent(new Event('submit'));
    fixture.detectChanges();
  };

  const rejectWithBatchRunInProgress = () =>
    mockUpdateService.markAllBuyOutSurplusTransactionDetailsPaymentStatus.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'BS1004' } })),
    );

  describe('for a buy-out transaction', () => {
    beforeEach(() => setup(mockFacilityTransactionFee, 'PAID'));

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should ask to mark all selected facilities as paid', () => {
      expect(heading()).toContain('Are you sure you want to mark all selected facilities as paid?');
      expect(intro()).toBe(
        "Only facilities that meet the 'awaiting payment' eligibility criteria when processed will be updated.",
      );
      expect(warning()).toBe(
        "Warning Some facilities may not be updated if they no longer meet the 'awaiting payment' eligibility criteria when processed.",
      );
    });

    it('should show the target unit, the transaction ID and the number of pending facilities', () => {
      expect(summaryRows()).toEqual({
        'Target unit': mockFacilityTransactionFee.accountBusinessId,
        'Transaction ID': mockFacilityTransactionFee.transactionCode,
        'Selected facilities': 'All (2 facilities)',
      });
    });

    it('should ask for the payment date with an example', () => {
      expect(dateInput()).toBeTruthy();
      expect(element().textContent).toContain('Enter the date when the payment was received in the bank account');
      expect(element().textContent).toContain('For example 27 3 2026');
    });

    it('should not submit and show the errors when the payment date is missing', () => {
      submitForm();

      expect(mockUpdateService.markAllBuyOutSurplusTransactionDetailsPaymentStatus).not.toHaveBeenCalled();
      expect(element().querySelector('.govuk-error-summary').textContent).toContain(
        'Enter the date when the payment was received in the bank account',
      );
    });

    it('should mark all facilities as paid and navigate to the confirmation page', () => {
      component.form.patchValue({
        paymentDate: enteredDate,
        comments: 'Received in full',
        evidenceFiles: [evidenceFile],
      });

      component.onSubmit();

      expect(mockUpdateService.markAllBuyOutSurplusTransactionDetailsPaymentStatus).toHaveBeenCalledWith(
        mockFacilityTransactionFee.id,
        {
          status: 'PAID',
          paymentDate: submittedDate,
          comments: 'Received in full',
          evidenceFiles: { 'evidence-uuid': 'evidence.pdf' },
        },
      );
      expect(store.state.selectedFacilities.size).toBe(0);
      expect(router.navigate).toHaveBeenCalledWith(['confirmation'], { relativeTo: activatedRoute, replaceUrl: true });
    });

    it('should show an error and stay on the page when a buy-out batch run is in progress', () => {
      rejectWithBatchRunInProgress();
      component.form.patchValue({ paymentDate: enteredDate });

      component.onSubmit();
      fixture.detectChanges();

      expect(element().querySelector('cca-error-summary').textContent).toContain(
        'You cannot make any adjustments while the buy-out batch run is in progress',
      );
      expect(store.state.selectedFacilities.size).toBe(1);
      expect(router.navigate).not.toHaveBeenCalled();
    });

    it('should hide the batch run error once the form is changed', () => {
      rejectWithBatchRunInProgress();
      component.form.patchValue({ paymentDate: enteredDate });
      component.onSubmit();
      fixture.detectChanges();

      component.form.patchValue({ comments: 'Retrying' });
      fixture.detectChanges();

      expect(element().querySelector('cca-error-summary')).toBeNull();
    });

    it('should leave any other error to the global error handling', async () => {
      const error = new HttpErrorResponse({ status: 400, error: { code: 'NOTFOUND1001' } });
      const onUnhandledError = vi.fn();
      const previousHandler = config.onUnhandledError;
      config.onUnhandledError = onUnhandledError;

      mockUpdateService.markAllBuyOutSurplusTransactionDetailsPaymentStatus.mockReturnValue(throwError(() => error));
      component.form.patchValue({ paymentDate: enteredDate });

      component.onSubmit();
      await new Promise((resolve) => setTimeout(resolve));
      fixture.detectChanges();
      config.onUnhandledError = previousHandler;

      expect(onUnhandledError).toHaveBeenCalledWith(error);
      expect(element().querySelector('cca-error-summary')).toBeNull();
      expect(store.state.selectedFacilities.size).toBe(1);
      expect(router.navigate).not.toHaveBeenCalled();
    });
  });

  describe('for a buy-out transaction with a single pending facility', () => {
    beforeEach(() => setup({ ...mockFacilityTransactionFee, actualMarkedFacilities: 11 }, 'PAID'));

    it('should show the pending facility in singular', () => {
      expect(summaryRows()['Selected facilities']).toBe('All (1 facility)');
    });
  });

  describe('for a refund transaction', () => {
    beforeEach(() => setup(pendingRefund, 'REFUNDED'));

    it('should ask to mark all awaiting facilities as refunded', () => {
      expect(heading()).toContain('Are you sure you want to mark all awaiting facilities as refunded?');
      expect(intro()).toBe(
        "Only facilities that meet the 'awaiting refund' eligibility criteria when processed will be updated. Facilities being terminated or no longer eligible will not be updated.",
      );
      expect(warning()).toBe(
        "Warning Some facilities may not be updated if they no longer meet the 'awaiting refund' eligibility criteria when processed.",
      );
    });

    it('should show the number of pending facilities', () => {
      expect(summaryRows()['Selected facilities']).toBe('All (3 facilities)');
    });

    it('should not ask for a payment date', () => {
      expect(dateInput()).toBeNull();
      expect(component.form.valid).toBe(true);
    });

    it('should mark all facilities as refunded without any date', () => {
      submitForm();

      expect(mockUpdateService.markAllBuyOutSurplusTransactionDetailsPaymentStatus).toHaveBeenCalledWith(
        pendingRefund.id,
        { status: 'REFUNDED', comments: '', evidenceFiles: {} },
      );
      expect(
        mockUpdateService.markAllBuyOutSurplusTransactionDetailsPaymentStatus.mock.calls[0][1].paymentDate,
      ).toBeUndefined();
    });
  });

  describe.each<[string, FacilityBuyOutSurplusTransactionDTO, Cca3MarkAllStatus]>([
    ['no facility is pending anymore', mockFacilityTransactionPaid, 'PAID'],
    ['the page does not match the charge type', mockFacilityTransactionFee, 'REFUNDED'],
  ])('when %s', (_, transactionDetails, markAllStatus) => {
    beforeEach(() => setup(transactionDetails, markAllStatus));

    it('should redirect to the transaction details without rendering the form', () => {
      expect(router.navigate).toHaveBeenCalledWith(['../../'], { relativeTo: activatedRoute, replaceUrl: true });
      expect(element().querySelector('form')).toBeNull();
    });
  });
});
