import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { config, of } from 'rxjs';

import { AuthService, GlobalErrorHandlingService } from '@shared/services';

import { HttpErrorInterceptor } from '../../../../../../interceptors/http-error.interceptor';
import { PendingRequestInterceptor } from '../../../../../../interceptors/pending-request.interceptor';
import { EtsReportingComponent } from './ets-reporting.component';

describe('EtsReportingComponent', () => {
  let fixture: ComponentFixture<EtsReportingComponent>;
  let component: EtsReportingComponent;
  let http: HttpTestingController;
  let router: Router;
  let route: ActivatedRoute;
  let previousUnhandledError: typeof config.onUnhandledError;

  beforeEach(async () => {
    previousUnhandledError = config.onUnhandledError;
    await TestBed.configureTestingModule({
      imports: [EtsReportingComponent],
      providers: [
        provideRouter([]),
        provideHttpClient(withInterceptors([HttpErrorInterceptor, PendingRequestInterceptor])),
        provideHttpClientTesting(),
        {
          provide: AuthService,
          useValue: { loadUserState: () => of({}), reauthenticate: vi.fn().mockResolvedValue(undefined) },
        },
      ],
    }).compileComponents();

    http = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);
    route = TestBed.inject(ActivatedRoute);
    route.snapshot.params = { facilityId: '42' };
    fixture = TestBed.createComponent(EtsReportingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    config.onUnhandledError = previousUnhandledError;
    http.verify();
    vi.useRealTimers();
    vi.restoreAllMocks();
  });

  function submitYear(year = 2026) {
    component.form.controls.targetYear.setValue(year);
    (fixture.nativeElement as HTMLElement).querySelector('form').dispatchEvent(new Event('submit'));
    fixture.detectChanges();
  }

  function expectCreateRequest() {
    return http.expectOne((request) => request.method === 'POST' && request.url.endsWith('/v1.0/requests'));
  }

  function expectTaskLookup() {
    return http.expectOne((request) => request.url.endsWith('/v1.0/items/ETS00042-2026-1'));
  }

  it.each(['2026-12-31T23:59:59Z', '2034-01-01T00:00:00Z'])(
    'should offer scheme data years independently of the browser date (%s)',
    (date) => {
      vi.useFakeTimers({ toFake: ['Date'] });
      vi.setSystemTime(new Date(date));
      fixture.destroy();
      fixture = TestBed.createComponent(EtsReportingComponent);
      fixture.detectChanges();

      const options = (fixture.nativeElement as HTMLElement).querySelectorAll('select option');
      expect(Array.from(options, (option) => option.textContent.trim()).filter(Boolean)).toEqual([
        '2026',
        '2027',
        '2028',
        '2029',
        '2030',
      ]);
      expect(fixture.nativeElement.querySelector('button[type="submit"]')).not.toBeNull();
    },
  );

  it('should require a year selection', () => {
    const element = fixture.nativeElement as HTMLElement;
    element.querySelector('form').dispatchEvent(new Event('submit'));
    fixture.detectChanges();

    expect(element.querySelectorAll('.govuk-error-summary')).toHaveLength(1);
    expect(element.querySelector('.govuk-error-summary__list a').textContent.trim()).toBe('Select a year');
    expect(element.querySelector('govuk-error-message').textContent).toContain('Select a year');
    expect(element.querySelector('select').classList).toContain('govuk-select--error');
    http.expectNone((request) => request.method === 'POST');
  });

  it('should create the facility request with the selected data year and open its task list', () => {
    submitYear();
    const request = expectCreateRequest();
    expect(request.request.params.get('resourceId')).toBe('42');
    expect(request.request.body).toEqual({
      requestType: 'EMISSIONS_TRADING_SCHEME_DATA',
      requestCreateActionPayload: {
        payloadType: 'EMISSIONS_TRADING_SCHEME_DATA_CREATE_ACTION_PAYLOAD',
        targetYear: 2026,
      },
    });
    expect(fixture.nativeElement.querySelector('button').disabled).toBe(true);

    request.flush({ requestId: 'ETS00042-2026-1' });
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('[role="status"]').textContent).toContain(
      'Opening your UK ETS reporting task',
    );
    expect(fixture.nativeElement.querySelector('form')).toBeNull();
    expect(router.navigate).not.toHaveBeenCalled();

    expectTaskLookup().flush({ items: [{ requestType: 'EMISSIONS_TRADING_SCHEME_DATA', taskId: 123 }] });
    expect(router.navigate).toHaveBeenCalledWith(['/tasks', 123], { relativeTo: route, replaceUrl: true });
  });

  it.each([{ items: [] }, { items: [{ taskId: 1 }, { taskId: 2 }] }])(
    'should return to the dashboard without one task: %j',
    (response) => {
      submitYear();
      expectCreateRequest().flush({ requestId: 'ETS00042-2026-1' });
      expectTaskLookup().flush(response);

      expect(router.navigate).toHaveBeenCalledWith(['/dashboard'], { relativeTo: route, replaceUrl: true });
    },
  );

  it.each([
    ['ETS1001', 'This facility is not eligible'],
    ['ETS1002', 'UK ETS reporting is not available'],
    ['ETS1003', 'has not started yet'],
  ])('should display the year validation error %s without navigating', (code, message) => {
    submitYear();
    expectCreateRequest().flush({ code }, { status: 400, statusText: 'Bad Request' });
    fixture.detectChanges();

    const element = fixture.nativeElement as HTMLElement;
    expect(element.querySelectorAll('.govuk-error-summary')).toHaveLength(1);
    expect(element.querySelector('.govuk-error-summary__list a').textContent).toContain(message);
    expect(element.querySelector('govuk-error-message').textContent).toContain(message);
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should show an unavailable workflow error at form level and allow retrying the same year', () => {
    submitYear();
    expectCreateRequest().flush({ code: 'REQUEST_CREATE_ACTION1000' }, { status: 400, statusText: 'Bad Request' });
    fixture.detectChanges();

    const element = fixture.nativeElement as HTMLElement;
    expect(element.querySelectorAll('.govuk-error-summary')).toHaveLength(1);
    expect(element.querySelector('.govuk-error-summary').textContent).toContain(
      'a task is already in progress or reporting is unavailable for this facility',
    );
    expect(element.querySelector('govuk-error-message')).toBeNull();
    expect(element.querySelector('select').classList).not.toContain('govuk-select--error');
    expect(router.navigate).not.toHaveBeenCalled();

    element.querySelector('form').dispatchEvent(new Event('submit'));
    expectCreateRequest().flush({ requestId: 'ETS00042-2026-1' });
    expectTaskLookup().flush({ items: [{ requestType: 'EMISSIONS_TRADING_SCHEME_DATA', taskId: 123 }] });
    fixture.detectChanges();

    expect(element.querySelector('.govuk-error-summary')).toBeNull();
    expect(router.navigate).toHaveBeenCalledWith(['/tasks', 123], { relativeTo: route, replaceUrl: true });
  });

  it.each([400, 404, 502])('should return to the dashboard when the created task lookup fails (%i)', async (status) => {
    submitYear();
    expectCreateRequest().flush({ requestId: 'ETS00042-2026-1' });
    expectTaskLookup().flush({ code: 'ETS1001' }, { status, statusText: 'Request failed' });
    await new Promise((resolve) => setTimeout(resolve));
    fixture.detectChanges();

    expect(router.navigate).toHaveBeenCalledOnce();
    expect(router.navigate).toHaveBeenCalledWith(['/dashboard'], { relativeTo: route, replaceUrl: true });
    expect(fixture.nativeElement.querySelector('.govuk-error-summary')).toBeNull();
  });

  it('should leave a failed task lookup with 401 to re-authentication', async () => {
    submitYear();
    expectCreateRequest().flush({ requestId: 'ETS00042-2026-1' });
    expectTaskLookup().flush(null, { status: 401, statusText: 'Unauthorized' });
    await new Promise((resolve) => setTimeout(resolve));

    expect(TestBed.inject(AuthService).reauthenticate).toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it.each([
    [403, ['landing'], { state: { forceNavigation: true } }],
    [500, ['/error', '500'], { state: { forceNavigation: true }, skipLocationChange: true }],
  ])(
    'should keep the global error navigation when the created task lookup fails (%i)',
    async (status, commands, extras) => {
      submitYear();
      expectCreateRequest().flush({ requestId: 'ETS00042-2026-1' });
      expectTaskLookup().flush({ code: 'ETS1001' }, { status, statusText: 'Request failed' });
      await new Promise((resolve) => setTimeout(resolve));

      expect(router.navigate).toHaveBeenCalledOnce();
      expect(router.navigate).toHaveBeenCalledWith(commands, extras);
    },
  );

  it('should return to the dashboard when the created task lookup has a network failure', () => {
    submitYear();
    expectCreateRequest().flush({ requestId: 'ETS00042-2026-1' });
    expectTaskLookup().error(new ProgressEvent('error'));

    expect(router.navigate).toHaveBeenCalledWith(['/dashboard'], { relativeTo: route, replaceUrl: true });
  });

  it.each([
    [400, 'UNKNOWN_CODE'],
    [404, 'RESOURCE_NOT_FOUND'],
    [403, 'FORBIDDEN'],
    [500, 'INTERNAL_SERVER_ERROR'],
  ])('should rethrow unexpected creation errors for global handling (%i)', async (status, code) => {
    const unhandledError = new Promise<unknown>((resolve) => {
      config.onUnhandledError = resolve;
    });
    submitYear();
    expectCreateRequest().flush({ code }, { status, statusText: 'Request failed' });
    const error = await unhandledError;
    fixture.detectChanges();

    expect(error).toMatchObject({ status, error: { code } });
    expect(fixture.nativeElement.querySelector('.govuk-error-summary')).toBeNull();
    expect(component.requestCreated()).toBe(false);
    expect(fixture.nativeElement.textContent).not.toContain('task was created');

    if (status === 404) {
      vi.spyOn(console, 'error').mockImplementation(() => undefined);
      TestBed.inject(GlobalErrorHandlingService).handleError(error);
      expect(router.navigate).toHaveBeenCalledWith(['/error', '404'], { state: { forceNavigation: true } });
    } else if (status === 500) {
      expect(router.navigate).toHaveBeenCalledWith(['/error', '500'], {
        state: { forceNavigation: true },
        skipLocationChange: true,
      });
    } else if (status === 403) {
      expect(router.navigate).toHaveBeenCalledWith(['landing'], { state: { forceNavigation: true } });
    } else {
      expect(router.navigate).not.toHaveBeenCalled();
    }
  });
});
