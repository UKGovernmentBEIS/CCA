import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of, Subject, tap, throwError } from 'rxjs';

import { PendingRequestService } from '@netz/common/services';
import {
  ITEM_TYPE_TO_RETURN_TEXT_MAPPER,
  RequestTaskState,
  RequestTaskStore,
  TYPE_AWARE_STORE,
} from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';

import { EtsReportingTaskPayload } from '../../../ets-reporting.types';
import { EnergyFuelAmountDetailsCheckYourAnswersComponent } from './energy-fuel-amount-details-check-your-answers.component';

describe('ETS energy/fuel check your answers', () => {
  let fixture: ComponentFixture<EnergyFuelAmountDetailsCheckYourAnswersComponent>;
  let store: RequestTaskStore;
  let router: Router;
  const tasksApiService = { saveRequestTaskAction: vi.fn() };
  const payload: EtsReportingTaskPayload = {
    payloadType: 'EMISSIONS_TRADING_SCHEME_DATA_SUBMIT_PAYLOAD',
    targetYear: 2026,
    measurementType: 'ENERGY_KWH',
    emissionsTradingSchemeDataInputDetails: { isUltraSmallEmitter: true, provideFuelDetails: false },
    sectionsCompleted: { etsEnergyFuelDetails: 'IN_PROGRESS', anotherSection: 'COMPLETED' },
  };

  function setState(nextPayload: EtsReportingTaskPayload = payload, isEditable = true): void {
    store.setState({
      requestTaskItem: {
        requestTask: { id: 42, type: 'EMISSIONS_TRADING_SCHEME_DATA_SUBMIT', payload: nextPayload },
        allowedRequestTaskActions: ['EMISSIONS_TRADING_SCHEME_DATA_SAVE_APPLICATION'],
        requestInfo: { id: 'ETS-1', type: 'EMISSIONS_TRADING_SCHEME_DATA', accountId: 1 },
      },
      relatedTasks: [],
      timeline: [],
      taskReassignedTo: null,
      isEditable,
    } as RequestTaskState);
  }

  beforeEach(async () => {
    tasksApiService.saveRequestTaskAction.mockReset().mockReturnValue(of({}));
    await TestBed.configureTestingModule({
      imports: [EnergyFuelAmountDetailsCheckYourAnswersComponent],
      providers: [
        RequestTaskStore,
        PendingRequestService,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'UK ETS reporting - Submit form' },
        { provide: TasksApiService, useValue: tasksApiService },
      ],
    }).compileComponents();
    store = TestBed.inject(RequestTaskStore);
    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);
    setState();
    fixture = TestBed.createComponent(EnergyFuelAmountDetailsCheckYourAnswersComponent);
    fixture.detectChanges();
  });

  it('saves the complete input and preserves other section statuses before returning to the task', () => {
    fixture.componentInstance.onSubmit();
    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalledWith({
      requestTaskId: 42,
      requestTaskActionType: 'EMISSIONS_TRADING_SCHEME_DATA_SAVE_APPLICATION',
      requestTaskActionPayload: {
        payloadType: 'EMISSIONS_TRADING_SCHEME_DATA_SAVE_PAYLOAD',
        emissionsTradingSchemeDataInputDetails: payload.emissionsTradingSchemeDataInputDetails,
        sectionsCompleted: { etsEnergyFuelDetails: 'COMPLETED', anotherSection: 'COMPLETED' },
      },
    });
    expect(router.navigate).toHaveBeenCalledWith(['../../..'], expect.objectContaining({ replaceUrl: true }));
    expect(store.state.requestTaskItem.requestTask.payload).toEqual(payload);
  });

  function withCustomFuel(): EtsReportingTaskPayload {
    return {
      ...payload,
      emissionsTradingSchemeDataInputDetails: {
        isUltraSmallEmitter: false,
        energyFuelDetails: {
          standardFuels: { NATURAL_GAS: { deliveredEnergy: '1000', primaryEnergy: '1000' } },
          nonStandardFuels: [
            { name: 'Custom fuel', conversionFactor: '0.5', deliveredEnergy: '100', primaryEnergy: '100' },
          ],
          totalPrimaryEnergy: '1100',
          totalCo2Emissions: '0.23254',
        },
      },
    };
  }

  it('deletes the clicked fuel and displays the saved totals without completing the section', () => {
    const current = withCustomFuel();
    setState(current);
    tasksApiService.saveRequestTaskAction.mockImplementation((dto) =>
      of(dto.requestTaskActionPayload).pipe(
        tap((response) => store.setPayload({ ...current, ...response, payloadType: current.payloadType })),
      ),
    );
    fixture.detectChanges();
    fixture.nativeElement.querySelector('cca-ets-energy-fuel-summary button').click();
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('tbody').textContent).not.toContain('Custom fuel');
    expect(fixture.nativeElement.textContent).toContain('0.18254 tCO2e');
    expect((store.state.requestTaskItem.requestTask.payload as EtsReportingTaskPayload).sectionsCompleted).toEqual({
      etsEnergyFuelDetails: 'IN_PROGRESS',
      anotherSection: 'COMPLETED',
    });
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('prevents concurrent deletion and completion, preserves the fuel on failure, and allows retry', () => {
    const current = withCustomFuel();
    setState(current);
    const response = new Subject();
    tasksApiService.saveRequestTaskAction.mockReturnValue(
      response.pipe(TestBed.inject(PendingRequestService).trackRequest()),
    );
    fixture.detectChanges();
    fixture.nativeElement.querySelector('cca-ets-energy-fuel-summary button').click();
    fixture.detectChanges();
    fixture.nativeElement.querySelectorAll('button').forEach((button: HTMLButtonElement) => button.click());
    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(
      [...fixture.nativeElement.querySelectorAll('button')].every((button: HTMLButtonElement) => button.disabled),
    ).toBe(true);
    response.error(new Error('Failed'));
    fixture.detectChanges();
    expect(store.state.requestTaskItem.requestTask.payload).toEqual(current);
    expect(fixture.nativeElement.textContent).toContain('The fuel could not be deleted. Please try again.');
    expect(fixture.nativeElement.querySelector('tbody').textContent).toContain('Custom fuel');
    tasksApiService.saveRequestTaskAction.mockReturnValue(of({}));
    fixture.nativeElement.querySelector('cca-ets-energy-fuel-summary button').click();
    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalledTimes(2);
  });

  it('does not navigate or complete the section after a failed save and allows retry', () => {
    tasksApiService.saveRequestTaskAction.mockReturnValue(throwError(() => new Error('Failed')));
    fixture.componentInstance.onSubmit();
    fixture.detectChanges();
    expect(router.navigate).not.toHaveBeenCalled();
    expect(fixture.nativeElement.querySelector('.govuk-error-summary').textContent).toContain(
      'Your answers could not be saved. Please try again.',
    );
    expect(store.state.requestTaskItem.requestTask.payload).toEqual(payload);
    tasksApiService.saveRequestTaskAction.mockReturnValue(of({}));
    fixture.componentInstance.onSubmit();
    expect(router.navigate).toHaveBeenCalledTimes(1);
  });

  it('keeps the button pending and prevents duplicate submissions until the save finishes', () => {
    const response = new Subject();
    tasksApiService.saveRequestTaskAction.mockReturnValue(
      response.pipe(TestBed.inject(PendingRequestService).trackRequest()),
    );
    fixture.nativeElement.querySelector('button').click();
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('button').disabled).toBe(true);
    fixture.nativeElement.querySelector('button').click();
    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(router.navigate).not.toHaveBeenCalled();
    response.next({});
    response.complete();
    expect(router.navigate).toHaveBeenCalledTimes(1);
  });

  it.each([
    undefined,
    { isUltraSmallEmitter: true },
    { isUltraSmallEmitter: false },
    { isUltraSmallEmitter: true, provideFuelDetails: true },
    {
      isUltraSmallEmitter: false,
      energyFuelDetails: { standardFuels: {}, totalPrimaryEnergy: '0', totalCo2Emissions: '0' },
    },
  ])('prevents invalid branch completion even when a stale section is marked completed: %j', (details) => {
    setState({
      ...payload,
      emissionsTradingSchemeDataInputDetails: details,
      sectionsCompleted: { etsEnergyFuelDetails: 'COMPLETED' },
    });
    fixture.componentInstance.onSubmit();
    fixture.detectChanges();
    expect(tasksApiService.saveRequestTaskAction).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
    expect(fixture.nativeElement.querySelector('.govuk-error-summary').textContent).toContain(
      'Complete the UK ETS energy/fuel details',
    );
  });

  it('completes the fuel-required branch with valid calculated data', () => {
    setState({
      ...payload,
      emissionsTradingSchemeDataInputDetails: {
        isUltraSmallEmitter: false,
        energyFuelDetails: {
          standardFuels: { NATURAL_GAS: { deliveredEnergy: '1000', primaryEnergy: '1000' } },
          totalPrimaryEnergy: '1000',
          totalCo2Emissions: '0.18254',
        },
      },
    });
    fixture.componentInstance.onSubmit();
    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(router.navigate).toHaveBeenCalledTimes(1);
  });

  it('omits editing and completion controls in read-only mode, and does not save', () => {
    setState(payload, false);
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('button')).toBeNull();
    expect(fixture.nativeElement.querySelector('cca-ets-energy-fuel-summary a')).toBeNull();
    fixture.componentInstance.onSubmit();
    fixture.componentInstance.onRemoveFuel(0);
    expect(tasksApiService.saveRequestTaskAction).not.toHaveBeenCalled();
  });
});
