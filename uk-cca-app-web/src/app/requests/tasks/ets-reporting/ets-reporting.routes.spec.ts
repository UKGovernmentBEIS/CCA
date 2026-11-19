import { TestBed } from '@angular/core/testing';
import { NavigationEnd, provideRouter, Router } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { filter, firstValueFrom, of, throwError } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';
import { TasksApiService } from '@requests/common';

import { TASKS_ROUTES } from '../tasks.routes';
import { ETS_REPORTING_ROUTES } from './ets-reporting.routes';
import { ETS_ENERGY_FUEL_DETAILS_SUBTASK, EtsReportingTaskPayload } from './ets-reporting.types';
import { mockCompletedEtsPayload, mockEtsFuelPayload, mockEtsPayload } from './testing/mock-data';

describe('ETS reporting task routes', () => {
  let store: RequestTaskStore;

  beforeEach(() => {
    const taskRoute = TASKS_ROUTES.find((route) => route.path === ':taskId');
    const etsRoute = taskRoute.children.find((route) => route.path === 'ets-reporting');
    TestBed.configureTestingModule({
      providers: [
        { provide: TasksApiService, useValue: { saveRequestTaskAction: vi.fn().mockReturnValue(of({})) } },
        provideRouter([
          {
            path: 'tasks',
            children: [
              {
                path: ':taskId',
                providers: taskRoute.providers,
                children: [{ path: '', pathMatch: 'full', children: [] }, etsRoute],
              },
            ],
          },
          { path: 'dashboard', children: [] },
        ]),
      ],
    });
    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({
      requestTask: { id: 123, type: 'EMISSIONS_TRADING_SCHEME_DATA_SUBMIT', payload: mockEtsPayload() },
    });
    store.setIsEditable(true);
  });

  it.each([
    ['unanswered', mockEtsPayload(), 'ultra-small-emitter'],
    [
      'partly answered',
      mockEtsPayload({ emissionsTradingSchemeDataInputDetails: { isUltraSmallEmitter: true } }),
      'ultra-small-emitter',
    ],
    [
      'required fuel missing',
      mockEtsPayload({ emissionsTradingSchemeDataInputDetails: { isUltraSmallEmitter: false } }),
      'details',
    ],
    ['valid in progress', mockEtsFuelPayload(), 'check-your-answers'],
    ['valid completed', mockCompletedEtsPayload(), 'summary'],
    [
      'stale completion',
      mockEtsPayload({ sectionsCompleted: { [ETS_ENERGY_FUEL_DETAILS_SUBTASK]: 'COMPLETED' } }),
      'ultra-small-emitter',
    ],
  ] as [string, EtsReportingTaskPayload, string][])('resumes %s at %s', async (_description, payload, step) => {
    store.setPayload(payload);
    const harness = await RouterTestingHarness.create();
    await harness.navigateByUrl('/tasks/123/ets-reporting/energy-fuel-amount');
    expect(TestBed.inject(Router).url).toBe(`/tasks/123/ets-reporting/energy-fuel-amount/${step}`);
    expect(harness.routeNativeElement.querySelector('netz-return-to-task-or-action-page a').getAttribute('href')).toBe(
      '/tasks/123',
    );
  });

  it.each([
    '',
    '/details',
    '/ultra-small-emitter',
    '/ultra-small-emitter?summary=1',
    '/check-your-answers',
    '/summary',
  ])('opens a read-only summary for energy-fuel-amount%s', async (path) => {
    store.setIsEditable(false);
    store.setPayload(mockEtsFuelPayload());
    const harness = await RouterTestingHarness.create();
    await harness.navigateByUrl(`/tasks/123/ets-reporting/energy-fuel-amount${path}`);
    expect(TestBed.inject(Router).url).toBe('/tasks/123/ets-reporting/energy-fuel-amount/summary');
    expect(harness.routeNativeElement.textContent).not.toContain('Change');
    expect(harness.routeNativeElement.querySelector('button[type="submit"]')).toBeNull();
  });

  it.each([
    ['unanswered', mockEtsPayload()],
    ['in progress', mockEtsFuelPayload()],
    ['stale completion', mockEtsPayload({ sectionsCompleted: { [ETS_ENERGY_FUEL_DETAILS_SUBTASK]: 'COMPLETED' } })],
  ] as [string, EtsReportingTaskPayload][])('protects direct submit access for an %s report', async (_, payload) => {
    store.setPayload(payload);
    const harness = await RouterTestingHarness.create();
    await harness.navigateByUrl('/tasks/123/ets-reporting/submit');
    expect(TestBed.inject(Router).url).toBe('/tasks/123');
  });

  it('opens submit for a valid completed report', async () => {
    store.setPayload(mockCompletedEtsPayload());
    const harness = await RouterTestingHarness.create();
    await harness.navigateByUrl('/tasks/123/ets-reporting/submit');
    expect(harness.routeNativeElement.querySelector('h1').textContent).toBe('Submit your UK ETS report');
    expect(harness.routeNativeElement.querySelector('button').textContent.trim()).toBe(
      'Confirm and submit UK ETS report',
    );
  });

  it('blocks submission on a read-only task', async () => {
    store.setPayload(mockCompletedEtsPayload());
    store.setIsEditable(false);
    await RouterTestingHarness.create();
    expect(await TestBed.inject(Router).navigateByUrl('/tasks/123/ets-reporting/submit')).toBe(false);
  });

  it.each(['ETS1004', 'ETS1005'])('returns to editable saved answers after %s', async (code) => {
    const payload = mockEtsFuelPayload();
    payload.sectionsCompleted[ETS_ENERGY_FUEL_DETAILS_SUBTASK] = 'COMPLETED';
    store.setPayload(payload);
    vi.mocked(TestBed.inject(TasksApiService).saveRequestTaskAction).mockReturnValueOnce(
      throwError(() => ({ status: 400, error: { code } })),
    );
    const harness = await RouterTestingHarness.create();
    await harness.navigateByUrl('/tasks/123/ets-reporting/submit');
    harness.routeNativeElement.querySelector('button').click();
    harness.detectChanges();

    const router = TestBed.inject(Router);
    expect(router.url).toBe('/tasks/123/ets-reporting/submit');
    expect(store.state.requestTaskItem.requestTask.payload).toEqual(payload);
    const link = harness.routeNativeElement.querySelector<HTMLAnchorElement>('.govuk-error-summary a');
    expect(link.getAttribute('href')).toBe('/tasks/123/ets-reporting/energy-fuel-amount/check-your-answers');
    const navigation = firstValueFrom(router.events.pipe(filter((event) => event instanceof NavigationEnd)));
    link.click();
    await navigation;
    harness.detectChanges();
    expect(router.url).toBe('/tasks/123/ets-reporting/energy-fuel-amount/check-your-answers');
    expect(harness.routeNativeElement.textContent).toContain('Change');
    expect(harness.routeNativeElement.querySelector('button').textContent.trim()).toBe('Confirm and complete');
  });

  it('navigates from submit to confirmation when success clears the payload and editability', async () => {
    store.setPayload(mockCompletedEtsPayload());
    vi.mocked(TestBed.inject(TasksApiService).saveRequestTaskAction).mockImplementationOnce(() => {
      store.setPayload({});
      store.setIsEditable(false);
      return of({});
    });
    const harness = await RouterTestingHarness.create();
    await harness.navigateByUrl('/tasks/123/ets-reporting/submit');
    const router = TestBed.inject(Router);
    const navigation = firstValueFrom(router.events.pipe(filter((event) => event instanceof NavigationEnd)));

    harness.routeNativeElement.querySelector('button').click();
    await navigation;
    harness.detectChanges();

    expect(router.url).toBe('/tasks/123/ets-reporting/submit/confirmation');
    expect(harness.routeNativeElement.querySelector('h1').textContent.trim()).toBe(
      'UK emissions trading scheme (UK ETS) report submitted',
    );
    expect(harness.routeNativeElement.querySelector('a').getAttribute('href')).toBe('/dashboard');
  });

  it('renders confirmation after submission clears the payload and returns Back to dashboard', async () => {
    store.setPayload({});
    const harness = await RouterTestingHarness.create();
    await TestBed.inject(Router).navigateByUrl('/tasks/123/ets-reporting/submit/confirmation', {
      state: { etsReportSubmitted: true },
    });
    harness.detectChanges();
    expect(harness.routeNativeElement.textContent).toContain(
      'Your UK ETS report has been submitted. You can return to the dashboard to view your tasks.',
    );
    expect(harness.routeNativeElement.querySelector('a').getAttribute('href')).toBe('/dashboard');
    const submitRoute = ETS_REPORTING_ROUTES[0].children.find((route) => route.path === 'submit');
    expect(submitRoute.children.find((route) => route.path === 'confirmation').data.backlink).toBe('/dashboard');
  });

  it('does not claim submission for direct confirmation access', async () => {
    const harness = await RouterTestingHarness.create();
    await harness.navigateByUrl('/tasks/123/ets-reporting/submit/confirmation');
    expect(TestBed.inject(Router).url).toBe('/dashboard');
  });
});
