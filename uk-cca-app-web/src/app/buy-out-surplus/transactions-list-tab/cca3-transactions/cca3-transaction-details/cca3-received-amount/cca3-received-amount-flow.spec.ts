import { HttpErrorResponse, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivationEnd, provideRouter, Router, withRouterConfig } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { of, throwError } from 'rxjs';

import { AuthStore } from '@netz/common/auth';

import {
  BuyOutAndSurplusFacilityTransactionViewService,
  BuyOutSurplusTransactionReceivedAmountHistoryDTO,
  BuyOutSurplusTransactionUpdateControllerService,
  FacilityBuyOutSurplusTransactionDTO,
} from 'cca-api';

import { PendingRequestInterceptor } from '../../../../../interceptors/pending-request.interceptor';
import { mockFacilityTransactionFee } from '../../../testing/cca3-facility-mock-data';
import { CCA3_TRANSACTIONS_ROUTES } from '../../cca3-transactions.routes';
import { MAX_RECEIVED_AMOUNT_MESSAGE, NEGATIVE_RECEIVED_AMOUNT_MESSAGE } from './cca3-received-amount.validators';
import { CCA3_RECEIVED_AMOUNT_FORM, Cca3ReceivedAmountForm } from './cca3-received-amount-form.provider';

describe('CCA3 received amount flow', () => {
  const transactionUrl = '/buyout-surplus/cca3-transactions/10';
  const formUrl = `${transactionUrl}/received-amount`;
  let harness: RouterTestingHarness;
  let form: Cca3ReceivedAmountForm;
  let transaction: FacilityBuyOutSurplusTransactionDTO;
  let history: BuyOutSurplusTransactionReceivedAmountHistoryDTO[];
  const getTransaction = vi.fn();
  const getHistory = vi.fn();
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
  const openForm = async () => {
    await harness.navigateByUrl(formUrl);
    form = harness.routeDebugElement.injector.get(CCA3_RECEIVED_AMOUNT_FORM);
    await settle();
  };
  const enterDraft = () =>
    form.patchValue({
      adjustmentAmount: '100.25',
      paymentDate: new Date(Date.UTC(2025, 5, 1)),
      comments: 'Bank receipt confirmed',
    });

  beforeEach(async () => {
    transaction = {
      ...mockFacilityTransactionFee,
      id: 10,
      transactionCode: 'CCA070021',
      chargeType: 'FEE',
      expectedAmount: '1000',
      receivedAmount: '500',
    };
    history = [
      {
        id: 2,
        adjustmentAmount: '-250',
        submitter: 'Another regulator',
        submissionDate: '2025-05-12T15:25:00',
        comments: 'Incorrect payment corrected',
        evidenceFiles: {},
      },
      {
        id: 1,
        adjustmentAmount: '500',
        submitter: 'Regulator user',
        submissionDate: '2025-05-11T15:25:00',
        paymentDate: '2025-05-01',
        comments: 'Initial receipt',
        evidenceFiles: { 'test-file': 'receipt.pdf' },
      },
    ];
    getTransaction.mockReset().mockImplementation(() => of({ ...transaction }));
    getHistory.mockReset().mockImplementation(() => of(history));
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
            getBuyOutSurplusTransactionReceivedAmountHistory: getHistory,
            getFacilityBuyOutSurplusTransactionDetails: vi
              .fn()
              .mockReturnValue(of({ total: 0, transactionDetails: [] })),
          },
        },
        {
          provide: BuyOutSurplusTransactionUpdateControllerService,
          useValue: { updateBuyOutSurplusTransactionReceivedAmount: updateAmount },
        },
      ],
    }).compileComponents();
    TestBed.inject(AuthStore).setUserState({ roleType: 'REGULATOR' });
    harness = await RouterTestingHarness.create();
  });

  it('opens from the transaction Change link and validates before review', async () => {
    await harness.navigateByUrl(transactionUrl);
    const change = harness.routeNativeElement.querySelector<HTMLAnchorElement>('a[href$="/received-amount"]');
    expect(change).not.toBeNull();
    change.click();
    await settle();
    expect(text()).toContain('1,000.00');
    expect(text()).toContain('500.00');
    expect(text()).toContain('No files have been uploaded yet');
    expect(
      harness.routeNativeElement.querySelector<HTMLSelectElement>('select').selectedOptions[0].textContent.trim(),
    ).toBe('Add');
    await submit();
    expect(text()).toContain('Enter a positive value');
    expect(text()).toContain('Enter a date');
    expect(text()).toContain('Enter a comment');
    expect(updateAmount).not.toHaveBeenCalled();
  });

  it('reviews and saves a positive signed adjustment and refetches totals on return', async () => {
    await openForm();
    enterDraft();
    await submit();
    expect(TestBed.inject(Router).url).toBe(`${formUrl}/check-your-answers`);
    expect(text()).toContain('600.25');
    expect(updateAmount).not.toHaveBeenCalled();
    await submit();
    expect(updateAmount).toHaveBeenCalledExactlyOnceWith(10, {
      adjustmentAmount: '100.25',
      paymentDate: '2025-06-01',
      comments: 'Bank receipt confirmed',
      evidenceFiles: {},
    });
    expect(text()).toContain('Received amount updated');
    expect(text()).toContain('Return to: Buy-out MoA CCA070021');
    transaction.receivedAmount = '600.25';
    getTransaction.mockClear();
    await harness.navigateByUrl(transactionUrl);
    expect(getTransaction).toHaveBeenCalledWith(10);
    expect(text()).toContain('600.25');
  });

  it('switches to Subtract in the UI and submits without a date', async () => {
    await openForm();
    const select = harness.routeNativeElement.querySelector<HTMLSelectElement>('select');
    select.selectedIndex = 1;
    select.dispatchEvent(new Event('change', { bubbles: true }));
    await settle();
    expect(text()).toContain('bank account (optional)');
    form.patchValue({ adjustmentAmount: '500', comments: 'Correction' });
    await submit();
    expect(text()).toContain('Not provided');
    expect(TestBed.inject(Router).url).toBe(`${formUrl}/check-your-answers`);
    await submit();
    expect(updateAmount).toHaveBeenCalledWith(10, {
      adjustmentAmount: '-500.00',
      comments: 'Correction',
      evidenceFiles: {},
    });
  });

  it('blocks a partial or impossible optional date through the horizontal date inputs', async () => {
    await openForm();
    form.patchValue({ changeType: 'subtract', adjustmentAmount: '100', comments: 'Correction' });
    const inputs = harness.routeNativeElement.querySelectorAll<HTMLInputElement>('[govuk-date-input] input');
    inputs[0].value = '31';
    inputs[0].dispatchEvent(new Event('input', { bubbles: true }));
    await submit();
    expect(text()).toContain('Enter a full date');
    inputs[1].value = '2';
    inputs[1].dispatchEvent(new Event('input', { bubbles: true }));
    inputs[2].value = '2025';
    inputs[2].dispatchEvent(new Event('input', { bubbles: true }));
    await submit();
    expect(text()).toContain('Enter a real date');
    expect(TestBed.inject(Router).url).toBe(formUrl);
    expect(updateAmount).not.toHaveBeenCalled();
  });

  it('preserves a large decimal entered in the UI through review and submission', async () => {
    transaction.receivedAmount = '0';
    await openForm();
    enterDraft();
    const input = harness.routeNativeElement.querySelector<HTMLInputElement>('#adjustmentAmount');
    input.value = '999999999999999999.99';
    input.dispatchEvent(new Event('input', { bubbles: true }));
    await submit();
    expect(text()).toContain('999,999,999,999,999,999.99');
    await submit();
    expect(updateAmount).toHaveBeenCalledWith(
      10,
      expect.objectContaining({ adjustmentAmount: '999999999999999999.99' }),
    );
  });

  it('blocks subtraction below zero in the form', async () => {
    await openForm();
    form.patchValue({ changeType: 'subtract', adjustmentAmount: '500.01', comments: 'Correction' });
    await submit();
    expect(text()).toContain(NEGATIVE_RECEIVED_AMOUNT_MESSAGE);
    expect(TestBed.inject(Router).url).toBe(formUrl);
    expect(updateAmount).not.toHaveBeenCalled();
  });

  it('blocks addition beyond the total limit and allows correcting the amount', async () => {
    transaction.receivedAmount = '999999999999999999.98';
    await openForm();
    enterDraft();
    form.controls.adjustmentAmount.setValue('0.02');
    await submit();
    expect(text()).toContain(MAX_RECEIVED_AMOUNT_MESSAGE);
    expect(TestBed.inject(Router).url).toBe(formUrl);
    expect(updateAmount).not.toHaveBeenCalled();

    form.controls.adjustmentAmount.setValue('0.01');
    await submit();
    expect(text()).toContain('999,999,999,999,999,999.99');
    expect(harness.routeNativeElement.querySelector('form')).toBeNull();
    await submit();
    expect(updateAmount).toHaveBeenCalledWith(10, expect.objectContaining({ adjustmentAmount: '0.01' }));
  });

  it('requires a date again when switching from Subtract to Add', async () => {
    await openForm();
    form.patchValue({ changeType: 'subtract', adjustmentAmount: '100', comments: 'Correction' });
    await settle();
    form.controls.changeType.setValue('add');
    await submit();
    expect(text()).toContain('Enter a date');
    expect(TestBed.inject(Router).url).toBe(formUrl);
  });

  it('allows fixing an incomplete date after visiting history', async () => {
    await openForm();
    const day = harness.routeNativeElement.querySelector<HTMLInputElement>('[govuk-date-input] input');
    day.value = '1';
    day.dispatchEvent(new Event('input', { bubbles: true }));
    await harness.navigateByUrl(`${formUrl}/history-details/1`);
    await harness.navigateByUrl(formUrl);
    enterDraft();
    await submit();
    expect(TestBed.inject(Router).url).toBe(`${formUrl}/check-your-answers`);
  });

  it('prevents duplicate confirmation while the API request is pending', async () => {
    await openForm();
    enterDraft();
    await submit();
    const api = TestBed.runInInjectionContext(() => new BuyOutSurplusTransactionUpdateControllerService());
    updateAmount.mockImplementation((id, payload) => api.updateBuyOutSurplusTransactionReceivedAmount(id, payload));
    const button = harness.routeNativeElement.querySelector<HTMLButtonElement>('button[govukButton]');
    button.click();
    await settle();
    expect(button.disabled).toBe(true);
    button.click();
    expect(updateAmount).toHaveBeenCalledTimes(1);
    const http = TestBed.inject(HttpTestingController);
    http.expectOne((req) => req.url.endsWith('/transactions/10/update/received-amount')).flush(null);
    await settle();
    expect(text()).toContain('Received amount updated');
    http.verify();
  });

  it('retains dates, comments and evidence when editing reviewed answers', async () => {
    await openForm();
    enterDraft();
    form.controls.evidenceFiles.setValue([{ uuid: 'test-file', file: { name: 'receipt.pdf' } }]);
    await submit();
    expect(harness.routeNativeElement.querySelector('a[href$="/test-file"]').getAttribute('href')).toBe(
      `${transactionUrl}/file-evidences-download/attachment/test-file`,
    );
    await harness.navigateByUrl(formUrl);
    expect(form.value.comments).toBe('Bank receipt confirmed');
    expect(form.value.paymentDate).toEqual(new Date(Date.UTC(2025, 5, 1)));
    await submit();
    await submit();
    expect(updateAmount).toHaveBeenCalledWith(
      10,
      expect.objectContaining({ evidenceFiles: { 'test-file': 'receipt.pdf' } }),
    );
  });

  it.each([
    ['BS1004', 'You cannot make an adjustment while a Batch Run is in progress.'],
    ['BS1005', NEGATIVE_RECEIVED_AMOUNT_MESSAGE],
    ['BS1006', 'You can only update the received amount for a buy-out transaction.'],
  ])('preserves the draft and reports %s on the review page', async (code, message) => {
    await openForm();
    enterDraft();
    await submit();
    updateAmount.mockReturnValueOnce(throwError(() => new HttpErrorResponse({ status: 400, error: { code } })));
    await submit();
    expect(TestBed.inject(Router).url).toBe(`${formUrl}/check-your-answers`);
    expect(text()).toContain(message);
    expect(text()).not.toContain('Received amount updated');
    expect(form.value.adjustmentAmount).toBe('100.25');
    expect(form.value.comments).toBe('Bank receipt confirmed');
    await submit();
    expect(text()).toContain('Received amount updated');
  });

  it('allows editing the draft after a rejected adjustment without carrying the error to the form', async () => {
    await openForm();
    enterDraft();
    await submit();
    updateAmount.mockReturnValueOnce(
      throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'BS1005' } })),
    );
    await submit();

    const change = harness.routeNativeElement.querySelector<HTMLAnchorElement>('.govuk-summary-list__actions a');
    change.click();
    await settle();
    expect(TestBed.inject(Router).url).toBe(formUrl);
    expect(text()).not.toContain(NEGATIVE_RECEIVED_AMOUNT_MESSAGE);
    expect(form.value.adjustmentAmount).toBe('100.25');

    form.controls.adjustmentAmount.setValue('50');
    await submit();
    expect(text()).toContain('550.00');
    expect(text()).not.toContain(NEGATIVE_RECEIVED_AMOUNT_MESSAGE);
    await submit();
    expect(updateAmount).toHaveBeenLastCalledWith(10, expect.objectContaining({ adjustmentAmount: '50.00' }));
    expect(text()).toContain('Received amount updated');
  });

  it('preserves the backend newest-first history order and opens added and subtracted details directly', async () => {
    await openForm();
    const titles = Array.from(harness.routeNativeElement.querySelectorAll('govuk-details h3')).map(
      (el) => el.textContent,
    );
    expect(titles[0]).toContain('Amount subtracted 250.00');
    expect(titles[1]).toContain('Amount added 500.00');
    await harness.navigateByUrl(`${formUrl}/history-details/2`);
    expect(text()).toContain('Amount subtracted 250.00 GBP by "Another regulator"');
    expect(text()).toContain('Not provided');
    await harness.navigateByUrl(formUrl);
    await harness.navigateByUrl(`${formUrl}/history-details/1`);
    expect(text()).toContain('Amount added 500.00 GBP by "Regulator user"');
    expect(text()).toContain('1 May 2025');
    expect(text()).toContain('Initial receipt');
    expect(text()).toContain('receipt.pdf');
  });

  it('loads a history deep link and downloads evidence using the transaction endpoint', async () => {
    await harness.navigateByUrl(`${formUrl}/history-details/1`);
    expect(getHistory).toHaveBeenCalledWith(10);
    const evidenceUrl = harness.routeNativeElement.querySelector('a[href$="/test-file"]').getAttribute('href');
    expect(evidenceUrl).toBe(`${transactionUrl}/file-evidences-download/attachment/test-file`);
    vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => undefined);
    await harness.navigateByUrl(evidenceUrl);
    const http = TestBed.inject(HttpTestingController);
    const request = http.expectOne((req) => req.url.endsWith('/transactions/10/evidence'));
    expect(request.request.params.get('fileEvidenceUuid')).toBe('test-file');
    request.flush({ token: 'download-token', tokenExpirationMinutes: 1 });
    await settle();
    expect(harness.routeNativeElement.querySelector('a').getAttribute('href')).toBe(
      '/api/v1.0/file-evidences/download-token',
    );
    http.verify();
  });

  it('redirects invalid history IDs and review without a draft to the form', async () => {
    await harness.navigateByUrl(`${formUrl}/history-details/999`);
    expect(TestBed.inject(Router).url).toBe(formUrl);
    await harness.navigateByUrl(`${formUrl}/check-your-answers`);
    await settle();
    expect(TestBed.inject(Router).url).toBe(formUrl);
  });

  it('redirects a directly opened review page without activating it when there is no draft', async () => {
    const reviewActivated = vi.fn();
    const subscription = TestBed.inject(Router).events.subscribe((event) => {
      if (event instanceof ActivationEnd && event.snapshot.routeConfig?.path === 'check-your-answers') {
        reviewActivated();
      }
    });

    await harness.navigateByUrl(`${formUrl}/check-your-answers`);
    await settle();
    expect(TestBed.inject(Router).url).toBe(formUrl);
    expect(reviewActivated).not.toHaveBeenCalled();
    expect(updateAmount).not.toHaveBeenCalled();
    subscription.unsubscribe();
  });

  it('clears an abandoned draft and blocks review when reopening the flow', async () => {
    await openForm();
    enterDraft();
    await harness.navigateByUrl(transactionUrl);
    await harness.navigateByUrl(`${formUrl}/check-your-answers`);
    expect(TestBed.inject(Router).url).toBe(formUrl);
    await openForm();
    expect(form.value.adjustmentAmount).toBe('');
    expect(form.value.comments).toBe('');
  });

  it('permits adjustment for terminated transactions', async () => {
    transaction.paymentStatus = 'TERMINATED';
    await openForm();
    enterDraft();
    await submit();
    await submit();
    expect(text()).toContain('Received amount updated');
  });

  it.each(['OPERATOR', 'SECTOR_USER'] as const)('blocks direct editing and history access for %s', async (roleType) => {
    TestBed.inject(AuthStore).setUserState({ roleType });
    await harness.navigateByUrl(formUrl);
    expect(TestBed.inject(Router).url).toBe(transactionUrl);
    await harness.navigateByUrl(`${formUrl}/history-details/1`);
    expect(TestBed.inject(Router).url).toBe(transactionUrl);
    expect(getHistory).not.toHaveBeenCalled();
    expect(updateAmount).not.toHaveBeenCalled();
  });

  it('blocks received amount adjustment for refunds', async () => {
    transaction.chargeType = 'REFUND';
    await harness.navigateByUrl(formUrl);
    expect(TestBed.inject(Router).url).toBe(transactionUrl);
    expect(getHistory).not.toHaveBeenCalled();
  });
});
