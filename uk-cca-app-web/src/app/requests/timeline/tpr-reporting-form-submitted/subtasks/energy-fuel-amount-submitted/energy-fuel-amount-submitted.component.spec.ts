import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionState, RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { PerformanceDataFacilitySubmittedRequestActionPayload } from 'cca-api';

import { EnergyFuelAmountSubmittedComponent } from './energy-fuel-amount-submitted.component';

function createPayload(
  overrides: Partial<PerformanceDataFacilitySubmittedRequestActionPayload> = {},
): PerformanceDataFacilitySubmittedRequestActionPayload {
  return {
    payloadType: 'PERFORMANCE_DATA_FACILITY_SUBMITTED_PAYLOAD',
    details: {
      targetPeriodType: 'TP8',
      submissionType: 'PRIMARY',
      reportType: 'FINAL',
      reportVersion: 1,
      creationDate: '2026-01-01T00:00:00Z',
      submissionDate: '2026-01-02T00:00:00Z',
      targetPeriodYear: 2026,
    },
    performanceData: {
      baselineAndTargets: {
        measurementType: 'ENERGY_KWH',
        usedReportingMechanism: true,
      },
      energyFuelDetails: {
        fuels: [
          {
            name: 'Grid electricity and electricity from combustion of a renewable fuel',
            fixedConversionFactorCode: 'GRID_ELECTRICITY',
            conversionFactor: '0.10046',
            deliveredEnergy: '100',
            primaryConversionFactor: '2.1',
            primaryEnergy: '210',
          },
          {
            name: 'Custom fuel',
            conversionFactor: '0.333',
            deliveredEnergy: '50',
            primaryConversionFactor: '1',
            primaryEnergy: '50',
          },
        ],
        atLeastSeventyPercentEnergyUsed: true,
        electricitySuppliedFromCHP: '20',
      },
      throughputDetails: {
        actualThroughput: '1000',
        targetImprovement: '0',
        adjustedThroughput: '0',
        totalTargetVariableEnergy: '0',
        variableEnergyConsumptionDataByProduct: [],
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

describe('EnergyFuelAmountSubmittedComponent', () => {
  let component: EnergyFuelAmountSubmittedComponent;
  let fixture: ComponentFixture<EnergyFuelAmountSubmittedComponent>;
  let store: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EnergyFuelAmountSubmittedComponent],
      providers: [RequestActionStore, { provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState(createState(createPayload()));

    fixture = TestBed.createComponent(EnergyFuelAmountSubmittedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the submitted fuels and 70% answer', () => {
    const html = fixture.nativeElement.textContent;

    expect(html).toContain('Energy/fuel details');
    expect(html).toContain('Fuel type');
    expect(html).toContain('Grid electricity and electricity from combustion of a renewable fuel');
    expect(html).toContain('Custom fuel');
    expect(html).toContain('70% rule');
    expect(html).toContain('Yes');
  });

  it('should show SRM values when reporting mechanism is used', () => {
    const html = fixture.nativeElement.textContent;

    expect(html).toContain('Special reporting methodology (SRM)');
    expect(html).toContain(
      'Electricity supplied from combined heat and power plant and dedicated generator electricity (kWh)',
    );
    expect(html).toContain('Throughput adjustment factor');
  });

  it('should show the SRM electricity unit for the submitted energy measurement type', () => {
    const payload = createPayload({
      performanceData: {
        ...createPayload().performanceData,
        baselineAndTargets: {
          ...createPayload().performanceData.baselineAndTargets,
          measurementType: 'ENERGY_MWH',
        },
      },
    });

    store.setState(createState(payload));
    fixture.detectChanges();

    const html = fixture.nativeElement.textContent;
    expect(html).toContain(
      'Electricity supplied from combined heat and power plant and dedicated generator electricity (MWh)',
    );
  });

  it('should hide SRM section when reporting mechanism is not used', () => {
    const payload = createPayload({
      performanceData: {
        ...createPayload().performanceData,
        baselineAndTargets: {
          ...createPayload().performanceData.baselineAndTargets,
          usedReportingMechanism: false,
        },
      },
    });

    store.setState(createState(payload));
    fixture.detectChanges();

    const html = fixture.nativeElement.textContent;
    expect(html).not.toContain('Special reporting methodology (SRM)');
    expect(html).not.toContain('Throughput adjustment factor');
  });

  it('should show not provided when no non-zero fuel rows exist', () => {
    const payload = createPayload({
      performanceData: {
        ...createPayload().performanceData,
        energyFuelDetails: {
          ...createPayload().performanceData.energyFuelDetails,
          fuels: [
            {
              name: 'Natural gas',
              fixedConversionFactorCode: 'NATURAL_GAS',
              conversionFactor: '0.18254',
              deliveredEnergy: '0',
              primaryConversionFactor: '1',
              primaryEnergy: '0',
            },
          ],
          atLeastSeventyPercentEnergyUsed: undefined,
        },
      },
    });

    store.setState(createState(payload));
    fixture.detectChanges();

    const html = fixture.nativeElement.textContent;
    expect(html).toContain('Energy/fuel amount consumed');
    expect(html).toContain('Not provided');
  });

  it('should render carbon labels for carbon measurement types', () => {
    const payload = createPayload({
      performanceData: {
        ...createPayload().performanceData,
        baselineAndTargets: {
          ...createPayload().performanceData.baselineAndTargets,
          measurementType: 'CARBON_KG',
        },
      },
    });

    store.setState(createState(payload));
    fixture.detectChanges();

    const html = fixture.nativeElement.textContent;
    const text = html.replace(/\s+/g, ' ');
    expect(html).toContain('CO2 conversion factor (kgCO2e/kWh)');
    expect(html).toContain('Primary CO2e (kgCO2e)');
    expect(text).toContain('100 kWh');
    expect(html).toContain(
      'Electricity supplied from combined heat and power plant and dedicated generator electricity (kWh)',
    );
  });

  it('should render tonne carbon labels for tonne carbon measurement types', () => {
    const payload = createPayload({
      performanceData: {
        ...createPayload().performanceData,
        baselineAndTargets: {
          ...createPayload().performanceData.baselineAndTargets,
          measurementType: 'CARBON_TONNE',
        },
      },
    });

    store.setState(createState(payload));
    fixture.detectChanges();

    const html = fixture.nativeElement.textContent;
    const text = html.replace(/\s+/g, ' ');
    expect(html).toContain('CO2 conversion factor (kgCO2e/kWh)');
    expect(html).toContain('Primary CO2e (tCO2e)');
    expect(text).toContain('100 kWh');
  });
});
