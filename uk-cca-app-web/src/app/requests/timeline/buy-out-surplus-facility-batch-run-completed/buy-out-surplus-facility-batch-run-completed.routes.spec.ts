import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter, Router, withRouterConfig } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { of } from 'rxjs';

import { AuthStore } from '@netz/common/auth';
import { getByRole, getSummaryListData } from '@testing';

import {
  BuyOutSurplusFacilityRunCompletedRequestActionPayload,
  RequestActionDTO,
  RequestActionsService,
  RequestsService,
} from 'cca-api';

import { BUY_OUT_SURPLUS_WORKFLOW_HISTORY_ROUTES } from '../../../buy-out-surplus/workflow-history/workflow-history.routes';
import {
  buyOutSurplusFacilityBatchRunCompletedMock,
  buyOutSurplusFacilityBatchRunCompletedRequestActionDTO,
} from './tests/mock-data';

describe('CCA3 batch completion from the global workflow timeline', () => {
  let harness: RouterTestingHarness;
  let http: HttpTestingController;
  const workflowUrl = '/buyout-surplus/workflow-history/BOS-TP8001';

  beforeEach(async () => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter(
          [{ path: 'buyout-surplus/workflow-history', children: BUY_OUT_SURPLUS_WORKFLOW_HISTORY_ROUTES }],
          withRouterConfig({ paramsInheritanceStrategy: 'always' }),
        ),
      ],
    });
    TestBed.inject(AuthStore).setUserState({ roleType: 'REGULATOR', status: 'ENABLED' });
    harness = await RouterTestingHarness.create();
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  async function openFromWorkflow(action: RequestActionDTO) {
    vi.spyOn(TestBed.inject(RequestsService), 'getRequestDetailsById').mockReturnValue(
      of({
        id: action.requestId,
        requestType: action.requestType,
        requestStatus: action.type.endsWith('WITH_FAILURES') ? 'COMPLETED_WITH_FAILURES' : 'COMPLETED',
      }),
    );
    vi.spyOn(TestBed.inject(RequestActionsService), 'getRequestActionsByRequestId').mockReturnValue(
      of([action, { id: 47, type: 'BUY_OUT_SURPLUS_FACILITY_RUN_SUBMITTED', submitter: 'Regulator England' }]),
    );
    await harness.navigateByUrl(workflowUrl);
    await harness.fixture.whenStable();
    const link = getByRole('link', { name: /^View details/ }) as HTMLAnchorElement;
    expect(link.getAttribute('href')).toBe(`${workflowUrl}/timeline/${action.id}`);
    link.click();
    const request = await vi.waitFor(() => http.expectOne((req) => req.url.endsWith(`/request-actions/${action.id}`)));
    expect(request.request.method).toBe('GET');
    request.flush(action);
    await harness.fixture.whenStable();
    harness.detectChanges();
  }

  it.each([
    ['BUY_OUT_SURPLUS_FACILITY_RUN_COMPLETED', 'Completed', 'Buy-out and surplus batch run completed'],
    [
      'BUY_OUT_SURPLUS_FACILITY_RUN_COMPLETED_WITH_FAILURES',
      'Completed with failures',
      'Buy-out and surplus batch run completed with failures',
    ],
  ])('opens %s through View details and renders the API summary', async (type, status, title) => {
    await openFromWorkflow({ ...buyOutSurplusFacilityBatchRunCompletedRequestActionDTO, type });

    expect(TestBed.inject(Router).url).toBe(`${workflowUrl}/timeline/49`);
    expect(document.title).toBe(title);
    expect(getByRole('heading', { name: title })).toBeTruthy();
    expect(harness.routeNativeElement.querySelector('cca-buy-out-surplus-facility-batch-run-completed')).toBeTruthy();
    const sections = getSummaryListData(harness.routeNativeElement);
    expect(sections[0][1]).toContain('BOS-TP8001');
    expect(sections[0][1]).toContain(status);
    expect(sections[0][1]).toContain('3,000');
    expect(sections[1][1]).toEqual(['38.00', '5,500', '1,700', '4,000', '2', '2']);
    expect(sections[2][1]).toEqual(['37.00', '5,500', '1,700', '4,000', '2', '2']);
    if (status === 'Completed with failures') {
      expect(sections[0][0]).toContain('Failed target units');
      expect(sections[0][0]).toContain('Failed facilities');
      expect(sections[0][1]).toContain('1,700');
      expect(sections[0][1]).toContain('3,500');
    } else {
      expect(sections[0][0]).not.toContain('Failed facilities');
    }
    const csvLink = getByRole('link', {
      name: 'BOS-TP8001 buy-out and surplus summary report.csv (opens in a new tab)',
    }) as HTMLAnchorElement;
    expect(csvLink.getAttribute('href')).toBe(
      `${workflowUrl}/timeline/49/file-download/650c6835-dbf3-4fd2-a0d6-7ac1e8919234`,
    );
    expect(csvLink.target).toBe('_blank');
  });

  it('opens a completed run with no eligible facilities and no CSV report', async () => {
    const payload: BuyOutSurplusFacilityRunCompletedRequestActionPayload = {
      ...buyOutSurplusFacilityBatchRunCompletedMock,
      runSummary: {
        totalAccounts: 0,
        failedAccounts: 0,
        totalFacilities: 0,
        failedFacilities: 0,
        runSummaryEntries: [],
      },
      csvFile: undefined,
    };
    await openFromWorkflow({ ...buyOutSurplusFacilityBatchRunCompletedRequestActionDTO, payload });
    expect(getSummaryListData(harness.routeNativeElement)).toEqual([
      [
        ['Run ID', 'Status', 'Total target units'],
        ['BOS-TP8001', 'Completed', '0'],
      ],
    ]);
  });
});
