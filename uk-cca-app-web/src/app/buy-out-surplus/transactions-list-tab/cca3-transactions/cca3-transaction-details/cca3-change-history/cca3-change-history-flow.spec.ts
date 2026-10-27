import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter, Router, withRouterConfig } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { of } from 'rxjs';

import { AuthStore } from '@netz/common/auth';

import { BuyOutAndSurplusFacilityTransactionViewService } from 'cca-api';

import { mockFacilityTransactionFee } from '../../../testing/cca3-facility-mock-data';
import { CCA3_TRANSACTIONS_ROUTES } from '../../cca3-transactions.routes';

describe('CCA3 facility change history flow', () => {
  const transactionUrl = '/buyout-surplus/cca3-transactions/1001';
  const historyUrl = `${transactionUrl}/facilities/AIC-F00040/change-history`;
  const facility = {
    id: 2001,
    facilityBusinessId: 'AIC-F00040',
    siteName: 'Facility name 40',
    paymentStatus: 'TERMINATED',
    hasHistory: true,
  };
  const getFacilities = vi.fn();
  let harness: RouterTestingHarness;
  let http: HttpTestingController;

  beforeEach(async () => {
    getFacilities.mockReset().mockReturnValue(of({ total: 1, transactionDetails: [facility] }));
    await TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [{ path: 'buyout-surplus/cca3-transactions', children: CCA3_TRANSACTIONS_ROUTES }],
          withRouterConfig({ paramsInheritanceStrategy: 'always' }),
        ),
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: BuyOutAndSurplusFacilityTransactionViewService,
          useValue: {
            getFacilityBuyOutSurplusTransaction: vi.fn().mockReturnValue(of(mockFacilityTransactionFee)),
            getFacilityBuyOutSurplusTransactionDetails: getFacilities,
          },
        },
      ],
    }).compileComponents();
    TestBed.inject(AuthStore).setUserState({ roleType: 'REGULATOR' });
    http = TestBed.inject(HttpTestingController);
    harness = await RouterTestingHarness.create();
  });

  afterEach(() => http.verify());

  it('clears table filters and pagination consistently from both facility action links', async () => {
    getFacilities.mockReturnValue(
      of({ total: 1, transactionDetails: [{ ...facility, paymentStatus: 'AWAITING_PAYMENT' }] }),
    );
    await harness.navigateByUrl(`${transactionUrl}?term=Cardiff&paymentStatus=AWAITING_PAYMENT&page=2&pageSize=10`);
    await harness.fixture.whenStable();
    harness.detectChanges();

    const actionLinks = Array.from(harness.routeNativeElement.querySelectorAll('a')).filter(
      (link) => link.textContent.includes('Change amount') || link.textContent.includes('View history'),
    );
    expect(actionLinks.map((link) => link.getAttribute('href'))).toEqual([
      `${transactionUrl}/facilities/AIC-F00040/change-amount`,
      historyUrl,
    ]);
  });

  it('opens a direct history link for a terminated facility and downloads evidence using its detail ID', async () => {
    await harness.navigateByUrl(historyUrl);
    expect(getFacilities).toHaveBeenCalledWith(1001, { term: 'AIC-F00040', pageNumber: 0, pageSize: 50 });
    http.expectOne('/api/v1.0/buy-out-surplus/transaction-details/2001/history').flush([
      {
        id: 1,
        submitter: 'Regulator user',
        submissionDate: '2027-05-02T15:20:00',
        payload: { type: 'AMOUNT_CHANGED', amount: '1250', evidenceFiles: { 'evidence-uuid': 'Evidence.pdf' } },
      },
    ]);
    harness.detectChanges();

    expect(harness.routeNativeElement.textContent).toContain('CCA080001');
    expect(harness.routeNativeElement.textContent).toContain('Facility name 40');
    const evidenceUrl = harness.routeNativeElement.querySelector('a').getAttribute('href');
    expect(evidenceUrl).toBe(
      `${transactionUrl}/facilities/AIC-F00040/file-evidences-download/2001/attachment/evidence-uuid`,
    );

    vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => undefined);
    await harness.navigateByUrl(evidenceUrl);
    const request = http.expectOne((req) => req.url.endsWith('/transaction-details/2001/evidence'));
    expect(request.request.params.get('fileEvidenceUuid')).toBe('evidence-uuid');
    request.flush({ token: 'download-token', tokenExpirationMinutes: 1 });
    harness.detectChanges();
    expect(harness.routeNativeElement.querySelector('a').getAttribute('href')).toBe(
      '/api/v1.0/file-evidences/download-token',
    );
  });

  it('redirects an unknown facility to the transaction without requesting unrelated history', async () => {
    getFacilities.mockReturnValue(of({ total: 0, transactionDetails: [] }));
    await harness.navigateByUrl(historyUrl);
    expect(TestBed.inject(Router).url).toBe(transactionUrl);
    http.expectNone((req) => req.url.endsWith('/history'));
  });

  it.each([
    historyUrl,
    `${transactionUrl}/facilities/AIC-F00040/file-evidences-download/2001/attachment/evidence-uuid`,
  ])('prevents non regulators from opening %s', async (url) => {
    TestBed.inject(AuthStore).setUserState({ roleType: 'OPERATOR' });
    await harness.navigateByUrl(url);
    expect(TestBed.inject(Router).url).toBe(transactionUrl);
    http.expectNone((req) => req.url.includes('/transaction-details/'));
  });
});
