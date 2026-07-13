import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionState, RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { PerformanceDataFacilitySubmittedRequestActionPayload } from 'cca-api';

import { TprThroughputSubmittedComponent } from './tpr-throughput-submitted.component';

function createPayload(
  overrides: Partial<PerformanceDataFacilitySubmittedRequestActionPayload> = {},
): PerformanceDataFacilitySubmittedRequestActionPayload {
  return {
    payloadType: 'PERFORMANCE_DATA_FACILITY_SUBMITTED_PAYLOAD',
    details: {
      targetPeriodType: 'TP8',
      reportType: 'FINAL',
      submissionType: 'PRIMARY',
      reportVersion: 2,
      targetPeriodYear: 2028,
      creationDate: '2026-01-01T00:00:00Z',
      submissionDate: '2026-01-02T00:00:00Z',
    },
    performanceData: {
      baselineAndTargets: {
        measurementType: 'ENERGY_KWH',
        variableEnergyType: 'BY_PRODUCT',
        throughputUnit: 'Box of 50',
        variableEnergyConsumptionDataByProduct: [
          {
            productName: 'Blue Widgets',
            baselineYear: 2022,
            productStatus: 'LIVE',
            energy: '1000',
            throughput: '1',
            throughputUnit: 'Each',
          },
          {
            productName: 'Red Widgets',
            baselineYear: 2024,
            productStatus: 'LIVE',
            energy: '1000',
            throughput: '4',
            throughputUnit: 'Box of 50',
          },
          {
            productName: 'Excluded Widgets',
            baselineYear: 2024,
            productStatus: 'EXCLUDED',
            energy: '1',
            throughput: '1',
            throughputUnit: 'Each',
          },
        ],
      },
      energyFuelDetails: {
        atLeastSeventyPercentEnergyUsed: true,
      },
      throughputDetails: {
        actualThroughput: '5000',
        targetImprovement: '0.12',
        adjustedThroughput: '4500',
        totalTargetVariableEnergy: '1796.667',
        variableEnergyConsumptionDataByProduct: [
          {
            productName: 'Blue Widgets',
            actualThroughput: '1000',
            targetImprovement: '0.12',
            adjustedThroughput: '900',
            targetEnergy: '880',
          },
          {
            productName: 'Red Widgets',
            actualThroughput: '4000',
            targetImprovement: '0.08333',
            adjustedThroughput: '3600',
            targetEnergy: '916.667',
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
    },
    ...overrides,
  };
}

function createState(payload: PerformanceDataFacilitySubmittedRequestActionPayload): RequestActionState {
  return {
    action: {
      id: 1,
      type: 'PERFORMANCE_DATA_FACILITY_SUBMITTED',
      payload,
      creationDate: '2026-01-02T00:00:00Z',
    },
  };
}

describe('TprThroughputSubmittedComponent', () => {
  let component: TprThroughputSubmittedComponent;
  let fixture: ComponentFixture<TprThroughputSubmittedComponent>;
  let store: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TprThroughputSubmittedComponent],
      providers: [RequestActionStore, { provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState(createState(createPayload()));

    fixture = TestBed.createComponent(TprThroughputSubmittedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render BY_PRODUCT throughput table and calculated total', () => {
    const html = fixture.nativeElement.textContent;

    expect(html).toContain('Throughput details');
    expect(html).toContain('Target period throughput details');
    expect(html).toContain('Product name');
    expect(html).toContain('Blue Widgets');
    expect(html).toContain('Red Widgets');
    expect(html).not.toContain('Excluded Widgets');
    expect(html).toContain('Calculated energy amounts');
    expect(html).toContain('Total target variable energy (kWh)');
  });

  it('should render interim target heading for interim reports', () => {
    const payload = createPayload({
      details: {
        ...createPayload().details,
        reportType: 'INTERIM',
      },
    });

    store.setState(createState(payload));
    fixture.detectChanges();

    const html = fixture.nativeElement.textContent;
    expect(html).toContain('Interim target %');
    expect(html).not.toContain('Improvement target %');
  });

  it('should render totals-only summary when variable energy type is totals', () => {
    const payload = createPayload({
      performanceData: {
        ...createPayload().performanceData,
        baselineAndTargets: {
          ...createPayload().performanceData.baselineAndTargets,
          variableEnergyType: 'TOTALS',
          throughputUnit: 'tonnes',
        },
        throughputDetails: {
          ...createPayload().performanceData.throughputDetails,
          actualThroughput: '8000',
          totalTargetVariableEnergy: '3520',
        },
      },
    });

    store.setState(createState(payload));
    fixture.detectChanges();

    const html = fixture.nativeElement.textContent;
    expect(html).toContain('Total throughput (tonnes)');
    expect(html).toContain('Total target variable energy (kWh)');
    expect(html).not.toContain('Product name');
  });

  it('should show not provided when BY_PRODUCT has no visible rows', () => {
    const payload = createPayload({
      performanceData: {
        ...createPayload().performanceData,
        baselineAndTargets: {
          ...createPayload().performanceData.baselineAndTargets,
          variableEnergyType: 'BY_PRODUCT',
          variableEnergyConsumptionDataByProduct: [
            {
              productName: 'Excluded Widgets',
              baselineYear: 2024,
              productStatus: 'EXCLUDED',
              energy: '1',
              throughput: '1',
              throughputUnit: 'Each',
            },
          ],
        },
        throughputDetails: {
          ...createPayload().performanceData.throughputDetails,
          variableEnergyConsumptionDataByProduct: [],
        },
      },
    });

    store.setState(createState(payload));
    fixture.detectChanges();

    const html = fixture.nativeElement.textContent;
    expect(html).toContain('Not provided');
  });
});
