import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';

import { of, Subject } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { PendingRequestService } from '@netz/common/services';
import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';

import { TasksService } from 'cca-api';

import { ETS_ENERGY_FUEL_DETAILS_SUBTASK } from '../ets-reporting.types';
import { mockCompletedEtsPayload, mockEtsFuelPayload } from '../testing/mock-data';
import EtsReportingSubmitComponent from './ets-reporting-submit.component';

describe('ETS submit', () => {
  let fixture: ComponentFixture<EtsReportingSubmitComponent>;
  let store: RequestTaskStore;
  let router: Router;
  let response: Subject<unknown>;
  let processRequestTaskAction: ReturnType<typeof vi.fn>;

  beforeEach(async () => {
    response = new Subject();
    processRequestTaskAction = vi.fn().mockReturnValue(response);
    await TestBed.configureTestingModule({
      imports: [EtsReportingSubmitComponent],
      providers: [
        RequestTaskStore,
        TasksApiService,
        PendingRequestService,
        { provide: TasksService, useValue: { processRequestTaskAction } },
        { provide: BusinessErrorService, useValue: { showErrorForceNavigation: vi.fn() } },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        {
          provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER,
          useValue: () => 'UK emissions trading scheme (UK ETS) reporting - Submit form',
        },
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        provideRouter([]),
      ],
    }).compileComponents();
    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({
      requestTask: { id: 123, type: 'EMISSIONS_TRADING_SCHEME_DATA_SUBMIT', payload: mockCompletedEtsPayload() },
    });
    store.setIsEditable(true);
    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);
    fixture = TestBed.createComponent(EtsReportingSubmitComponent);
    await fixture.whenStable();
  });

  it.each(['non-USE', 'USE with fuels', 'USE without fuels'])(
    'submits %s using the empty action, prevents duplicate requests and confirms only after success',
    async (branch) => {
      const payload = branch === 'USE without fuels' ? mockCompletedEtsPayload() : mockEtsFuelPayload();
      payload.sectionsCompleted[ETS_ENERGY_FUEL_DETAILS_SUBTASK] = 'COMPLETED';
      if (branch === 'USE with fuels') {
        payload.emissionsTradingSchemeDataInputDetails.isUltraSmallEmitter = true;
        payload.emissionsTradingSchemeDataInputDetails.provideFuelDetails = true;
      }
      // UK ETS ID is not a submission prerequisite, including for reporting facilities outside UK ETS.
      delete payload.uketsId;
      store.setPayload(payload);
      const button = fixture.nativeElement.querySelector('button') as HTMLButtonElement;
      button.click();
      fixture.detectChanges();
      expect(processRequestTaskAction).toHaveBeenCalledExactlyOnceWith({
        requestTaskId: 123,
        requestTaskActionType: 'EMISSIONS_TRADING_SCHEME_DATA_SUBMIT_APPLICATION',
        requestTaskActionPayload: { payloadType: 'EMPTY_PAYLOAD' },
      });
      expect(button.disabled).toBe(true);
      expect(router.navigate).not.toHaveBeenCalled();
      button.click();
      expect(processRequestTaskAction).toHaveBeenCalledTimes(1);
      response.next(payload);
      response.complete();
      await fixture.whenStable();
      expect(router.navigate).toHaveBeenCalledExactlyOnceWith(['confirmation'], {
        relativeTo: expect.anything(),
        replaceUrl: true,
        state: { etsReportSubmitted: true },
      });
      expect(store.state.requestTaskItem.requestTask.payload).toEqual(payload);
    },
  );

  it.each([
    ['ETS1004', 'Some of the information in your UK ETS report could not be validated.'],
    ['ETS1005', 'The calculated totals in your UK ETS report could not be validated.'],
  ])('handles %s without losing saved answers and allows a successful retry', async (code, message) => {
    const original = store.state.requestTaskItem.requestTask.payload;
    fixture.nativeElement.querySelector('button').click();
    response.error(new HttpErrorResponse({ status: 400, error: { code } }));
    await fixture.whenStable();

    const summary = fixture.nativeElement.querySelector('.govuk-error-summary');
    expect(summary.textContent).toContain(message);
    expect(summary.querySelector('a').textContent).toContain('Check your UK ETS energy/fuel details');
    expect(document.activeElement).toBe(summary);
    expect(store.state.requestTaskItem.requestTask.payload).toBe(original);
    expect(router.navigate).not.toHaveBeenCalled();
    expect(fixture.nativeElement.querySelector('button').disabled).toBe(false);

    processRequestTaskAction.mockReturnValue(of(original));
    fixture.nativeElement.querySelector('button').click();
    await fixture.whenStable();

    expect(processRequestTaskAction).toHaveBeenCalledTimes(2);
    expect(fixture.nativeElement.querySelector('.govuk-error-summary')).toBeNull();
    expect(router.navigate).toHaveBeenCalledExactlyOnceWith(['confirmation'], {
      relativeTo: expect.anything(),
      replaceUrl: true,
      state: { etsReportSubmitted: true },
    });
  });

  it('keeps the report and page on failure, displays an error, and permits retry', async () => {
    const original = store.state.requestTaskItem.requestTask.payload;
    fixture.nativeElement.querySelector('button').click();
    response.error({ status: 500 });
    await fixture.whenStable();
    expect(router.navigate).not.toHaveBeenCalled();
    expect(store.state.requestTaskItem.requestTask.payload).toEqual(original);
    expect(fixture.nativeElement.querySelector('.govuk-error-summary').textContent).toContain(
      'Your UK ETS report could not be submitted',
    );
    expect(fixture.nativeElement.querySelector('button').disabled).toBe(false);
    const retry = new Subject();
    processRequestTaskAction.mockReturnValue(retry);
    fixture.nativeElement.querySelector('button').click();
    await fixture.whenStable();
    expect(fixture.nativeElement.querySelector('.govuk-error-summary')).toBeNull();
    expect(processRequestTaskAction).toHaveBeenCalledTimes(2);
    expect(router.navigate).not.toHaveBeenCalled();
    retry.error(new HttpErrorResponse({ status: 400, error: { code: 'UNKNOWN_ERROR' } }));
    await fixture.whenStable();
    expect(fixture.nativeElement.querySelector('.govuk-error-summary').textContent).toContain(
      'Your UK ETS report could not be submitted',
    );
    expect(store.state.requestTaskItem.requestTask.payload).toBe(original);
    expect(fixture.nativeElement.querySelector('button').disabled).toBe(false);
  });
});
