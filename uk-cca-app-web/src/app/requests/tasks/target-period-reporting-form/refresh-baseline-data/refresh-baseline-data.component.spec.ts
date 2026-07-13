import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of, throwError } from 'rxjs';

import { RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';
import { getByText } from '@testing';

import { RefreshBaselineDataComponent } from './refresh-baseline-data.component';

describe('RefreshBaselineDataComponent', () => {
  let component: RefreshBaselineDataComponent;
  let fixture: ComponentFixture<RefreshBaselineDataComponent>;
  let store: RequestTaskStore;
  let router: Router;

  const route = new ActivatedRouteStub();

  const tasksApiService = {
    saveRequestTaskAction: vi.fn().mockReturnValue(of({})),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RefreshBaselineDataComponent],
      providers: [
        provideRouter([]),
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksApiService, useValue: tasksApiService },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);

    store.setRequestTaskItem({
      requestTask: {
        id: 42,
        type: 'PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_APPLICATION',
        payload: { payloadType: 'EMPTY_PAYLOAD' } as any,
      } as any,
      requestInfo: { accountId: 1 } as any,
    });

    fixture = TestBed.createComponent(RefreshBaselineDataComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();

    tasksApiService.saveRequestTaskAction.mockClear();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the page heading and description', () => {
    expect(getByText('Refresh baseline data', fixture.nativeElement)).toBeTruthy();
    expect(
      getByText(/You are about to refresh the baseline data in your TPR form/, fixture.nativeElement),
    ).toBeTruthy();
  });

  it('should render the refresh button', () => {
    const button = fixture.nativeElement.querySelector('button');
    expect(button).toBeTruthy();
    expect(button.textContent.trim()).toBe('Refresh baseline data');
  });

  it('should not display error summary initially', () => {
    expect(component.isErrorSummaryDisplayed()).toBe(false);
    expect(fixture.nativeElement.querySelector('cca-error-summary')).toBeNull();
  });

  it('should call saveRequestTaskAction with correct payload and navigate on success', () => {
    tasksApiService.saveRequestTaskAction.mockReturnValue(of({}));

    component.onRefreshBaselineData();

    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalledWith({
      requestTaskId: 42,
      requestTaskActionType: 'PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_REFRESH_APPLICATION',
      requestTaskActionPayload: { payloadType: 'EMPTY_PAYLOAD' },
    });

    expect(router.navigate).toHaveBeenCalledWith(['../..'], { relativeTo: route, replaceUrl: true });
  });

  it('should display error summary when API returns a known error code', async () => {
    tasksApiService.saveRequestTaskAction.mockReturnValue(throwError(() => ({ error: { code: 'TPRDF1002' } })));

    component.onRefreshBaselineData();
    fixture.detectChanges();

    expect(component.isErrorSummaryDisplayed()).toBe(true);
    expect(component.errorSummaryInfo().message).toBe(
      'This facility is not eligible to report for this target period - the workflow must be cancelled.',
    );
    expect(fixture.nativeElement.querySelector('cca-error-summary')).toBeTruthy();
  });

  it('should not navigate when API returns an error', () => {
    tasksApiService.saveRequestTaskAction.mockReturnValue(throwError(() => ({ error: { code: 'TPRDF1005' } })));

    component.onRefreshBaselineData();

    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should not display error summary for unknown error codes', () => {
    tasksApiService.saveRequestTaskAction.mockReturnValue(throwError(() => ({ error: { code: 'UNKNOWN_CODE' } })));

    component.onRefreshBaselineData();

    expect(component.isErrorSummaryDisplayed()).toBe(false);
  });
});
