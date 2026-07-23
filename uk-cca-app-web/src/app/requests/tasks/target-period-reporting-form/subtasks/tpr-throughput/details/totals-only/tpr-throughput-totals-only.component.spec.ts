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
import { TprThroughputTotalsOnlyComponent } from './tpr-throughput-totals-only.component';

const basePayload = mockTprRequestTaskStateThroughputTotalsOnly.requestTaskItem.requestTask
  .payload as PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload;

const mockFixedOnlyState = {
  ...mockTprRequestTaskStateThroughputTotalsOnly,
  requestTaskItem: {
    ...mockTprRequestTaskStateThroughputTotalsOnly.requestTaskItem,
    requestTask: {
      ...mockTprRequestTaskStateThroughputTotalsOnly.requestTaskItem.requestTask,
      payload: {
        ...basePayload,
        referenceData: {
          ...basePayload.referenceData,
          baselineAndTargets: {
            ...basePayload.referenceData?.baselineAndTargets,
            variableEnergyType: null,
            baselineVariableEnergy: null,
          },
        },
      },
    },
  },
} as RequestTaskState;
const mockInterimTotalsState = {
  ...mockTprRequestTaskStateThroughputTotalsOnly,
  requestTaskItem: {
    ...mockTprRequestTaskStateThroughputTotalsOnly.requestTaskItem,
    requestTask: {
      ...mockTprRequestTaskStateThroughputTotalsOnly.requestTaskItem.requestTask,
      payload: {
        ...basePayload,
        reportType: 'INTERIM',
      },
    },
  },
} as RequestTaskState;
const mockCarbonTotalsState = {
  ...mockTprRequestTaskStateThroughputTotalsOnly,
  requestTaskItem: {
    ...mockTprRequestTaskStateThroughputTotalsOnly.requestTaskItem,
    requestTask: {
      ...mockTprRequestTaskStateThroughputTotalsOnly.requestTaskItem.requestTask,
      payload: {
        ...basePayload,
        referenceData: {
          ...basePayload.referenceData,
          baselineAndTargets: {
            ...basePayload.referenceData?.baselineAndTargets,
            measurementType: 'CARBON_KG',
          },
        },
      },
    },
  },
} as RequestTaskState;

describe('TprThroughputTotalsOnlyComponent', () => {
  let component: TprThroughputTotalsOnlyComponent;
  let fixture: ComponentFixture<TprThroughputTotalsOnlyComponent>;
  let store: RequestTaskStore;
  let tasksApiService: Mocked<Pick<TasksApiService, 'saveRequestTaskAction'>>;

  beforeEach(async () => {
    tasksApiService = { saveRequestTaskAction: vi.fn().mockReturnValue(of(null)) };

    await TestBed.configureTestingModule({
      imports: [TprThroughputTotalsOnlyComponent],
      providers: [
        RequestTaskStore,
        { provide: Router, useValue: { navigate: vi.fn() } },
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TasksApiService, useValue: tasksApiService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockTprRequestTaskStateThroughputTotalsOnly);

    fixture = TestBed.createComponent(TprThroughputTotalsOnlyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the baseline throughput with its throughput unit in the baseline details', () => {
    expect(getByText(/Total baseline throughput \(tonnes\)/, fixture.nativeElement)).toBeTruthy();
    expect(() => getByText(/Total throughput \(kWh\)/, fixture.nativeElement)).toThrow();
  });

  it('should show the underlying agreement selection hint', () => {
    expect(
      getByText('This was selected when you applied for your underlying agreement', fixture.nativeElement),
    ).toBeTruthy();
  });

  it('should hide baseline intensity and total target variable energy for fixed-only facilities', async () => {
    store.setState(mockFixedOnlyState);
    fixture.detectChanges();
    await fixture.whenStable();

    expect(getByText(/No variable energy \(only fixed energy\)/, fixture.nativeElement)).toBeTruthy();
    expect(() =>
      getByText(/Baseline energy intensity|Baseline carbon dioxide \(CO2\) intensity/, fixture.nativeElement),
    ).toThrow();
    expect(() => getByText(/Total target variable energy/, fixture.nativeElement)).toThrow();
  });

  it('should show interim target label for interim reports', async () => {
    store.setState(mockInterimTotalsState);
    fixture.detectChanges();
    await fixture.whenStable();

    expect(getByText(/Interim target/, fixture.nativeElement)).toBeTruthy();
    expect(() => getByText(/Improvement target/, fixture.nativeElement)).toThrow();
  });

  it('should use carbon dioxide labels for carbon totals-only facilities', async () => {
    store.setState(mockCarbonTotalsState);
    fixture.detectChanges();
    await fixture.whenStable();

    expect(getByText(/Baseline carbon dioxide \(CO2\) intensity/, fixture.nativeElement)).toBeTruthy();
    expect(getByText(/Total target variable carbon dioxide/, fixture.nativeElement)).toBeTruthy();
  });
});
