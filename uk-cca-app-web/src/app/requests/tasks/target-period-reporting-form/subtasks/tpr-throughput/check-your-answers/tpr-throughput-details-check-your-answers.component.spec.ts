import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import {
  ITEM_TYPE_TO_RETURN_TEXT_MAPPER,
  RequestTaskState,
  RequestTaskStore,
  TYPE_AWARE_STORE,
} from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TaskItemStatus, TasksApiService, TPR_FORM_THROUGHPUT_DETAILS_SUBTASK } from '@requests/common';
import { click, getByRole, getByText } from '@testing';
import { Mocked } from 'vitest';

import {
  PerformanceDataFacilityDigitalFormSaveRequestTaskActionPayload,
  PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload,
} from 'cca-api';

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
} as RequestTaskState;

const mockDirtyTotalsState = {
  ...mockTotalsState,
  requestTaskItem: {
    ...mockTotalsState.requestTaskItem,
    requestTask: {
      ...mockTotalsState.requestTaskItem.requestTask,
      payload: {
        ...(mockTotalsState.requestTaskItem.requestTask
          .payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload),
        performanceData: {
          ...(
            mockTotalsState.requestTaskItem.requestTask
              .payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload
          ).performanceData,
          throughputDetails: {
            ...(
              mockTotalsState.requestTaskItem.requestTask
                .payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload
            ).performanceData.throughputDetails,
            targetImprovement: '0.25',
            totalTargetVariableEnergy: '1057.1452187',
          },
        },
      },
    },
  },
};

