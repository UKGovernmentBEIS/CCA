import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskState, RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';
import { getByText } from '@testing';
import { Mocked } from 'vitest';

import { PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload } from 'cca-api';

import { mockTprRequestTaskStateThroughputTotalsOnly } from '../../../../testing/mock-data';
import { TprThroughputSplitByProductComponent } from './tpr-throughput-split-by-product.component';

const mockByProductState = {
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
            variableEnergyType: 'BY_PRODUCT',
            baselineDate: '2022-01-01',
            variableEnergyConsumptionDataByProduct: [
              {
                productName: 'Blue Widgets',
                baselineYear: 2022,
                productStatus: 'LIVE',
                energy: '1000',
                throughput: '1000',
                throughputUnit: 'tonnes',
              },
              {
                productName: 'Green Widgets',
                baselineYear: 2022,
                productStatus: 'LIVE',
                energy: '2000',
                throughput: '500',
                throughputUnit: 'tonnes',
              },
            ],
          },
        },
        performanceData: {
          ...(
            mockTprRequestTaskStateThroughputTotalsOnly.requestTaskItem.requestTask
              .payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload
          ).performanceData,
          throughputDetails: {
            totalTargetVariableEnergy: '0',
            variableEnergyConsumptionDataByProduct: [
              {
                productName: 'Blue Widgets',
                actualThroughput: '321.1234567',
                targetImprovement: '8',
                adjustedThroughput: '321.1234567',
                targetEnergy: '295.4335802',
              },
              {
                productName: 'Green Widgets',
                actualThroughput: '100',
                targetImprovement: '8',
                adjustedThroughput: '100',
                targetEnergy: '184000',
              },
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
          calculatedResults: {
            actualEnergyCarbon: '0',
            targetEnergyCarbon: '0',
            energyCarbonDifference: '0',
            targetImprovement: '0',
            weightedConversionFactor: '0',
            targetCo2Emissions: '0',
            actualCo2Emissions: '0',
            co2EmissionsDifference: '0',
            actualImprovement: '0',
          },
        },
      },
    },
  },
};

const mockInterimByProductState = {
  ...mockByProductState,
  requestTaskItem: {
    ...mockByProductState.requestTaskItem,
    requestTask: {
      ...mockByProductState.requestTaskItem.requestTask,
      payload: {
        ...mockByProductState.requestTaskItem.requestTask.payload,
        reportType: 'INTERIM',
      },
    },
  },
};

describe('TprThroughputSplitByProductComponent', () => {
  let component: TprThroughputSplitByProductComponent;
  let fixture: ComponentFixture<TprThroughputSplitByProductComponent>;
  let store: RequestTaskStore;
  let tasksApiService: Mocked<Pick<TasksApiService, 'saveRequestTaskAction'>>;

  beforeEach(async () => {
    tasksApiService = { saveRequestTaskAction: vi.fn().mockReturnValue(of(null)) };

    await TestBed.configureTestingModule({
      imports: [TprThroughputSplitByProductComponent],
      providers: [
        RequestTaskStore,
        { provide: Router, useValue: { navigate: vi.fn() } },
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TasksApiService, useValue: tasksApiService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockByProductState);

    fixture = TestBed.createComponent(TprThroughputSplitByProductComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  });

  afterEach(() => {
    TestBed.resetTestingModule();
  });

  it('should create', async () => {
    expect(component).toBeTruthy();
  });

  it('should prepopulate actual throughput from saved throughput details', async () => {
    const actualThroughput = component['form'].controls.products.at(0).controls.actualThroughput.value;

    expect(actualThroughput).toBe(321.1234567);
  });

  it('should show the underlying agreement selection hint', () => {
    expect(
      getByText('This was selected when you applied for your underlying agreement', fixture.nativeElement),
    ).toBeTruthy();
  });

  it('should calculate total target variable energy from baseline intensities and apply facility improvement once', async () => {
    const expected = ((1000 / 1000) * 321.1234567 + (2000 / 500) * 100) * (1 - 0.12);

    expect(component['totalTargetVariableEnergy']()).toBeCloseTo(expected, 7);
  });

  it('should show interim target header for interim reports', async () => {
    store.setState(mockInterimByProductState as RequestTaskState);
    fixture.detectChanges();
    await fixture.whenStable();

    expect(getByText(/Interim target %/, fixture.nativeElement)).toBeTruthy();
    expect(() => getByText(/Improvement target %/, fixture.nativeElement)).toThrow();
  });
});
