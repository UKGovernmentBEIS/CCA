import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of, throwError } from 'rxjs';

import {
  ITEM_TYPE_TO_RETURN_TEXT_MAPPER,
  RequestTaskState,
  RequestTaskStore,
  TYPE_AWARE_STORE,
} from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';

import { EtsReportingTaskPayload } from '../../../ets-reporting.types';
import { EnergyFuelAmountDetailsSummaryComponent } from './energy-fuel-amount-details-summary.component';

describe('ETS completed energy/fuel summary', () => {
  let fixture: ComponentFixture<EnergyFuelAmountDetailsSummaryComponent>;
  let store: RequestTaskStore;
  const tasksApiService = { saveRequestTaskAction: vi.fn() };

  beforeEach(async () => {
    tasksApiService.saveRequestTaskAction.mockReset().mockReturnValue(of({}));
    await TestBed.configureTestingModule({
      imports: [EnergyFuelAmountDetailsSummaryComponent],
      providers: [
        RequestTaskStore,
        { provide: TasksApiService, useValue: tasksApiService },
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'UK ETS reporting - Submit form' },
      ],
    }).compileComponents();
    store = TestBed.inject(RequestTaskStore);
    store.setState({
      requestTaskItem: {
        requestTask: {
          id: 42,
          type: 'EMISSIONS_TRADING_SCHEME_DATA_SUBMIT',
          payload: {
            payloadType: 'EMISSIONS_TRADING_SCHEME_DATA_SUBMIT_PAYLOAD',
            measurementType: 'CARBON_KG',
            emissionsTradingSchemeDataInputDetails: {
              isUltraSmallEmitter: true,
              provideFuelDetails: true,
              energyFuelDetails: {
                standardFuels: { NATURAL_GAS: { deliveredEnergy: '1000', primaryEnergy: '182.54' } },
                totalPrimaryEnergy: '182.54',
                totalCo2Emissions: '0.18254',
              },
            },
            sectionsCompleted: { etsEnergyFuelDetails: 'COMPLETED' },
          },
        },
        requestInfo: { id: 'ETS-1', type: 'EMISSIONS_TRADING_SCHEME_DATA', accountId: 1 },
      },
      relatedTasks: [],
      timeline: [],
      taskReassignedTo: null,
      isEditable: false,
    } as RequestTaskState);
    fixture = TestBed.createComponent(EnergyFuelAmountDetailsSummaryComponent);
    fixture.detectChanges();
  });

  it('renders persisted correction answers and carbon data without completion or editing controls', () => {
    const element: HTMLElement = fixture.nativeElement;
    expect(element.querySelector('h1').textContent).toBe('Summary');
    expect(element.textContent).toContain('Natural gas');
    expect(element.textContent).toContain('182.54 kg');
    expect(element.querySelector('button')).toBeNull();
    expect(element.querySelector('cca-ets-energy-fuel-summary a')).toBeNull();
    expect(element.querySelector('netz-return-to-task-or-action-page').previousElementSibling.tagName).toBe('HR');
  });

  it('allows changes when the completed task remains editable', () => {
    store.setState({ ...store.state, isEditable: true });
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelectorAll('cca-ets-energy-fuel-summary a')).toHaveLength(3);
    expect(fixture.nativeElement.querySelector('button')).toBeNull();
  });

  it('requires confirmation again after deleting a fuel from a completed summary, and preserves data on failure', () => {
    const current = store.state.requestTaskItem.requestTask.payload as EtsReportingTaskPayload;
    store.setPayload({
      ...current,
      emissionsTradingSchemeDataInputDetails: {
        ...current.emissionsTradingSchemeDataInputDetails,
        energyFuelDetails: {
          ...current.emissionsTradingSchemeDataInputDetails.energyFuelDetails,
          nonStandardFuels: [{ name: 'Custom', conversionFactor: '0.5', deliveredEnergy: '100', primaryEnergy: '50' }],
        },
      },
    } as EtsReportingTaskPayload);
    fixture.componentInstance.onRemoveFuel(0);
    expect(tasksApiService.saveRequestTaskAction).not.toHaveBeenCalled();
    store.setState({ ...store.state, isEditable: true });
    const original = store.state.requestTaskItem.requestTask.payload;
    const navigate = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    tasksApiService.saveRequestTaskAction.mockReturnValueOnce(throwError(() => new Error('Failed')));
    fixture.detectChanges();
    fixture.nativeElement.querySelector('cca-ets-energy-fuel-summary button').click();
    fixture.detectChanges();
    expect(navigate).not.toHaveBeenCalled();
    expect(store.state.requestTaskItem.requestTask.payload).toBe(original);
    expect(fixture.nativeElement.textContent).toContain('The fuel could not be deleted. Please try again.');
    fixture.nativeElement.querySelector('cca-ets-energy-fuel-summary button').click();
    expect(tasksApiService.saveRequestTaskAction).toHaveBeenLastCalledWith(
      expect.objectContaining({
        requestTaskActionPayload: expect.objectContaining({
          sectionsCompleted: { etsEnergyFuelDetails: 'IN_PROGRESS' },
          emissionsTradingSchemeDataInputDetails: expect.objectContaining({
            energyFuelDetails: expect.objectContaining({
              nonStandardFuels: [],
              totalPrimaryEnergy: '182.54',
              totalCo2Emissions: '0.18254',
            }),
          }),
        }),
      }),
    );
    expect(navigate).toHaveBeenCalledWith(['../check-your-answers'], expect.objectContaining({ replaceUrl: true }));
  });
});
