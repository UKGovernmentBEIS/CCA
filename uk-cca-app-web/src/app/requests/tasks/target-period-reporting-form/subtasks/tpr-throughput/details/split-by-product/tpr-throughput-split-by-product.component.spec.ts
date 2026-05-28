import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';

import { PerformanceDataFacilityInputData, PerformanceDataFacilityReferenceData } from 'cca-api';

import { tprFormQuery } from '../../../../target-period-reporting-form.selectors';
import { TprThroughputSplitByProductComponent } from './tpr-throughput-split-by-product.component';

const mockBaselineData: PerformanceDataFacilityReferenceData = {
  baselineAndTargets: {
    baselineVariableEnergy: '5000',
    totalThroughput: '10000',
    variableEnergyType: 'BY_PRODUCT',
    measurementType: 'ENERGY_KWH',
    throughputUnit: 'tonnes',
    baselineDate: '2022-01-01',
    improvements: { TP5: '5', TP6: '6', TP7: '8', TP8: '12', TP9: '15' },
    usedReportingMechanism: false,
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
};

const mockPerformanceData: PerformanceDataFacilityInputData = {
  energyFuelDetails: {
    standardFuels: {
      GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '0' },
      NON_GRID_ELECTRICITY: { deliveredEnergy: '0', primaryEnergy: '0' },
    },
    atLeastSeventyPercentEnergyUsed: false,
    electricitySuppliedFromCHP: '0',
  },
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
};

describe('TprThroughputSplitByProductComponent', () => {
  let component: TprThroughputSplitByProductComponent;
  let fixture: ComponentFixture<TprThroughputSplitByProductComponent>;
  let storeMock: { select: (selector: unknown) => unknown };

  const configureAndCreate = async (
    options: {
      referenceData?: PerformanceDataFacilityReferenceData;
      performanceData?: PerformanceDataFacilityInputData;
      targetPeriodYear?: number;
    } = {},
  ) => {
    const {
      referenceData = mockBaselineData,
      performanceData = mockPerformanceData,
      targetPeriodYear = 2026,
    } = options;

    storeMock = {
      select: vi.fn().mockImplementation((selector: unknown) => {
        if (selector === tprFormQuery.selectReferenceData) return signal(referenceData);
        if (selector === tprFormQuery.selectPerformanceData) return signal(performanceData);
        if (selector === tprFormQuery.selectReportType) return signal<'FINAL' | 'INTERIM'>('FINAL');
        if (selector === tprFormQuery.selectTargetPeriodType) return signal('TP5');
        if (selector === tprFormQuery.selectTargetPeriodYear) return signal(targetPeriodYear);
        if (selector === tprFormQuery.selectSectionsCompleted) return signal({});
        if (selector === tprFormQuery.selectPayload)
          return signal({
            referenceData,
            performanceData,
            reportType: 'FINAL',
            targetPeriodType: 'TP5',
            targetPeriodYear,
            sectionsCompleted: {},
          });
        return signal(null);
      }),
    };

    await TestBed.configureTestingModule({
      imports: [TprThroughputSplitByProductComponent],
      providers: [
        { provide: RequestTaskStore, useValue: storeMock },
        { provide: Router, useValue: { navigate: vi.fn() } },
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TasksApiService, useValue: { saveRequestTaskAction: vi.fn(() => of(null)) } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TprThroughputSplitByProductComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  };

  afterEach(() => {
    TestBed.resetTestingModule();
  });

  it('should create', async () => {
    await configureAndCreate();

    expect(component).toBeTruthy();
  });

  it('should prepopulate actual throughput from saved throughput details', async () => {
    await configureAndCreate();

    const actualThroughput = component['form'].controls.products.at(0).controls.actualThroughput.value;

    expect(actualThroughput).toBe(321.1234567);
  });

  it('should calculate total target variable energy from summed product intensities and apply facility improvement once', async () => {
    await configureAndCreate();

    const expected = (1000 * 321.1234567 + 2000 * 100) * (1 - 0.05);

    expect(component['totalTargetVariableEnergy']()).toBeCloseTo(expected, 7);
  });
});
