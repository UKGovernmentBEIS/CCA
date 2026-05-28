import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';

import { PerformanceDataFacilityInputData, PerformanceDataFacilityReferenceData } from 'cca-api';

import { tprFormQuery } from '../../../../target-period-reporting-form.selectors';
import { TprThroughputTotalsOnlyComponent } from './tpr-throughput-totals-only.component';

const mockBaselineData: PerformanceDataFacilityReferenceData = {
  baselineAndTargets: {
    baselineVariableEnergy: '5000',
    totalThroughput: '10000',
    totalFixedEnergy: '2000',
    variableEnergyType: null,
    measurementType: 'ENERGY_KWH',
    throughputUnit: 'tonnes',
    improvements: { TP5: '5', TP6: '6', TP7: '8', TP8: '12', TP9: '15' },
    usedReportingMechanism: false,
  },
} as any;

const mockPerformanceData: PerformanceDataFacilityInputData = {
  energyFuelDetails: {
    standardFuels: {
      GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '0' },
      NON_GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '0' },
    },
    atLeastSeventyPercentEnergyUsed: false,
    electricitySuppliedFromCHP: '0',
  },
  throughputDetails: { actualThroughput: '8000' },
} as any;

describe('TprThroughputTotalsOnlyComponent', () => {
  let component: TprThroughputTotalsOnlyComponent;
  let fixture: ComponentFixture<TprThroughputTotalsOnlyComponent>;

  beforeEach(async () => {
    const requestTaskStore = {
      select: vi.fn().mockImplementation((selector: unknown) => {
        if (selector === tprFormQuery.selectReferenceData) return signal(mockBaselineData);
        if (selector === tprFormQuery.selectPerformanceData) return signal(mockPerformanceData);
        if (selector === tprFormQuery.selectReportType) return signal<'FINAL' | 'INTERIM'>('FINAL');
        if (selector === tprFormQuery.selectTargetPeriodType) return signal('TP5');
        if (selector === tprFormQuery.selectSectionsCompleted) return signal({});
        if (selector === tprFormQuery.selectPayload)
          return signal({
            referenceData: mockBaselineData,
            performanceData: mockPerformanceData,
            reportType: 'FINAL',
            targetPeriodType: 'TP5',
            targetPeriodYear: 2026,
            sectionsCompleted: {},
          });
        return signal(null);
      }),
    };

    await TestBed.configureTestingModule({
      imports: [TprThroughputTotalsOnlyComponent],
      providers: [
        { provide: RequestTaskStore, useValue: requestTaskStore },
        { provide: Router, useValue: { navigate: vi.fn() } },
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TasksApiService, useValue: { saveRequestTaskAction: vi.fn(() => of(null)) } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TprThroughputTotalsOnlyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
