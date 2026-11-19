import { Location } from '@angular/common';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideLocationMocks } from '@angular/common/testing';
import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { NavigationEnd, provideRouter, Router, withRouterConfig } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { filter, firstValueFrom, of, take, throwError } from 'rxjs';

import { AuthStore } from '@netz/common/auth';
import { RequestActionStore } from '@netz/common/store';
import { getByLabelText, getByRole } from '@testing';

import { RequestActionsService } from 'cca-api';

import { surplusCalculatedRequestActionDTO } from '../surplus-calculated/tests/mock-data';
import { TIMELINE_ROUTES } from '../timeline.routes';
import { mockAccountAction, mockAccountPayload } from './testing/mock-data';

@Component({ template: '<p>Workflow timeline</p>' })
class WorkflowFixtureComponent {}

describe('CCA3 account event production routes', () => {
  let harness: RouterTestingHarness;
  let router: Router;
  let actions: { getRequestActionById: ReturnType<typeof vi.fn> };
  const filteredUrl = '/timeline/3293?search=North&targetPeriod=TP8&result=TARGET_MET&pageSize=20';

  beforeEach(async () => {
    actions = { getRequestActionById: vi.fn().mockReturnValue(of(mockAccountAction())) };
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideLocationMocks(),
        { provide: RequestActionsService, useValue: actions },
        provideRouter(
          [
            { path: 'timeline', children: TIMELINE_ROUTES },
            { path: 'workflow', component: WorkflowFixtureComponent },
            { path: 'dashboard', component: WorkflowFixtureComponent },
            { path: 'refused', component: WorkflowFixtureComponent, canActivate: [() => false] },
            {
              path: 'failed',
              component: WorkflowFixtureComponent,
              canActivate: [
                () => {
                  throw new Error('Navigation failed');
                },
              ],
            },
            { path: 'other-timeline', children: TIMELINE_ROUTES.map((route) => ({ ...route })) },
          ],
          withRouterConfig({ paramsInheritanceStrategy: 'always' }),
        ),
      ],
    });
    harness = await RouterTestingHarness.create();
    router = TestBed.inject(Router);
    router.setUpLocationChangeListener();
  });

  afterEach(() => TestBed.inject(HttpTestingController).verify());

  async function open(url = '/timeline/3293') {
    await harness.navigateByUrl(url);
    await harness.fixture.whenStable();
  }

  async function browserBack() {
    const navigation = firstValueFrom(
      router.events.pipe(
        filter((event) => event instanceof NavigationEnd),
        take(1),
      ),
    );
    TestBed.inject(Location).back();
    await navigation;
    harness.detectChanges();
    await harness.fixture.whenStable();
  }

  it.each(['REGULATOR', 'SECTOR_USER', 'OPERATOR'])(
    'loads the historical action and fixed title/date for entitled %s users',
    async (roleType) => {
      TestBed.inject(AuthStore).setUserState({ roleType, status: 'ENABLED' });
      await open();
      expect(actions.getRequestActionById).toHaveBeenCalledExactlyOnceWith(3293);
      expect(document.title).toBe('Buy-out fee and surplus calculated');
      expect(getByRole('heading', { name: 'Buy-out fee and surplus calculated' })).toBeTruthy();
      expect(harness.routeNativeElement.textContent).toContain('2 Sept 2027, 3:35pm');
      expect(harness.routeNativeElement.textContent).toContain('BOS-TP8004');
    },
  );

  it('starts with default filters when reopening the event without query parameters', async () => {
    await open(filteredUrl);
    await harness.navigateByUrl('/workflow');
    expect(TestBed.inject(RequestActionStore).state.action).toBeNull();

    await open();

    expect(router.url).toBe('/timeline/3293');
    expect((getByLabelText('Facility ID or site name') as HTMLInputElement).value).toBe('');
    expect((getByLabelText('Rows per page') as HTMLSelectElement).value).toBe('10');
    expect(harness.routeNativeElement.querySelectorAll('tbody tr')).toHaveLength(4);
    expect(harness.routeNativeElement.textContent).toContain('199.25');
    expect(harness.routeNativeElement.textContent).toContain('williamsj@example.com');
    expect(actions.getRequestActionById).toHaveBeenCalledTimes(2);
  });

  it('keeps the action, visible event and filters when another guard refuses navigation', async () => {
    await open(filteredUrl);
    const action = TestBed.inject(RequestActionStore).state.action;

    expect(await router.navigateByUrl('/refused')).toBe(false);
    harness.detectChanges();

    expect(router.url).toBe(filteredUrl);
    expect(TestBed.inject(RequestActionStore).state.action).toBe(action);
    expect((getByLabelText('Facility ID or site name') as HTMLInputElement).value).toBe('North');
    expect((getByLabelText('Rows per page') as HTMLSelectElement).value).toBe('20');
    expect(harness.routeNativeElement.textContent).toContain('BOS-TP8004');
  });

  it('keeps the action and filters when an attempted exit fails', async () => {
    await open(filteredUrl);
    const action = TestBed.inject(RequestActionStore).state.action;

    await expect(router.navigateByUrl('/failed')).rejects.toThrow('Navigation failed');
    harness.detectChanges();

    expect(TestBed.inject(RequestActionStore).state.action).toBe(action);
    expect(router.url).toBe(filteredUrl);
    expect((getByLabelText('Facility ID or site name') as HTMLInputElement).value).toBe('North');
    expect(harness.routeNativeElement.textContent).toContain('BOS-TP8004');
  });

  it('does not clear the destination action when moving between separate timeline routes', async () => {
    await open(filteredUrl);
    const destination = mockAccountAction(mockAccountPayload({ runId: 'BOS-TP9001' }), 3294);
    actions.getRequestActionById.mockReturnValue(of(destination));

    await open('/other-timeline/3294');

    expect(TestBed.inject(RequestActionStore).state.action).toEqual(destination);
    expect((getByLabelText('Facility ID or site name') as HTMLInputElement).value).toBe('');
    expect(harness.routeNativeElement.textContent).toContain('BOS-TP9001');
  });

  it('loads another action with fresh filters when the outer route is reused', async () => {
    await open(filteredUrl);
    actions.getRequestActionById.mockReturnValue(
      of(mockAccountAction(mockAccountPayload({ runId: 'BOS-TP9001', summary: [], defaultContacts: [] }), 3294)),
    );
    await open('/timeline/3294');
    expect((getByLabelText('Facility ID or site name') as HTMLInputElement).value).toBe('');
    expect(harness.routeNativeElement.textContent).toContain('BOS-TP9001');
    expect(harness.routeNativeElement.textContent).not.toContain('BOS-TP8004');
    expect(harness.routeNativeElement.textContent).not.toContain('Recipients');
  });

  it('renders other timeline types through the existing content mapping and resets filters on return', async () => {
    await open(filteredUrl);
    actions.getRequestActionById.mockReturnValue(of(surplusCalculatedRequestActionDTO));

    await harness.navigateByUrl('/timeline/75');

    expect(harness.routeNativeElement.textContent).toContain('BOS-TP6012');

    actions.getRequestActionById.mockReturnValue(of(mockAccountAction()));
    await open();

    expect((getByLabelText('Facility ID or site name') as HTMLInputElement).value).toBe('');
    expect(harness.routeNativeElement.textContent).toContain('BOS-TP8004');
  });

  it.each([403, 404, 500])('retains existing action-load failure handling for HTTP %s', async (status) => {
    actions.getRequestActionById.mockReturnValue(throwError(() => ({ status })));
    await harness.navigateByUrl('/timeline/3293');
    expect(router.url).toBe('/dashboard');
    expect(TestBed.inject(RequestActionStore).state.action).toBeNull();
    expect(harness.routeNativeElement.textContent).not.toContain('199.25');
  });

  it('routes an issued historical notice to the authorized document-token endpoint', async () => {
    await open(filteredUrl);
    const documentLink = getByRole('link', { name: 'CCA080024.pdf (opens in a new tab)' }) as HTMLAnchorElement;
    expect(documentLink.getAttribute('href')).toBe('/timeline/3293/file-download/document/tp8-refund-notice');
    expect(documentLink.target).toBe('_blank');
    vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => undefined);
    await harness.navigateByUrl(documentLink.getAttribute('href'));
    const request = TestBed.inject(HttpTestingController).expectOne((req) =>
      req.url.endsWith('/v1.0/request-action-file-documents/3293'),
    );
    expect(request.request.method).toBe('GET');
    expect(request.request.params.get('fileDocumentUuid')).toBe('tp8-refund-notice');
    request.flush({ token: 'historical-authorized-token', tokenExpirationMinutes: 10 });
    harness.detectChanges();
    await harness.fixture.whenStable();
    expect(harness.routeNativeElement.querySelector('a').href).toContain(
      '/v1.0/file-documents/historical-authorized-token',
    );
    TestBed.inject(HttpTestingController).expectNone((req) => req.url.includes('request-action-attachments'));

    // Returning recreates the list and restores its selection from the list URL.
    await browserBack();
    expect(router.url).toBe(filteredUrl);
    expect((getByLabelText('Facility ID or site name') as HTMLInputElement).value).toBe('North');
    expect((getByLabelText('Rows per page') as HTMLSelectElement).value).toBe('20');
    expect(harness.routeNativeElement.querySelectorAll('tbody tr')).toHaveLength(1);
  });

  it('restores applied filters through browser Back without reloading the historical action', async () => {
    await open('/timeline/3293?context=timeline#facilities');
    const input = getByLabelText('Facility ID or site name') as HTMLInputElement;
    input.value = 'North';
    input.dispatchEvent(new Event('input'));
    getByRole('button', { name: 'Apply' }).click();
    await harness.fixture.whenStable();
    const appliedUrl = router.parseUrl(router.url);
    expect(appliedUrl.queryParams).toEqual({ context: 'timeline', search: 'North', page: '1' });
    expect(appliedUrl.fragment).toBe('facilities');
    expect(harness.routeNativeElement.querySelectorAll('tbody tr')).toHaveLength(1);

    getByRole('button', { name: 'Clear' }).click();
    await harness.fixture.whenStable();
    expect(harness.routeNativeElement.querySelectorAll('tbody tr')).toHaveLength(4);
    expect(router.parseUrl(router.url).queryParams).toEqual({ context: 'timeline', page: '1' });

    await browserBack();

    expect((getByLabelText('Facility ID or site name') as HTMLInputElement).value).toBe('North');
    expect(harness.routeNativeElement.querySelectorAll('tbody tr')).toHaveLength(1);
    expect(actions.getRequestActionById).toHaveBeenCalledExactlyOnceWith(3293);
  });
});
