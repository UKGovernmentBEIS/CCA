import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionState, RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { PerformanceDataFacilitySubmittedRequestActionPayload } from 'cca-api';

import { SubmitResultsComponent } from './submit-results.component';

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
        baselineDate: '2022-01-01',
        measurementType: 'ENERGY_KWH',
        usedReportingMechanism: true,
        totalFixedEnergy: '1000',
        baselineVariableEnergy: '200',
        totalThroughput: '10000',
        throughputUnit: 'tonnes',
        variableEnergyType: 'TOTALS',
        improvements: {
          TP7: '8',
          TP8: '12',
          TP9: '15',
        },
        variableEnergyConsumptionDataByProduct: [],
      },
      energyFuelDetails: {
        atLeastSeventyPercentEnergyUsed: true,
      },
      throughputDetails: {
        totalTargetVariableEnergy: '4436.667',
      },
      calculatedResults: {
        actualEnergyCarbon: '4890000',
        targetEnergyCarbon: '4436667',
        energyCarbonDifference: '-453333',
        targetImprovement: '0.12',
        weightedConversionFactor: '0.1134866',
        targetCo2Emissions: '504',
        actualCo2Emissions: '555',
        co2EmissionsDifference: '51',
        actualImprovement: '0.022',
        targetPeriodResultType: 'TARGET_NOT_MET',
        buyOutRequired: '51',
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

describe('SubmitResultsComponent', () => {
  let component: SubmitResultsComponent;
  let fixture: ComponentFixture<SubmitResultsComponent>;
  let store: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SubmitResultsComponent],
      providers: [RequestActionStore, { provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState(createState(createPayload()));

    fixture = TestBed.createComponent(SubmitResultsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render comparison and results details for final report', () => {
    const html = fixture.nativeElement.textContent;

    expect(html).toContain('TPR calculated results');
    expect(html).toContain('Reporting period');
    expect(html).toContain('Comparison between actual and target amounts');
    expect(html).toContain('Actual energy (kWh)');
    expect(html).toContain('Results details');
    expect(html).toContain('Target period result');
    expect(html).toContain('Target not met');
    expect(html).toContain('Total buy-out required (tCO2e)');
  });

  it('should hide results details section for interim report', () => {
    const payload = createPayload({
      details: {
        ...createPayload().details,
        reportType: 'INTERIM',
      },
      performanceData: {
        ...createPayload().performanceData,
        calculatedResults: {
          ...createPayload().performanceData.calculatedResults,
          targetPeriodResultType: undefined,
          buyOutRequired: undefined,
        },
      },
    });

    store.setState(createState(payload));
    fixture.detectChanges();

    const html = fixture.nativeElement.textContent;
    expect(html).toContain('TPR calculated results');
    expect(html).toContain('Comparison between actual and target amounts');
    expect(html).not.toContain('Results details');
    expect(html).not.toContain('Target period result');
  });

  it('should render collapsed baseline details section', () => {
    const html = fixture.nativeElement.textContent;

    expect(html).toContain('View baseline details');
    expect(html).toContain('Details of baseline data');
    expect(html).toContain('Total baseline throughput (tonnes)');
    expect(html).not.toContain('Total throughput (kWh)');
    expect(html).toContain('Targets');
  });
});