const mockByProductState = {
  ...mockTotalsState,
  requestTaskItem: {
    ...mockTotalsState.requestTaskItem,
    requestTask: {
      ...mockTotalsState.requestTaskItem.requestTask,
      payload: {
        ...(mockTotalsState.requestTaskItem.requestTask
          .payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload),
        referenceData: {
          ...(
            mockTotalsState.requestTaskItem.requestTask
              .payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload
          ).referenceData,
          baselineAndTargets: {
            ...(
              mockTotalsState.requestTaskItem.requestTask
                .payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload
            ).referenceData?.baselineAndTargets,
            variableEnergyType: 'BY_PRODUCT',
            baselineDate: '2022-01-01',
            variableEnergyConsumptionDataByProduct: [
              {
                productName: 'Blue Widgets',
                baselineYear: 2022,
                productStatus: 'LIVE',
                energy: '1000',
                throughputUnit: 'tonnes',
                throughput: '1000',
              },
              {
                productName: 'Green Widgets',
                baselineYear: 2022,
                productStatus: 'LIVE',
                energy: '2000',
                throughputUnit: 'tonnes',
                throughput: '500',
              },
            ],
          },
        },
        performanceData: {
          ...(
            mockTotalsState.requestTaskItem.requestTask
              .payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload
          ).performanceData,
          throughputDetails: {
            variableEnergyConsumptionDataByProduct: [
              { productName: 'Blue Widgets', actualThroughput: '321.1234567' },
              { productName: 'Green Widgets', actualThroughput: '100' },
            ],
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
} as RequestTaskState;

const mockDirtyByProductState = {
  ...mockByProductState,
  requestTaskItem: {
    ...mockByProductState.requestTaskItem,
    requestTask: {
      ...mockByProductState.requestTaskItem.requestTask,
      payload: {
        ...mockByProductState.requestTaskItem.requestTask.payload,
        performanceData: {
          ...(
            mockByProductState.requestTaskItem.requestTask
              .payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload
          ).performanceData,
          throughputDetails: {
            ...(
              mockByProductState.requestTaskItem.requestTask
                .payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload
            ).performanceData.throughputDetails,
            totalTargetVariableEnergy: '16.0983835',
            variableEnergyConsumptionDataByProduct: [
              {
                productName: 'Blue Widgets',
                actualThroughput: '321.1234567',
                targetImprovement: '0.2000000',
                adjustedThroughput: '321.1234567',
                targetEnergy: '5.8252623',
              },
              {
                productName: 'Green Widgets',
                actualThroughput: '100',
                targetImprovement: '0.0769231',
                adjustedThroughput: '100',
                targetEnergy: '13.0919386',
              },
            ],
          },
        },
      },
    },
  },
} as RequestTaskState;

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

    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalledTimes(1);

    expect(tasksApiService.saveRequestTaskAction).toHaveBeenNthCalledWith(
      1,
      expect.objectContaining({
        requestTaskId: 42,
        requestTaskActionPayload: expect.objectContaining({
          sectionsCompleted: expect.objectContaining({
            [TPR_FORM_THROUGHPUT_DETAILS_SUBTASK]: TaskItemStatus.COMPLETED,
          }),
          energyFuelDetails: expect.any(Object),
          throughputDetails: expect.any(Object),
        }),
      }),
    );

    expect(router.navigate).toHaveBeenCalledWith(['../../..'], expect.any(Object));
  });

  it('should normalize stored throughput values before submit', () => {
    store.setState(mockDirtyTotalsState);
    fixture.detectChanges();

    click(getByRole('button', { name: /Confirm and complete/i }, fixture.nativeElement));

    const submittedPayload = (
      tasksApiService.saveRequestTaskAction.mock.calls[0][0]
        .requestTaskActionPayload as PerformanceDataFacilityDigitalFormSaveRequestTaskActionPayload
    ).throughputDetails;

    expect(Number(submittedPayload.targetImprovement)).toBeCloseTo(0.25, 7);
    expect(Number(submittedPayload.totalTargetVariableEnergy)).toBeCloseTo(3520, 7);
  });

  it('should rebuild by-product throughput data before submit using intensity-based calculations', () => {
    store.setState(mockDirtyByProductState);
    fixture.detectChanges();

    click(getByRole('button', { name: /Confirm and complete/i }, fixture.nativeElement));

    const submittedPayload = (
      tasksApiService.saveRequestTaskAction.mock.calls[0][0]
        .requestTaskActionPayload as PerformanceDataFacilityDigitalFormSaveRequestTaskActionPayload
    ).throughputDetails;

    const productData = submittedPayload.variableEnergyConsumptionDataByProduct ?? [];

    expect(Number(submittedPayload.totalTargetVariableEnergy)).toBeCloseTo(
      ((1000 / 1000) * 321.1234567 + (2000 / 500) * 100) * (1 - 0.12),
      7,
    );
    expect(Number(productData[0].targetImprovement)).toBeCloseTo(0.12, 7);
    expect(Number(productData[1].targetImprovement)).toBeCloseTo(0.12, 7);
    expect(Number(productData[0].targetEnergy)).toBeCloseTo((1000 / 1000) * 321.1234567 * (1 - 0.12), 7);
    expect(Number(productData[1].targetEnergy)).toBeCloseTo((2000 / 500) * 100 * (1 - 0.12), 7);
  });

  it('should calculate BY_PRODUCT total using per-product improvement targets when baseline years differ', () => {
    // Green Widgets baselineYear=2024 > facilityBaseYear=2022 → adjusted target = 1/12
    const state = {
      ...mockByProductState,
      requestTaskItem: {
        ...mockByProductState.requestTaskItem,
        requestTask: {
          ...mockByProductState.requestTaskItem.requestTask,
          payload: {
            ...mockByProductState.requestTaskItem.requestTask.payload,
            referenceData: {
              ...(
                mockByProductState.requestTaskItem.requestTask
                  .payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload
              ).referenceData,
              baselineAndTargets: {
                ...(
                  mockByProductState.requestTaskItem.requestTask
                    .payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload
                ).referenceData?.baselineAndTargets,
                variableEnergyConsumptionDataByProduct: [
                  {
                    productName: 'Blue Widgets',
                    baselineYear: 2022,
                    productStatus: 'LIVE',
                    energy: '1000',
                    throughputUnit: 'tonnes',
                    throughput: '1000',
                  },
                  {
                    productName: 'Green Widgets',
                    baselineYear: 2024,
                    productStatus: 'LIVE',
                    energy: '2000',
                    throughputUnit: 'tonnes',
                    throughput: '500',
                  },
                ],
              },
            },
          },
        },
      },
    } as RequestTaskState;

    store.setState(state);
    fixture.detectChanges();

    click(getByRole('button', { name: /Confirm and complete/i }, fixture.nativeElement));

    const submittedPayload = (
      tasksApiService.saveRequestTaskAction.mock.calls[0][0]
        .requestTaskActionPayload as PerformanceDataFacilityDigitalFormSaveRequestTaskActionPayload
    ).throughputDetails;

    const productData = submittedPayload.variableEnergyConsumptionDataByProduct ?? [];

    // Blue Widgets: baselineYear=2022 (same as facility) → facility improvement target 0.12
    expect(Number(productData[0].targetImprovement)).toBeCloseTo(0.12, 7);
    expect(Number(productData[0].targetEnergy)).toBeCloseTo((1000 / 1000) * 321.1234567 * (1 - 0.12), 7);

    // Green Widgets: baselineYear=2024 (after facility) → adjusted target (0.12 - 0.04) / 0.96 = 1/12
    expect(Number(productData[1].targetImprovement)).toBeCloseTo(1 / 12, 7);
    expect(Number(productData[1].targetEnergy)).toBeCloseTo((2000 / 500) * 100 * (1 - 1 / 12), 7);

    // Total = sum of each product's targetEnergy using their respective improvement targets
    // (not the old behaviour: applying facility target once to the raw sum of intensities × throughput)
    const expectedTotal = (1000 / 1000) * 321.1234567 * (1 - 0.12) + (2000 / 500) * 100 * (1 - 1 / 12);

    expect(Number(submittedPayload.totalTargetVariableEnergy)).toBeCloseTo(expectedTotal, 7);
  });

  it('should render split-by-product summary when variable energy type is BY_PRODUCT', () => {
    store.setState(mockByProductState);
    fixture.detectChanges();

    expect(getByText(/Product name/, fixture.nativeElement)).toBeTruthy();
    expect(getByText(/Blue Widgets/, fixture.nativeElement)).toBeTruthy();
    expect(() => getByText(/Total throughput/, fixture.nativeElement)).toThrow();
  });
});
