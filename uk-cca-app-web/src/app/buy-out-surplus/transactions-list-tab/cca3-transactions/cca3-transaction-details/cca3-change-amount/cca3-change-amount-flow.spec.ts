import { HttpErrorResponse, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter, Router, withRouterConfig } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { of, throwError } from 'rxjs';

import { AuthStore } from '@netz/common/auth';

import {
  BuyOutAndSurplusFacilityTransactionViewService,
  BuyOutSurplusTransactionDetailUpdateControllerService,
  FacilityBuyOutSurplusTransactionDTO,
} from 'cca-api';

import { PendingRequestInterceptor } from '../../../../../interceptors/pending-request.interceptor';
import { mockFacilityTransactionFee } from '../../../testing/cca3-facility-mock-data';
import { CCA3_TRANSACTIONS_ROUTES } from '../../cca3-transactions.routes';
import { BATCH_AMOUNT_MESSAGE, Cca3ChangeAmountState, TERMINATED_AMOUNT_MESSAGE } from './cca3-change-amount.state';

describe('CCA3 change facility amount flow', () => {
  const transactionUrl = '/buyout-surplus/cca3-transactions/10';
  const formUrl = `${transactionUrl}/facilities/ADS_1-F00005/change-amount`;
  let harness: RouterTestingHarness;
  let state: Cca3ChangeAmountState;
  let transaction: FacilityBuyOutSurplusTransactionDTO;
  let facility: {
    id: number;
    facilityBusinessId: string;
    paymentStatus: 'AWAITING_PAYMENT' | 'TERMINATED';
    invoicedBuyFee: string;
    buyOutFee: string;
  };
  const getTransaction = vi.fn();
  const getFacilities = vi.fn();
  const updateAmount = vi.fn();

  const text = () => harness.routeNativeElement?.textContent ?? '';
  const settle = async () => {
    await harness.fixture.whenStable();
    harness.detectChanges();
  };
  const submit = async () => {
    const formElement = harness.routeNativeElement.querySelector('form');
    if (formElement) {
      formElement.dispatchEvent(new Event('submit', { bubbles: true, cancelable: true }));
    } else {
      harness.routeNativeElement.querySelector<HTMLButtonElement>('button[govukButton]').click();
    }
    await settle();
  };
  const enterValidDraft = () => state.form.patchValue({ amount: '2400', comments: 'Corrected invoice amount' });
  const openForm = async () => {
    await harness.navigateByUrl(formUrl);
    state = harness.routeDebugElement.injector.get(Cca3ChangeAmountState);
    await settle();
  };

  beforeEach(async () => {
    transaction = { ...mockFacilityTransactionFee, id: 10, transactionCode: 'CCA2843-BUYOUT', chargeType: 'FEE' };
    facility = {
      id: 13,
      facilityBusinessId: 'ADS_1-F00005',
      paymentStatus: 'AWAITING_PAYMENT',
      invoicedBuyFee: '2644',
      buyOutFee: '2000',
    };
    getTransaction.mockReset().mockImplementation(() => of({ ...transaction }));
    getFacilities.mockReset().mockImplementation(() => of({ total: 1, transactionDetails: [{ ...facility }] }));
    updateAmount.mockReset().mockReturnValue(of(null));

    await TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [{ path: 'buyout-surplus/cca3-transactions', children: CCA3_TRANSACTIONS_ROUTES }],
          withRouterConfig({ paramsInheritanceStrategy: 'always' }),
        ),
        provideHttpClient(withInterceptors([PendingRequestInterceptor])),
        provideHttpClientTesting(),
        {
          provide: BuyOutAndSurplusFacilityTransactionViewService,
          useValue: {
            getFacilityBuyOutSurplusTransaction: getTransaction,
            getFacilityBuyOutSurplusTransactionDetails: getFacilities,
          },
        },
        {
          provide: BuyOutSurplusTransactionDetailUpdateControllerService,
          useValue: { updateBuyOutSurplusTransactionDetailAmount: updateAmount },
        },
      ],
    }).compileComponents();
    TestBed.inject(AuthStore).setUserState({ roleType: 'REGULATOR' });
    harness = await RouterTestingHarness.create();
  });

  it('shows the facility amounts and validation before allowing review', async () => {
    await openForm();
    expect(text()).toContain('2,644.00');
    expect(text()).toContain('2,000.00');
    expect(text()).toContain('No files have been uploaded yet');
    const amountInput = harness.routeNativeElement.querySelector<HTMLInputElement>('#amount');
    const amountHint = harness.routeNativeElement.querySelector<HTMLElement>('#amount-hint');
    expect(amountInput.getAttribute('aria-describedby')).toBe(amountHint.id);
    expect(amountHint.textContent).toContain('You must only enter a new amount');
    expect(amountHint.querySelector('strong').textContent).toContain(
      'Do not use this field to reflect partial payment amounts received for this facility.',
    );
    await submit();
    expect(text()).toContain('Enter a number greater than 0 with up to 2 decimal place');
    expect(text()).toContain('Enter a reason');
    expect(harness.routeNativeElement.querySelectorAll('.govuk-error-summary')).toHaveLength(1);
    expect(harness.routeNativeElement.querySelector('#amount-error').textContent).toContain(
      'Enter a number greater than 0 with up to 2 decimal place',
    );
    expect(harness.routeNativeElement.querySelector('#amount').classList.contains('govuk-input--error')).toBe(true);
    expect(amountInput.getAttribute('aria-describedby')).toBe(`${amountHint.id} amount-error`);
    expect(updateAmount).not.toHaveBeenCalled();
  });

  it('saves only after review and confirmation, using the detail ID rather than transaction ID', async () => {
    await openForm();
    enterValidDraft();
    await submit();
    expect(TestBed.inject(Router).url).toBe(`${formUrl}/check-your-answers`);
    expect(text()).toContain('2,400.00');
    expect(text()).toContain('Corrected invoice amount');
    expect(updateAmount).not.toHaveBeenCalled();
    await submit();
    expect(updateAmount).toHaveBeenCalledExactlyOnceWith(13, {
      amount: '2400',
      comments: 'Corrected invoice amount',
      evidenceFiles: {},
    });
    expect(text()).toContain('Current amount updated');
    expect(text()).toContain('Return to: Buy-out MoA CCA2843-BUYOUT');
    facility.buyOutFee = '2400';
    transaction.expectedAmount = '2400';
    getTransaction.mockClear();
    getFacilities.mockClear();
    await harness.navigateByUrl(transactionUrl);
    expect(getTransaction).toHaveBeenCalledWith(10);
    expect(getFacilities).toHaveBeenCalled();
    expect(text()).toContain('2,400.00');
  });

  it('searches for the facility once, not on every step of the flow', async () => {
    await openForm();
    enterValidDraft();
    await submit();
    await submit();
    expect(text()).toContain('Current amount updated');
    expect(getFacilities).toHaveBeenCalledTimes(1);
  });

  it('keeps the amount, reason and evidence when changing the reviewed answers', async () => {
    await openForm();
    enterValidDraft();
    const evidence = [{ uuid: 'test-file', file: { name: 'receipt.pdf' } }];
    state.form.controls.evidenceFiles.setValue(evidence);
    await submit();
    expect(text()).toContain('receipt.pdf');
    await harness.navigateByUrl(formUrl);
    expect(state.form.getRawValue()).toEqual({
      amount: '2400',
      comments: 'Corrected invoice amount',
      evidenceFiles: evidence,
    });
    await submit();
    await submit();
    expect(updateAmount).toHaveBeenCalledWith(
      13,
      expect.objectContaining({ evidenceFiles: { 'test-file': 'receipt.pdf' } }),
    );
  });

  it('downloads existing evidence from the facility detail endpoint from both the form and review', async () => {
    await openForm();
    enterValidDraft();
    state.form.controls.evidenceFiles.setValue([{ uuid: 'test-file', file: { name: 'receipt.pdf' } }]);
    await settle();
    const evidenceUrl = `${transactionUrl}/facilities/ADS_1-F00005/file-evidences-download/13/attachment/test-file`;
    expect(harness.routeNativeElement.querySelector('a[href$="/test-file"]').getAttribute('href')).toBe(evidenceUrl);

    await submit();
    expect(harness.routeNativeElement.querySelector('a[href$="/test-file"]').getAttribute('href')).toBe(evidenceUrl);

    vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => undefined);
    await harness.navigateByUrl(evidenceUrl);
    const http = TestBed.inject(HttpTestingController);
    const request = http.expectOne((req) => req.url.endsWith('/transaction-details/13/evidence'));
    expect(request.request.params.get('fileEvidenceUuid')).toBe('test-file');
    request.flush({ token: 'download-token', tokenExpirationMinutes: 1 });
    await settle();
    expect(harness.routeNativeElement.querySelector('a').getAttribute('href')).toBe(
      '/api/v1.0/file-evidences/download-token',
    );
    http.verify();
  });

  it.each([
    ['BS1002', TERMINATED_AMOUNT_MESSAGE],
    ['BS1004', BATCH_AMOUNT_MESSAGE],
  ])('shows %s and retains the draft after a rejected confirmation', async (code, message) => {
    await openForm();
    enterValidDraft();
    await submit();
    updateAmount.mockReturnValue(throwError(() => new HttpErrorResponse({ status: 400, error: { code } })));
    await submit();
    expect(TestBed.inject(Router).url).toBe(formUrl);
    expect(text()).toContain(message);
    expect(state.form.value.amount).toBe('2400');
    expect(state.form.value.comments).toBe('Corrected invoice amount');
    expect(text()).not.toContain('Current amount updated');
    expect(harness.routeNativeElement.querySelectorAll('.govuk-error-summary')).toHaveLength(1);
    expect(harness.routeNativeElement.querySelector('cca-error-summary a').getAttribute('href')).toBe(transactionUrl);
    expect(harness.routeNativeElement.querySelector('button[type="submit"]') !== null).toBe(code !== 'BS1002');
  });

  it('blocks review after the API reports that the facility has been terminated', async () => {
    await openForm();
    enterValidDraft();
    await submit();
    updateAmount.mockReturnValue(throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'BS1002' } })));
    await submit();
    await harness.navigateByUrl(`${formUrl}/check-your-answers`);
    expect(TestBed.inject(Router).url).toBe(formUrl);
    expect(text()).toContain(TERMINATED_AMOUNT_MESSAGE);
    expect(updateAmount).toHaveBeenCalledTimes(1);
  });

  it('can retry after the batch run ends', async () => {
    await openForm();
    enterValidDraft();
    await submit();
    updateAmount.mockReturnValueOnce(
      throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'BS1004' } })),
    );
    await submit();
    await submit();
    await submit();
    expect(updateAmount).toHaveBeenCalledTimes(2);
    expect(text()).toContain('Current amount updated');
  });

  it('does not submit a terminated facility reached through a direct link', async () => {
    facility.paymentStatus = 'TERMINATED';
    await openForm();
    expect(text()).toContain(TERMINATED_AMOUNT_MESSAGE);
    expect(harness.routeNativeElement.querySelector('button[type="submit"]')).toBeNull();
    enterValidDraft();
    await submit();
    expect(TestBed.inject(Router).url).toBe(formUrl);
    expect(updateAmount).not.toHaveBeenCalled();
  });

  it('returns a directly opened review page without a draft to the form', async () => {
    await harness.navigateByUrl(`${formUrl}/check-your-answers`);
    await settle();
    expect(TestBed.inject(Router).url).toBe(formUrl);
    expect(updateAmount).not.toHaveBeenCalled();
  });

  it('discards the draft on leaving the flow', async () => {
    await openForm();
    enterValidDraft();
    await harness.navigateByUrl(transactionUrl);
    await openForm();
    expect(state.form.value.amount).toBe('');
    expect(state.form.value.comments).toBe('');
  });

  it('does not review evidence while its upload validation is pending', async () => {
    await openForm();
    enterValidDraft();
    state.form.controls.evidenceFiles.markAsPending();
    await submit();
    expect(TestBed.inject(Router).url).toBe(formUrl);
    expect(updateAmount).not.toHaveBeenCalled();
  });

  it('does not reuse a previous draft when reopening review after leaving the flow', async () => {
    await openForm();
    enterValidDraft();
    await submit();
    await harness.navigateByUrl(transactionUrl);
    await harness.navigateByUrl(`${formUrl}/check-your-answers`);
    expect(TestBed.inject(Router).url).toBe(formUrl);
    expect(state.form.value.amount).toBe('');
    expect(state.form.value.comments).toBe('');
    expect(updateAmount).not.toHaveBeenCalled();
  });

  it.each([
    ['0.01', '0.01'],
    ['120.5', '120.50'],
    ['120.50', '120.50'],
    ['999999999999999999', '999,999,999,999,999,999.00'],
    ['999999999999999999.99', '999,999,999,999,999,999.99'],
  ])('preserves the amount %s entered through the shared text input', async (amount, formattedAmount) => {
    await openForm();
    enterValidDraft();
    const input = harness.routeNativeElement.querySelector<HTMLInputElement>('#amount');
    input.value = amount;
    input.dispatchEvent(new Event('input', { bubbles: true }));
    await submit();
    expect(text()).toContain(formattedAmount);
    await submit();
    expect(updateAmount).toHaveBeenCalledWith(13, expect.objectContaining({ amount }));
  });

  it('submits refunds as positive decimal amounts and returns to the refund transaction', async () => {
    transaction.chargeType = 'REFUND';
    transaction.transactionCode = 'CCA2843-REFUND';
    await openForm();
    expect(text()).toContain('Explain why you are changing the refund amount');
    enterValidDraft();
    state.form.controls.amount.setValue('2400.50');
    await submit();
    expect(text()).toContain('2,400.50');
    await submit();
    expect(updateAmount).toHaveBeenCalledWith(13, expect.objectContaining({ amount: '2400.50' }));
    expect(text()).toContain('Return to: Refund MoA CCA2843-REFUND');
  });

  it('prevents duplicate submissions while the request is pending', async () => {
    await openForm();
    enterValidDraft();
    await submit();
    const api = TestBed.runInInjectionContext(() => new BuyOutSurplusTransactionDetailUpdateControllerService());
    updateAmount.mockImplementation((id, payload) => api.updateBuyOutSurplusTransactionDetailAmount(id, payload));
    const button = harness.routeNativeElement.querySelector<HTMLButtonElement>('button[govukButton]');
    button.click();
    await settle();
    expect(button.disabled).toBe(true);
    button.click();
    expect(updateAmount).toHaveBeenCalledTimes(1);
    const http = TestBed.inject(HttpTestingController);
    http.expectOne((request) => request.url.endsWith('/transaction-details/13/update/amount')).flush(null);
    await settle();
    expect(text()).toContain('Current amount updated');
    http.verify();
  });

  it.each(['OPERATOR', 'SECTOR_USER'] as const)('blocks direct access for %s', async (roleType) => {
    TestBed.inject(AuthStore).setUserState({ roleType });
    await harness.navigateByUrl(formUrl);
    expect(TestBed.inject(Router).url).toBe(transactionUrl);
    expect(text()).not.toContain('Change amount');
    expect(updateAmount).not.toHaveBeenCalled();
  });
});
