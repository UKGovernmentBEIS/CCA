import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { PerformanceDataFacilityInputEnergyFuelDetails } from 'cca-api';

import { EnergyFuelAmountSummaryComponent } from '../energy-fuel-amount/energy-fuel-amount-summary.component';

describe('EnergyFuelAmountSummaryComponent', () => {
  let component: EnergyFuelAmountSummaryComponent;
  let fixture: ComponentFixture<EnergyFuelAmountSummaryComponent>;

  const energyFuelDetailsWithFuels: PerformanceDataFacilityInputEnergyFuelDetails = {
    standardFuels: {
      NATURAL_GAS: { deliveredEnergy: '1000', primaryEnergy: '1000' },
      GRID_ELECTRICITY: { deliveredEnergy: '500', primaryEnergy: '1050' },
    },
    nonStandardFuels: [],
    atLeastSeventyPercentEnergyUsed: false,
    electricitySuppliedFromCHP: '200',
  };

  const energyFuelDetailsNoFuels: PerformanceDataFacilityInputEnergyFuelDetails = {
    standardFuels: {},
    nonStandardFuels: [],
    atLeastSeventyPercentEnergyUsed: false,
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EnergyFuelAmountSummaryComponent, RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(EnergyFuelAmountSummaryComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.componentRef.setInput('energyFuelDetails', energyFuelDetailsNoFuels);
    fixture.componentRef.setInput('usedReportingMechanism', false);
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should render fuel table rows when non-zero fuels exist', () => {
    fixture.componentRef.setInput('energyFuelDetails', energyFuelDetailsWithFuels);
    fixture.componentRef.setInput('usedReportingMechanism', true);
    fixture.detectChanges();

    const tableRows = fixture.nativeElement.querySelectorAll('table tbody tr');
    expect(tableRows.length).toBe(2); // NATURAL_GAS + GRID_ELECTRICITY
  });

  it('should show "Not provided" summary row when no fuels have values', () => {
    fixture.componentRef.setInput('energyFuelDetails', energyFuelDetailsNoFuels);
    fixture.componentRef.setInput('usedReportingMechanism', false);
    fixture.detectChanges();

    const element: HTMLElement = fixture.nativeElement;
    expect(element.textContent).toContain('Not provided');
  });

  it('should show the SRM section when reporting mechanism is used and rows exist', () => {
    fixture.componentRef.setInput('energyFuelDetails', energyFuelDetailsWithFuels);
    fixture.componentRef.setInput('usedReportingMechanism', true);
    fixture.detectChanges();

    const element: HTMLElement = fixture.nativeElement;
    expect(element.textContent).toContain('Special reporting methodology');
    expect(element.textContent).toContain('Throughput adjustment factor');
  });

  it('should hide the SRM section when reporting mechanism is not used', () => {
    fixture.componentRef.setInput('energyFuelDetails', energyFuelDetailsWithFuels);
    fixture.componentRef.setInput('usedReportingMechanism', false);
    fixture.detectChanges();

    const element: HTMLElement = fixture.nativeElement;
    expect(element.textContent).not.toContain('Special reporting methodology');
    expect(element.textContent).not.toContain('Throughput adjustment factor');
  });

  it('should calculate and display the throughput adjustment factor', () => {
    // GRID=500, NON_GRID=0, CHP=200 => (500+0)/(500+0+200) = 500/700 ≈ 0.7142857
    fixture.componentRef.setInput('energyFuelDetails', energyFuelDetailsWithFuels);
    fixture.componentRef.setInput('usedReportingMechanism', true);
    fixture.detectChanges();

    const element: HTMLElement = fixture.nativeElement;
    expect(element.textContent).toContain('0.7142857');
  });

  it('should show Change links when isEditable is true', () => {
    fixture.componentRef.setInput('energyFuelDetails', energyFuelDetailsWithFuels);
    fixture.componentRef.setInput('isEditable', true);
    fixture.componentRef.setInput('usedReportingMechanism', true);
    fixture.detectChanges();

    const changeLinks = fixture.nativeElement.querySelectorAll('a');
    expect(changeLinks.length).toBeGreaterThan(0);
  });

  it('should hide Change links when isEditable is false', () => {
    fixture.componentRef.setInput('energyFuelDetails', energyFuelDetailsWithFuels);
    fixture.componentRef.setInput('isEditable', false);
    fixture.componentRef.setInput('usedReportingMechanism', true);
    fixture.detectChanges();

    const changeLinks = fixture.nativeElement.querySelectorAll('a');
    expect(changeLinks.length).toBe(0);
  });

  it('should display non-standard (custom) fuel rows', () => {
    fixture.componentRef.setInput('energyFuelDetails', {
      standardFuels: {},
      nonStandardFuels: [{ name: 'Wood chips', conversionFactor: '0.5', deliveredEnergy: '300' }],
    });
    fixture.componentRef.setInput('usedReportingMechanism', false);
    fixture.detectChanges();

    const element: HTMLElement = fixture.nativeElement;
    expect(element.textContent).toContain('Wood chips');
  });

  it('should filter out zero-value standard fuels from the table', () => {
    fixture.componentRef.setInput('energyFuelDetails', {
      standardFuels: {
        NATURAL_GAS: { deliveredEnergy: '0', primaryEnergy: '0' },
        GRID_ELECTRICITY: { deliveredEnergy: '500', primaryEnergy: '1050' },
      },
      nonStandardFuels: [],
    });
    fixture.componentRef.setInput('usedReportingMechanism', false);
    fixture.detectChanges();

    const tableRows = fixture.nativeElement.querySelectorAll('table tbody tr');
    expect(tableRows.length).toBe(1); // only GRID_ELECTRICITY
  });

  it('should display kWh CO2 conversion factors from canonical base values', () => {
    fixture.componentRef.setInput('measurementType', 'kWh');
    fixture.componentRef.setInput('energyFuelDetails', {
      standardFuels: { NATURAL_GAS: { deliveredEnergy: '1000', primaryEnergy: '1000' } },
      nonStandardFuels: [],
    });
    fixture.componentRef.setInput('usedReportingMechanism', false);
    fixture.detectChanges();

    const element: HTMLElement = fixture.nativeElement;
    expect(element.textContent).toContain('0.18254');
  });

  it('should calculate primary carbon using delivered x primary factor x CO2 factor', () => {
    fixture.componentRef.setInput('measurementType', 'kg');
    fixture.componentRef.setInput('energyFuelDetails', {
      standardFuels: { NATURAL_GAS: { deliveredEnergy: '1000', primaryEnergy: '1000' } },
      nonStandardFuels: [],
    });
    fixture.componentRef.setInput('usedReportingMechanism', false);
    fixture.detectChanges();

    const element: HTMLElement = fixture.nativeElement;
    expect(element.textContent).toContain('Primary carbon (kgCO2e)');
    expect(element.textContent).toContain('182.54');
  });
});
