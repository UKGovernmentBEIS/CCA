import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';
import { Mocked } from 'vitest';

import { mockTprRequestTaskState, mockTprRequestTaskStateNoFuels } from '../../../testing/mock-data';
import { EnergyFuelAmountDetailsCheckYourAnswersComponent } from './energy-fuel-amount-details-check-your-answers.component';

describe('EnergyFuelAmountDetailsCheckYourAnswersComponent', () => {
  let component: EnergyFuelAmountDetailsCheckYourAnswersComponent;
  let fixture: ComponentFixture<EnergyFuelAmountDetailsCheckYourAnswersComponent>;
  let store: RequestTaskStore;
  let tasksApiService: Mocked<Pick<TasksApiService, 'saveRequestTaskAction'>>;
  let router: Router;

  beforeEach(async () => {
    tasksApiService = { saveRequestTaskAction: vi.fn().mockReturnValue(of({})) };

    await TestBed.configureTestingModule({
      imports: [EnergyFuelAmountDetailsCheckYourAnswersComponent],
      providers: [
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Report energy/fuel consumption' },
        { provide: TasksApiService, useValue: tasksApiService },
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
    store = TestBed.inject(RequestTaskStore);
    store.setState(mockTprRequestTaskState);

    fixture = TestBed.createComponent(EnergyFuelAmountDetailsCheckYourAnswersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => vi.clearAllMocks());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not show no-consumption message when non-zero fuels exist', () => {
    const element: HTMLElement = fixture.nativeElement;
    expect(element.textContent).not.toContain('has not used any electricity');
  });

  it('should show no-consumption message when all fuels are zero', () => {
    store.setState(mockTprRequestTaskStateNoFuels);
    fixture.detectChanges();

    const element: HTMLElement = fixture.nativeElement;
    expect(element.textContent).toContain('has not used any electricity');
  });

  it('should call the API with COMPLETED status and navigate on submit', () => {
    vi.spyOn(router, 'navigate');

    component.onSubmit();

    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalledWith(
      expect.objectContaining({
        requestTaskId: 42,
        requestTaskActionType: 'PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SAVE_APPLICATION',
        requestTaskActionPayload: expect.objectContaining({
          sectionsCompleted: expect.objectContaining({ tprEnergyFuelDetails: 'COMPLETED' }),
        }),
      }),
    );
    expect(router.navigate).toHaveBeenCalledWith(['../../..'], expect.any(Object));
  });
});
