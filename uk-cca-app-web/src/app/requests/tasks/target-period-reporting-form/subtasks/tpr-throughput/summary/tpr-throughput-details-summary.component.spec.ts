import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getByText } from '@testing';

import { PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload } from 'cca-api';

import { mockTprRequestTaskStateThroughputTotalsOnly } from '../../../testing/mock-data';
import { TprThroughputDetailsSummaryComponent } from './tpr-throughput-details-summary.component';

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

describe('TprThroughputDetailsSummaryComponent', () => {
  let component: TprThroughputDetailsSummaryComponent;
  let fixture: ComponentFixture<TprThroughputDetailsSummaryComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TprThroughputDetailsSummaryComponent],
      providers: [
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Provide target period throughput details' },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockTotalsState);

    fixture = TestBed.createComponent(TprThroughputDetailsSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render summary page heading', () => {
    expect(getByText(/Summary/, fixture.nativeElement)).toBeTruthy();
  });

  it('should build totals-only summary data', () => {
    expect(getByText(/Target period throughput details/, fixture.nativeElement)).toBeTruthy();
    expect(getByText(/Total throughput/, fixture.nativeElement)).toBeTruthy();
  });

  it('should render split-by-product summary when variable energy type is BY_PRODUCT', () => {
    store.setState(mockByProductState);
    fixture.detectChanges();

    expect(getByText(/Product name/, fixture.nativeElement)).toBeTruthy();
    expect(getByText(/Product A/, fixture.nativeElement)).toBeTruthy();
    expect(() => getByText(/Total throughput/, fixture.nativeElement)).toThrow();
  });
});
