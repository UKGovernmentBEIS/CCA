import { Location } from '@angular/common';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideLocationMocks } from '@angular/common/testing';
import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { NavigationEnd, provideRouter, Router, RouterOutlet } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { filter, firstValueFrom, take } from 'rxjs';

import { BackLinkComponent } from '@netz/common/navigation';
import { RequestActionStore } from '@netz/common/store';
import { getByLabelText, getByRole } from '@testing';

import { RequestActionDTO } from 'cca-api';

import { TIMELINE_ROUTES } from '../../timeline.routes';
import { mockAccountAction, mockAccountAction126, mockAccountPayload, mockFacility } from '../testing/mock-data';
import { FacilityDetailsComponent } from './facility-details.component';

@Component({
  selector: 'cca-facility-route-test-shell',
  template: '<netz-back-link /><router-outlet />',
  imports: [BackLinkComponent, RouterOutlet],
})
class ShellComponent {}

@Component({ selector: 'cca-facility-route-test-destination', template: '<p>Workflow or error page</p>' })
class DestinationComponent {}

describe('CCA3 facility production routes with historical HTTP data', () => {
  let harness: RouterTestingHarness;
  let router: Router;
  let http: HttpTestingController;
  const listUrl = '/timeline/3293';
  const facilityPath = '/cca3-buyout-surplus/facility/AIC-F00064/TP8';
  const query = '?search=Historical&targetPeriod=TP8&result=TARGET_NOT_MET&page=2&pageSize=20';

  const facilities = Array.from({ length: 25 }, (_, index) =>
    mockFacility({
      facilityBusinessId: `AIC-F${String(44 + index).padStart(5, '0')}`,
      siteName: `Historical Site ${index}`,
      targetPeriod: 'TP8',
      invoicedBuyOutFee: '0.0000000',
      chargeType: undefined,
      transactionCode: undefined,
    }),
  );
  const action = mockAccountAction(
    mockAccountPayload({
      facilities: [
        ...facilities,
        mockFacility({ facilityBusinessId: 'AIC-F00064', targetPeriod: 'TP7', siteName: 'Earlier site name' }),
      ],
    }),
  );

  beforeEach(async () => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideLocationMocks(),
        provideRouter([
          {
            path: '',
            component: ShellComponent,
            children: [
              {
                path: 'timeline',
                children: TIMELINE_ROUTES,
              },
              { path: 'workflow', component: DestinationComponent },
              { path: 'dashboard', component: DestinationComponent },
              { path: 'error/404', component: DestinationComponent },
              { path: 'refused', component: DestinationComponent, canActivate: [() => false] },
              {
                path: 'failed',
                component: DestinationComponent,
                canActivate: [
                  () => {
                    throw new Error('Navigation failed');
                  },
                ],
              },
            ],
          },
        ]),
      ],
    });
    harness = await RouterTestingHarness.create();
    router = TestBed.inject(Router);
    router.setUpLocationChangeListener();
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  async function load(url: string, response: RequestActionDTO = action, status?: number) {
    const navigation = harness.navigateByUrl(url);
    const request = await vi.waitFor(() =>
      http.expectOne((req) => req.url.endsWith(`/request-actions/${response.id}`)),
    );
    expect(request.request.method).toBe('GET');
    if (status) request.flush({}, { status, statusText: 'Error' });
    else request.flush(response);
    await navigation;
    await harness.fixture.whenStable();
  }

  async function history(direction: 'back' | 'forward') {
    const navigation = firstValueFrom(
      router.events.pipe(
        filter((event) => event instanceof NavigationEnd),
        take(1),
      ),
    );
    TestBed.inject(Location)[direction]();
    await navigation;
    harness.detectChanges();
    await harness.fixture.whenStable();
  }

  function expectFilters() {
    expect((getByLabelText('Facility ID or site name') as HTMLInputElement).value).toBe('Historical');
    expect((getByLabelText('Target period') as HTMLSelectElement).value).toContain('TP8');
    expect((getByLabelText('TPR Result') as HTMLSelectElement).value).toContain('TARGET_NOT_MET');
    expect((getByLabelText('Rows per page') as HTMLSelectElement).value).toBe('20');
    expect(harness.routeNativeElement.querySelectorAll('tbody tr')).toHaveLength(5);
  }

  function summaryValue(label: string): string {
    const term = Array.from(harness.routeNativeElement.querySelectorAll('dt')).find(
      (element) => element.textContent.trim() === label,
    );
    expect(term, label).toBeTruthy();
    return term.nextElementSibling.textContent.trim();
  }

  it('opens action 126 values in the current API shape, preserving gross, paid and net rows', async () => {
    await load('/timeline/126', mockAccountAction126);
    getByRole('link', { name: 'test facility 2' }).click();
    await harness.fixture.whenStable();
    expect(router.url).toBe('/timeline/126/cca3-buyout-surplus/facility/ADS_1-F00001/TP8');
    expect(summaryValue('Type')).toBe('Secondary');
    expect(summaryValue('TPR Version')).toBe('TP8-V3 (Target not met)');
    expect(summaryValue('Amount above target (tCO₂e)')).toBe('100');
    expect(summaryValue('Cost (GBP / tCO₂e)')).toBe('11.00');
    expect(summaryValue('Total buy-out fee (GBP)')).toBe('1,100.00');
    expect(summaryValue('Previous buy-out fee received (GBP)')).toBe('825.00');
    expect(summaryValue('Previous surplus used/re-banked (tCO₂e)')).toBe('25');
    expect(summaryValue('Secondary buy-out fee (GBP)')).toBe('275.00');
    expect(summaryValue('Transaction ID')).toBe('CCA080012');
    http.expectNone(() => true);
  });

  it('opens a fully-offset facility without an invoice, keeping its zero fee and missing cost', async () => {
    const response = mockAccountAction(
      mockAccountPayload({
        summary: [],
        facilities: [
          mockFacility({
            targetPeriod: 'TP8',
            submissionType: 'SECONDARY',
            primaryRulesApplied: true,
            buyOutRequired: '40',
            surplusUsed: '40',
            previousPaidFees: '0',
            previousSurplusUsed: '0',
            previousSurplusGained: '0',
            invoicedBuyOutFee: '0',
            chargeType: null,
            transactionCode: null,
          }),
        ],
      }),
    );
    await load(listUrl, response);
    getByRole('link', { name: 'West London Site' }).click();
    await harness.fixture.whenStable();

    expect(router.url).toBe(`${listUrl}${facilityPath}`);
    expect(summaryValue('Type')).toBe('Primary');
    expect(summaryValue('Surplus used (tCO₂e)')).toBe('40');
    expect(summaryValue('Total buy-out fee (GBP)')).toBe('0.00');
    expect(summaryValue('Cost (GBP / tCO₂e)')).toBe('');
    expect(harness.routeNativeElement.textContent).not.toContain('Transaction ID');
    http.expectNone(() => true);
  });

  it.each([
    {
      name: 'Primary surplus for a Secondary report',
      values: {
        submissionType: 'SECONDARY',
        performanceOutcome: 'TARGET_MET',
        chargeType: null,
        surplusGained: '50',
      },
      type: 'Primary',
      section: 'Surplus',
      label: 'Surplus gained (tCO₂e)',
      value: '50',
    },
    {
      name: 'Primary without surplus use for a Secondary report',
      values: { submissionType: 'SECONDARY', surplusUsed: '0' },
      type: 'Primary',
      section: 'Buy-out',
      label: 'Surplus used (tCO₂e)',
      value: '0',
    },
    {
      name: 'Primary report refunded later',
      values: { chargeType: 'REFUND', previousPaidFees: '1000' },
      type: 'Primary',
      section: 'Refund',
      label: 'Total buy-out fee (GBP)',
      value: '798.00',
    },
    {
      name: 'Secondary first surplus for a Primary report',
      values: {
        submissionType: 'PRIMARY',
        performanceOutcome: 'TARGET_MET',
        chargeType: null,
        previousSurplusGained: '0',
        surplusGained: '50',
      },
      type: 'Secondary',
      section: 'Surplus',
      label: 'Previous surplus gained/removed (tCO₂e)',
      value: '0',
    },
    {
      name: 'Secondary zero history for a Primary report',
      values: {
        submissionType: 'PRIMARY',
        previousSurplusGained: '0',
        previousSurplusUsed: '0',
        previousPaidFees: '0',
      },
      type: 'Secondary',
      section: 'Buy-out',
      label: 'Previous buy-out fee received (GBP)',
      value: '0.00',
    },
    {
      name: 'Surplus rebanked without refund',
      values: { performanceOutcome: 'TARGET_MET', chargeType: null, previousSurplusUsed: '40', surplusGained: '10' },
      type: 'Secondary',
      section: 'Surplus',
      label: 'Previous surplus used/re-banked (tCO₂e)',
      value: '40',
    },
    {
      name: 'Primary positive fee',
      values: { surplusUsed: '12' },
      type: 'Primary',
      section: 'Buy-out',
      label: 'Total buy-out fee (GBP)',
      value: '202.00',
    },
    {
      name: 'Secondary revised surplus',
      values: { performanceOutcome: 'TARGET_MET', chargeType: null, previousSurplusGained: '50', surplusGained: '60' },
      type: 'Secondary',
      section: 'Surplus',
      label: 'Previous surplus gained/removed (tCO₂e)',
      value: '50',
    },
    {
      name: 'Target met refund',
      values: {
        performanceOutcome: 'TARGET_MET',
        chargeType: 'REFUND',
        previousPaidFees: '202',
        surplusGained: '0.0000',
      },
      type: 'Secondary',
      section: 'Refund',
      label: 'Surplus gained (tCO₂e)',
      value: '0.0000',
    },
    {
      name: 'Buy-out after surplus',
      values: { previousSurplusGained: '50' },
      type: 'Secondary',
      section: 'Buy-out',
      label: 'Previous surplus gained/removed (tCO₂e)',
      value: '50',
    },
    {
      name: 'Target not met refund',
      values: { chargeType: 'REFUND', previousPaidFees: '1000' },
      type: 'Secondary',
      section: 'Refund',
      label: 'Overpayment buy-out amount (GBP)',
      value: '202.00',
    },
    {
      name: 'Exactly paid Secondary',
      values: { previousSurplusUsed: '12', previousPaidFees: '202', invoicedBuyOutFee: '0.0000000', chargeType: null },
      type: 'Secondary',
      section: 'Buy-out',
      label: 'Secondary buy-out fee (GBP)',
      value: '0.00',
    },
  ] as const)('renders $name through the production HTTP route', async ({ values, type, section, label, value }) => {
    const record = mockFacility({
      submissionType: type === 'Primary' ? 'PRIMARY' : 'SECONDARY',
      primaryRulesApplied: type === 'Primary',
      surplusUsed: '0',
      previousPaidFees: '0',
      previousSurplusGained: '0',
      previousSurplusUsed: '0',
      ...values,
    });
    await load(
      `${listUrl}/cca3-buyout-surplus/facility/${record.facilityBusinessId}/${record.targetPeriod}`,
      mockAccountAction(mockAccountPayload({ facilities: [record] })),
    );
    expect(summaryValue('Type')).toBe(type);
    expect(getByRole('heading', { name: section })).toBeTruthy();
    expect(summaryValue(label)).toBe(value);
    if (section !== 'Refund') {
      expect(harness.routeNativeElement.querySelector('details') !== null).toBe(type === 'Secondary');
    }
    if (label === 'Secondary buy-out fee (GBP)') {
      expect(
        Array.from(harness.routeNativeElement.querySelectorAll('dt')).map((element) => element.textContent.trim()),
      ).not.toContain('Transaction ID');
    }
    http.expectNone(() => true);
  });

  it('links all valid final facilities even when previous amounts are zero or missing', async () => {
    const records = [
      mockFacility({ facilityBusinessId: 'AIC-F00001', siteName: 'Supported fee' }),
      mockFacility({
        facilityBusinessId: 'AIC-F00002',
        siteName: 'Ambiguous fee',
        surplusUsed: '0',
        previousPaidFees: '825',
      }),
      mockFacility({
        facilityBusinessId: 'AIC-F00003',
        siteName: 'Unknown surplus type',
        performanceOutcome: 'TARGET_MET',
        surplusUsed: '0',
        surplusGained: '50',
        chargeType: null,
        transactionCode: null,
      }),
    ];
    await load(listUrl, mockAccountAction(mockAccountPayload({ facilities: records })));
    const rows = harness.routeNativeElement.querySelectorAll('tbody tr');
    expect(rows).toHaveLength(3);
    expect(rows[0].querySelector('a').textContent).toBe('Supported fee');
    expect(rows[1].querySelector('a').textContent).toBe('Ambiguous fee');
    expect(rows[1].textContent).toContain('202');
    expect(rows[2].querySelector('a').textContent).toBe('Unknown surplus type');
    expect(rows[2].textContent).toContain('50');
    await harness.navigateByUrl(`${listUrl}/cca3-buyout-surplus/facility/AIC-F00002/TP7`);
    expect(summaryValue('Type')).toBe('Primary');
    expect(summaryValue('Total buy-out fee (GBP)')).toBe('1,027.00');
    await harness.navigateByUrl(`${listUrl}/cca3-buyout-surplus/facility/AIC-F00003/TP7`);
    expect(summaryValue('Type')).toBe('Primary');
    expect(summaryValue('Surplus gained (tCO₂e)')).toBe('50');
    expect(harness.routeNativeElement.textContent).not.toContain('Date banked');
  });

  it('does not create links for duplicate historical identities', async () => {
    await load(listUrl, mockAccountAction(mockAccountPayload({ facilities: [facilities[0], facilities[0]] })));
    expect(harness.routeNativeElement.querySelectorAll('tbody tr')).toHaveLength(2);
    expect(harness.routeNativeElement.querySelectorAll('tbody a')).toHaveLength(0);
  });

  it('preserves filters, page and page size through Site name -> explicit Back without more HTTP requests', async () => {
    await load(listUrl + query);
    expectFilters();
    const link = getByRole('link', { name: 'Historical Site 20' }) as HTMLAnchorElement;
    expect(link.getAttribute('href')).toBe(listUrl + facilityPath + query);
    expect(harness.routeNativeElement.querySelectorAll('thead th')).toHaveLength(8);
    link.click();
    await harness.fixture.whenStable();
    expect(router.url).toBe(listUrl + facilityPath + query);
    expect(document.title).toBe('Historical Site 20');
    expect(summaryValue('Total buy-out fee (GBP)')).toBe('0.00');
    const back = getByRole('link', { name: 'Back' }) as HTMLAnchorElement;
    expect(back.getAttribute('href')).toBe(listUrl + query);
    back.click();
    await harness.fixture.whenStable();
    expect(router.url).toBe(listUrl + query);
    expectFilters();
    http.expectNone(() => true);
  });

  it('supports explicit Back from a fresh deep link without browser history', async () => {
    await load(listUrl + facilityPath + query);
    getByRole('link', { name: 'Back' }).click();
    await harness.fixture.whenStable();
    expect(router.url).toBe(listUrl + query);
    expectFilters();
    http.expectNone(() => true);
  });

  it('restores the list with browser Back and the same facility with Forward', async () => {
    await load(listUrl + query);
    getByRole('link', { name: 'Historical Site 20' }).click();
    await harness.fixture.whenStable();
    await history('back');
    expectFilters();
    await history('forward');
    expect(router.url).toBe(listUrl + facilityPath + query);
    expect(getByRole('heading', { name: 'Historical Site 20' })).toBeTruthy();
    http.expectNone(() => true);
  });

  it('changes facility, TP and action reactively when the details component is reused', async () => {
    await load(listUrl + facilityPath);
    const component = harness.fixture.debugElement.query(By.directive(FacilityDetailsComponent)).componentInstance;
    await harness.navigateByUrl(listUrl + facilityPath.replace('TP8', 'TP7'));
    expect(getByRole('heading', { name: 'Earlier site name' })).toBeTruthy();
    expect(harness.routeNativeElement.textContent).toContain('TP7-V1');
    await harness.navigateByUrl(listUrl + facilityPath.replace('F00064', 'F00065'));
    expect(getByRole('heading', { name: 'Historical Site 21' })).toBeTruthy();
    await load(
      '/timeline/3294' + facilityPath,
      mockAccountAction(mockAccountPayload({ runId: 'BOS-TP9001', facilities }), 3294),
    );
    expect(harness.routeNativeElement.textContent).toContain('BOS-TP9001');
    expect(harness.fixture.debugElement.query(By.directive(FacilityDetailsComponent)).componentInstance).toBe(
      component,
    );
  });

  it('starts fresh when entering an event without query parameters from the workflow', async () => {
    await load(listUrl + facilityPath + query);
    await harness.navigateByUrl('/workflow');
    expect(TestBed.inject(RequestActionStore).state.action).toBeNull();
    await load('/timeline/3294', mockAccountAction(mockAccountPayload({ facilities }), 3294));
    expect((getByLabelText('Facility ID or site name') as HTMLInputElement).value).toBe('');
    expect((getByLabelText('Rows per page') as HTMLSelectElement).value).toBe('10');
    expect(harness.routeNativeElement.querySelectorAll('tbody tr')).toHaveLength(10);
  });

  it('retains the current action and screen after cancelled or failed navigation', async () => {
    await load(listUrl + facilityPath + query);
    const current = TestBed.inject(RequestActionStore).state.action;
    expect(await router.navigateByUrl('/refused')).toBe(false);
    await expect(router.navigateByUrl('/failed')).rejects.toThrow('Navigation failed');
    expect(router.url).toBe(listUrl + facilityPath + query);
    expect(TestBed.inject(RequestActionStore).state.action).toBe(current);
    expect(getByRole('heading', { name: 'Historical Site 20' })).toBeTruthy();
  });

  it.each(['unknown/TP8', 'AIC-F00064/8', 'AIC-F00064/TP9'])(
    'redirects invalid identity %s to 404',
    async (identity) => {
      await load(`${listUrl}/cca3-buyout-surplus/facility/${identity}`);
      expect(router.url).toBe('/error/404');
      expect(harness.routeNativeElement.querySelector('cca-buyout-surplus-facility-details')).toBeNull();
    },
  );

  it.each([
    mockAccountAction(mockAccountPayload({ facilities: [facilities[20], facilities[20]] })),
    { ...action, type: 'TP6_SURPLUS_ACCOUNT_PROCESSING_SUBMITTED' } as RequestActionDTO,
    { ...action, payload: { payloadType: 'OTHER_PAYLOAD' } },
  ])('redirects an ambiguous or incompatible historical action to 404', async (response) => {
    await load(listUrl + facilityPath, response);
    expect(router.url).toBe('/error/404');
  });

  it.each([403, 404, 500])('preserves existing action-fetch handling for HTTP %s', async (status) => {
    await load(listUrl + facilityPath, action, status);
    expect(router.url).toBe('/dashboard');
    expect(TestBed.inject(RequestActionStore).state.action).toBeNull();
  });
});
