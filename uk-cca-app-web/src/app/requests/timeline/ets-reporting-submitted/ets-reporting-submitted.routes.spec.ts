import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter, withRouterConfig } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { of } from 'rxjs';

import { AuthStore } from '@netz/common/auth';
import { getByRole } from '@testing';

import { RequestActionsService, RequestItemsService, RequestsService } from 'cca-api';

import { WORKFLOW_DETAILS_ROUTES } from '../../../shared/components/workflow-details/workflow-details.routes';
import { mockEtsSubmittedAction } from './testing/mock-data';

describe('ETS submitted timeline navigation', () => {
  const workflowUrl = '/workflow-details/ADS-F00040-ETS-1';
  let harness: RouterTestingHarness;
  let http: HttpTestingController;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter(
          [{ path: 'workflow-details', children: WORKFLOW_DETAILS_ROUTES }],
          withRouterConfig({ paramsInheritanceStrategy: 'always' }),
        ),
      ],
    });
    vi.spyOn(TestBed.inject(RequestsService), 'getRequestDetailsById').mockReturnValue(
      of({ id: 'ADS-F00040-ETS-1', requestType: 'EMISSIONS_TRADING_SCHEME_DATA', requestStatus: 'COMPLETED' }),
    );
    vi.spyOn(TestBed.inject(RequestItemsService), 'getItemsByRequest').mockReturnValue(
      of({ items: [], totalItems: 0 }),
    );
    vi.spyOn(TestBed.inject(RequestActionsService), 'getRequestActionsByRequestId').mockReturnValue(
      of([mockEtsSubmittedAction()]),
    );
    harness = await RouterTestingHarness.create();
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it.each(['REGULATOR', 'OPERATOR'])('opens the submitted action from its workflow for %s', async (roleType) => {
    TestBed.inject(AuthStore).setUserState({ roleType, status: 'ENABLED' });
    await harness.navigateByUrl(workflowUrl);
    await harness.fixture.whenStable();

    expect(
      getByRole('heading', { name: /UK emissions trading scheme \(UK ETS\) report/ }, harness.routeNativeElement),
    ).toBeTruthy();
    expect(harness.routeNativeElement.querySelector('govuk-tag').textContent.trim()).toBe('Completed');
    expect(harness.routeNativeElement.textContent).toContain(
      'UK emissions trading scheme (UK ETS) report submitted by Sector user',
    );
    const detailsLink = getByRole('link', { name: /^View details/ }, harness.routeNativeElement) as HTMLAnchorElement;
    expect(detailsLink.getAttribute('href')).toBe(`${workflowUrl}/timeline/42`);
    detailsLink.click();
    const request = await vi.waitFor(() => http.expectOne((req) => req.url.endsWith('/request-actions/42')));
    expect(request.request.method).toBe('GET');
    request.flush(mockEtsSubmittedAction());
    await harness.fixture.whenStable();
    harness.detectChanges();

    expect(
      getByRole(
        'heading',
        { name: 'UK emissions trading scheme (UK ETS) report submitted by Sector user' },
        harness.routeNativeElement,
      ),
    ).toBeTruthy();
    expect(harness.routeNativeElement.querySelector('cca-ets-reporting-submitted')).toBeTruthy();
    expect(harness.routeNativeElement.textContent).toContain('1 May 2027, 3:25pm');
    expect(harness.routeNativeElement.textContent).toContain('Yes, UK-E-IN-0000');
    expect(harness.routeNativeElement.querySelector('button, input')).toBeNull();
    expect(document.title).toBe('UK emissions trading scheme (UK ETS) report submitted by Sector user');
  });
});
