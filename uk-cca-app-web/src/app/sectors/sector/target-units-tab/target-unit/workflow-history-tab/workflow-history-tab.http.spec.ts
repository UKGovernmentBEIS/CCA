import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { BuyOutSurplusFacilityAccountProcessingRequestMetadata, RequestDetailsDTO } from 'cca-api';

import { WorkflowHistoryTabComponent } from './workflow-history-tab.component';

describe('CCA3 TU history HTTP and rendering (H1–H4)', () => {
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([{ path: 'target-unit/:targetUnitId', component: WorkflowHistoryTabComponent }]),
      ],
    });
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('H4 sends both request types once and preserves the single selected checkbox and account criteria', async () => {
    const harness = await RouterTestingHarness.create(
      '/target-unit/41?requestTypes=BUY_OUT_SURPLUS_ACCOUNT_PROCESSING&requestStatuses=COMPLETED',
    );
    const request = http.expectOne((req) => req.method === 'POST');
    expect(request.request.body).toEqual({
      resourceType: 'ACCOUNT',
      resourceId: '41',
      historyCategory: 'UNA',
      requestTypes: ['BUY_OUT_SURPLUS_ACCOUNT_PROCESSING', 'BUY_OUT_SURPLUS_FACILITY_ACCOUNT_PROCESSING'],
      requestStatuses: ['COMPLETED'],
      pageNumber: 0,
      pageSize: 10,
    });
    request.flush({ requestDetails: [], total: 0 });
    harness.detectChanges();
    const root = harness.routeNativeElement;
    const labels = [...root.querySelectorAll('label')].filter(
      (label) => label.textContent.trim() === 'Buy-out and surplus',
    );
    expect(labels).toHaveLength(1);
    expect((root.querySelector(`#${labels[0].htmlFor}`) as HTMLInputElement).checked).toBe(true);
    expect(TestBed.inject(Router).url).not.toContain('BUY_OUT_SURPLUS_FACILITY_ACCOUNT_PROCESSING');
  });

  it.each([
    { metadata: { buyOutTransactionCodes: { TP8: 'B8', TP7: 'B7' } }, buyout: 'B7, B8', refund: undefined },
    { metadata: { refundTransactionCodes: { TP7: 'R7' } }, buyout: undefined, refund: 'R7' },
    {
      metadata: { buyOutTransactionCodes: { TP7: 'B7' }, refundTransactionCodes: { TP7: 'R7' } },
      buyout: 'B7',
      refund: 'R7',
    },
    { metadata: {}, buyout: undefined, refund: undefined },
  ] satisfies { metadata: BuyOutSurplusFacilityAccountProcessingRequestMetadata; buyout?: string; refund?: string }[])(
    'H1/H2/H3 renders supplied conditional transaction metadata: $metadata',
    async ({ metadata, buyout, refund }) => {
      const harness = await RouterTestingHarness.create('/target-unit/41');
      const cca2Metadata = {
        type: 'BUY_OUT_SURPLUS_ACCOUNT_PROCESSING',
        targetPeriodType: 'TP6',
        performanceDataReportVersion: 1,
        tpOutcome: 'TARGET_MET',
        transactionId: 'CCA060021',
      };
      const requestDetails: RequestDetailsDTO[] = [
        {
          id: 'RETURNED-HISTORICAL-ID',
          requestType: 'BUY_OUT_SURPLUS_FACILITY_ACCOUNT_PROCESSING',
          requestStatus: 'COMPLETED',
          creationDate: '2027-09-02',
          requestMetadata: { type: 'BUY_OUT_SURPLUS_FACILITY_ACCOUNT_PROCESSING', ...metadata },
        },
        {
          id: 'CCA2-ID',
          requestType: 'BUY_OUT_SURPLUS_ACCOUNT_PROCESSING',
          requestStatus: 'COMPLETED',
          creationDate: '2025-05-01',
          requestMetadata: cca2Metadata,
        },
      ];
      http.expectOne((req) => req.method === 'POST').flush({ requestDetails, total: 2 });
      harness.detectChanges();
      const [cca3, cca2] = harness.routeNativeElement.querySelectorAll('.search-results-list_item');
      const text = cca3.textContent.replace(/\s+/g, ' ').trim();
      expect(text).toContain('RETURNED-HISTORICAL-ID Buy-out and surplus');
      expect(text).toContain('Completed');
      expect(text).not.toContain('TPR Version');
      expect(text).not.toContain('[object Object]');
      expect(text.includes('Buy-out transaction ID(s):')).toBe(!!buyout);
      expect(text.includes('Refund transaction ID(s):')).toBe(!!refund);
      if (buyout) expect(text).toContain(`Buy-out transaction ID(s): ${buyout}`);
      if (refund) expect(text).toContain(`Refund transaction ID(s): ${refund}`);
      expect(cca3.querySelector('a').getAttribute('href')).toBe(
        '/target-unit/41/workflow-details/RETURNED-HISTORICAL-ID',
      );
      expect(cca2.textContent.replace(/\s+/g, ' ')).toContain('TPR Version: TP6-V1 (Target met)');
      expect(cca2.textContent).toContain('Transaction ID: CCA060021');
    },
  );
});
