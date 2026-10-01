import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskState, RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { roundHalfUpTo7Decimals, TasksApiService } from '@requests/common';
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

  it('should remain rendered when actual throughput contains invalid characters', () => {
    component['form'].controls.actualThroughput.setValue('`');

    expect(() => fixture.detectChanges()).not.toThrow();
    expect(component['form'].controls.actualThroughput.invalid).toBe(true);
    expect(component.adjustedThroughput()).toBeNull();
    expect(component.targetVariableEnergy()).toBeNull();
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

  it('should produce display values matching roundHalfUpTo7Decimals for computed values', () => {
    // baselineEnergyIntensity = 1000/3 ≈ 333.333333..., target = 333.333... × 1000 × 0.88 ≈ 293333.333333...
    const precisionState = {
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
                baselineVariableEnergy: '1000',
                totalThroughput: '3',
              },
            },
            performanceData: {
              ...basePayload.performanceData,
              throughputDetails: {
                ...basePayload.performanceData?.throughputDetails,
                actualThroughput: '1000',
              },
            },
          },
        },
      },
    } as RequestTaskState;

    store.setState(precisionState);
    fixture.detectChanges();

    const target = component.targetVariableEnergy();
    const displayed = component.displayRounded(target!);
    const apiString = roundHalfUpTo7Decimals(target!);

    expect(displayed.toFixed(7)).toBe(Number(apiString).toFixed(7));

    const intensity = component.baselineEnergyIntensity();
    const displayedIntensity = component.displayRounded(intensity!);
    const apiIntensity = roundHalfUpTo7Decimals(intensity!);

    expect(displayedIntensity.toFixed(7)).toBe(Number(apiIntensity).toFixed(7));
  });

  it('should set zeroEnergy form-level error when delivered energy is 0 and baseline total is non-zero', async () => {
    const zeroEnergyState = {
      ...mockTprRequestTaskStateThroughputTotalsOnly,
      requestTaskItem: {
        ...mockTprRequestTaskStateThroughputTotalsOnly.requestTaskItem,
        requestTask: {
          ...mockTprRequestTaskStateThroughputTotalsOnly.requestTaskItem.requestTask,
          payload: {
            ...basePayload,
            performanceData: {
              ...basePayload.performanceData,
              energyFuelDetails: {
                ...basePayload.performanceData?.energyFuelDetails,
                standardFuels: {
                  GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '0' },
                  NON_GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '0' },
                },
                nonStandardFuels: [],
              },
            },
          },
        },
      },
    } as RequestTaskState;

    store.setState(zeroEnergyState);

    fixture = TestBed.createComponent(TprThroughputTotalsOnlyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();

    expect(component['form'].invalid).toBe(true);
    expect(component['form'].errors).toEqual({
      zeroEnergy: 'Total energy/fuel amount consumed during the period must be greater than zero',
    });
  });

  it('should not set zeroEnergy error when delivered energy is 0 but baseline total is also zero', async () => {
    const zeroEnergyAllowedState = {
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
                baselineVariableEnergy: '0',
              },
            },
            performanceData: {
              ...basePayload.performanceData,
              energyFuelDetails: {
                ...basePayload.performanceData?.energyFuelDetails,
                standardFuels: {
                  GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '0' },
                  NON_GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '0' },
                },
                nonStandardFuels: [],
              },
            },
          },
        },
      },
    } as RequestTaskState;

    store.setState(zeroEnergyAllowedState);

    fixture = TestBed.createComponent(TprThroughputTotalsOnlyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();

    expect(component['form'].errors).toBeNull();
  });
});
