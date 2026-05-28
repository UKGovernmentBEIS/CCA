import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TaskItemStatus, TasksApiService, TPR_FORM_THROUGHPUT_DETAILS_SUBTASK } from '@requests/common';
import { click, getByRole, getByText } from '@testing';
import { Mocked } from 'vitest';

import { PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload } from 'cca-api';

import { mockTprRequestTaskStateThroughputTotalsOnly } from '../../../testing/mock-data';
import { TprThroughputDetailsCheckYourAnswersComponent } from './tpr-throughput-details-check-your-answers.component';

const mockTotalsState = {
  ...mockTprRequestTaskStateThroughputTotalsOnly,
  requestTaskItem: {
    ...mockTprRequestTaskStateThroughputTotalsOnly.requestTaskItem,
    requestTask: {
      ...mockTprRequestTaskStateThroughputTotalsOnly.requestTaskItem.requestTask,
      payload: {
        ...mockTprRequestTaskStateThroughputTotalsOnly.requestTaskItem.requestTask.payload,
        referenceData: {
          ...(
            mockTprRequestTaskStateThroughputTotalsOnly.requestTaskItem.requestTask
              .payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload
          ).referenceData,
          baselineAndTargets: {
            ...(
              mockTprRequestTaskStateThroughputTotalsOnly.requestTaskItem.requestTask
                .payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload
            ).referenceData?.baselineAndTargets,
            variableEnergyType: 'TOTALS',
          },
        },
      },
    },
  },
} as any;

const mockByProductState = {
  ...mockTotalsState,
  requestTaskItem: {
    ...mockTotalsState.requestTaskItem,
    requestTask: {
      ...mockTotalsState.requestTaskItem.requestTask,
      payload: {
        ...mockTotalsState.requestTaskItem.requestTask.payload,
        referenceData: {
          ...mockTotalsState.requestTaskItem.requestTask.payload.referenceData,
          baselineAndTargets: {
            ...mockTotalsState.requestTaskItem.requestTask.payload.referenceData.baselineAndTargets,
            variableEnergyType: 'BY_PRODUCT',
            baselineDate: '2022-01-01',
            variableEnergyConsumptionDataByProduct: [
              {
                productName: 'Product A',
                baselineYear: 2022,
                productStatus: 'LIVE',
                energy: '1',
                throughputUnit: 'tonnes',
                throughput: '1',
              },
            ],
          },
        },
        performanceData: {
          ...mockTotalsState.requestTaskItem.requestTask.payload.performanceData,
          throughputDetails: {
            variableEnergyConsumptionDataByProduct: [{ productName: 'Product A', actualThroughput: '10' }],
          },
          energyFuelDetails: {
            standardFuels: {
              GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '0' },
              NON_GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '0' },
            },
            atLeastSeventyPercentEnergyUsed: false,
            electricitySuppliedFromCHP: '0',
          },
        },
      },
    },
  },
} as any;

describe('TprThroughputDetailsCheckYourAnswersComponent', () => {
  let component: TprThroughputDetailsCheckYourAnswersComponent;
  let fixture: ComponentFixture<TprThroughputDetailsCheckYourAnswersComponent>;
  let store: RequestTaskStore;
  let router: Router;
  let tasksApiService: Mocked<Pick<TasksApiService, 'saveRequestTaskAction'>>;

  beforeEach(async () => {
    tasksApiService = { saveRequestTaskAction: vi.fn().mockReturnValue(of({})) };

    await TestBed.configureTestingModule({
      imports: [TprThroughputDetailsCheckYourAnswersComponent],
      providers: [
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Provide target period throughput details' },
        { provide: TasksApiService, useValue: tasksApiService },
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
    store = TestBed.inject(RequestTaskStore);
    store.setState(mockTotalsState);

    fixture = TestBed.createComponent(TprThroughputDetailsCheckYourAnswersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => vi.clearAllMocks());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should build summary data with throughput section', () => {
    expect(getByText(/Target period throughput details/, fixture.nativeElement)).toBeTruthy();
    expect(getByText(/Total throughput/, fixture.nativeElement)).toBeTruthy();
  });

  it('should call api with completed status and navigate on submit', () => {
    vi.spyOn(router, 'navigate');

    click(getByRole('button', { name: /Confirm and complete/i }, fixture.nativeElement));

    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalledWith(
      expect.objectContaining({
        requestTaskId: 42,
        requestTaskActionPayload: expect.objectContaining({
          sectionsCompleted: expect.objectContaining({
            [TPR_FORM_THROUGHPUT_DETAILS_SUBTASK]: TaskItemStatus.COMPLETED,
          }),
        }),
      }),
    );
    expect(router.navigate).toHaveBeenCalledWith(['../../..'], expect.any(Object));
  });

  it('should render split-by-product summary when variable energy type is BY_PRODUCT', () => {
    store.setState(mockByProductState);
    fixture.detectChanges();

    expect(getByText(/Product name/, fixture.nativeElement)).toBeTruthy();
    expect(getByText(/Product A/, fixture.nativeElement)).toBeTruthy();
    expect(() => getByText(/Total throughput/, fixture.nativeElement)).toThrow();
  });
});
