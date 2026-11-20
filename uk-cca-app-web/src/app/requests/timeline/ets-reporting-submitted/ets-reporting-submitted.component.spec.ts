import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { getSummaryListData } from '@testing';

import { EmissionsTradingSchemeDataContainer } from 'cca-api';

import { EtsReportingSubmittedComponent } from './ets-reporting-submitted.component';
import { mockEtsSubmittedAction, mockEtsSubmittedPayload } from './testing/mock-data';

describe('EtsReportingSubmittedComponent', () => {
  let fixture: ComponentFixture<EtsReportingSubmittedComponent>;
  let store: RequestActionStore;
  let element: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EtsReportingSubmittedComponent],
      providers: [provideRouter([])],
    }).compileComponents();
    store = TestBed.inject(RequestActionStore);
    store.setAction(mockEtsSubmittedAction());
    fixture = TestBed.createComponent(EtsReportingSubmittedComponent);
    element = fixture.nativeElement;
  });

  it('renders the historical participation, data year, dates and non-USE answer', () => {
    fixture.detectChanges();
    expect(getSummaryListData(element)).toEqual([
      [['Participates in UK ETS'], ['Yes, UK-E-IN-0000']],
      [
        ['Reporting year', 'Created', 'Submitted'],
        ['2026', '30 Apr 2027', '1 May 2027, 3:25pm'],
      ],
      [['Is this facility registered as an ultra-small emitter (USE) in UK ETS?'], ['No']],
    ]);
    expect(element.textContent).toContain('Ultra-small emitter (USE) in UK ETS');
    expect(element.textContent).not.toContain('voluntarily');
    expect(element.querySelectorAll('tbody tr')).toHaveLength(2);
  });

  it('preserves submitted fuel factors, positive zero-carbon fuel and saved totals without recalculation', () => {
    fixture.detectChanges();
    const rows = Array.from(element.querySelectorAll('tbody tr'), (row) => row.textContent.replace(/\s+/g, ' ').trim());
    expect(rows).toEqual(['Natural gas 0.2500001 1,000 1.0 1,000', 'Zero-carbon fuel 0 10 1.0 10']);
    expect(element.textContent).toContain('1,010 kWh');
    expect(element.textContent).toContain('0.2500001 tCO2e');
    expect(element.textContent).not.toContain('0.18254');
    expect(element.querySelector('a, button, input')).toBeNull();
    expect(element.textContent).not.toContain('Actions');
    expect(element.textContent).not.toContain('70%');
  });

  it('shows voluntary fuel reporting for a USE with submitted results', () => {
    const payload = mockEtsSubmittedPayload();
    payload.performanceData.isUltraSmallEmitter = true;
    store.setAction(mockEtsSubmittedAction(payload));
    fixture.detectChanges();
    expect(getSummaryListData(element)[2]).toEqual([
      [
        'Is this facility registered as an ultra-small emitter (USE) in UK ETS?',
        'Do you want to voluntarily provide details of the fuels used in the year?',
      ],
      ['Yes', 'Yes'],
    ]);
    expect(element.querySelectorAll('tbody tr')).toHaveLength(2);
    expect(element.textContent).toContain('1,010 kWh');
  });

  it('shows a USE opting out without displaying fuel details or totals', () => {
    const payload = mockEtsSubmittedPayload();
    payload.performanceData.isUltraSmallEmitter = true;
    payload.performanceData.fuels = [];
    payload.calculatedResults = null;
    store.setAction(mockEtsSubmittedAction(payload));
    fixture.detectChanges();
    expect(getSummaryListData(element)[2][1]).toEqual(['Yes', 'No']);
    expect(element.querySelector('table')).toBeNull();
    expect(element.textContent).not.toContain('Energy/fuel amount consumed');
    expect(element.textContent).not.toContain('Total');
    expect(element.textContent).not.toContain('No delivered');
  });

  it('displays the submitted report when the facility participation snapshot is No', () => {
    const payload = mockEtsSubmittedPayload();
    payload.performanceData.uketsId = null;
    store.setAction(mockEtsSubmittedAction(payload));
    fixture.detectChanges();
    expect(getSummaryListData(element)[0]).toEqual([['Participates in UK ETS'], ['No']]);
    expect(element.querySelectorAll('tbody tr')).toHaveLength(2);
  });

  it.each<[EmissionsTradingSchemeDataContainer['measurementType'], string, string]>([
    ['ENERGY_KWH', 'Primary energy (kWh)', 'kgCO2e/kWh'],
    ['ENERGY_MWH', 'Primary energy (MWh)', 'kgCO2e/MWh'],
    ['ENERGY_GJ', 'Primary energy (GJ)', 'kgCO2e/GJ'],
    ['CARBON_KG', 'Primary CO2e (kgCO2e)', 'kgCO2e/kWh'],
    ['CARBON_TONNE', 'Primary CO2e (tCO2e)', 'kgCO2e/kWh'],
  ])('uses the historical %s units', (measurementType, primaryHeader, factorUnit) => {
    const payload = mockEtsSubmittedPayload();
    payload.performanceData.measurementType = measurementType;
    store.setAction(mockEtsSubmittedAction(payload));
    fixture.detectChanges();
    const header = element.querySelector('thead').textContent;
    expect(header).toContain(primaryHeader);
    expect(header).toContain(`CO2 conversion factor (${factorUnit})`);
    expect(element.textContent).toContain('Total CO2e emissions (tCO2e)');
  });

  it('displays submitted carbon amounts and totals at seven decimals without converting them again', () => {
    const payload = mockEtsSubmittedPayload();
    payload.performanceData.measurementType = 'CARBON_TONNE';
    payload.performanceData.fuels[0].primaryEnergy = '0.2500001';
    payload.performanceData.fuels[1].primaryEnergy = '0';
    payload.calculatedResults.totalPrimaryEnergy = '0.2500001';
    store.setAction(mockEtsSubmittedAction(payload));
    fixture.detectChanges();
    expect(element.querySelector('tbody tr').lastElementChild.textContent.trim()).toBe('0.2500001');
    expect(element.querySelectorAll('p')[0].textContent.trim()).toBe('0.2500001 tCO2e');
  });

  it('orders standard fuels before custom fuels without changing the frozen snapshot', () => {
    const payload = mockEtsSubmittedPayload();
    payload.performanceData.fuels.reverse();
    store.setAction(mockEtsSubmittedAction(payload));
    fixture.detectChanges();
    expect(element.querySelector('tbody th').textContent.trim()).toBe('Natural gas');
    expect(payload.performanceData.fuels[0].name).toBe('Zero-carbon fuel');
  });

  it('uses the ETS task labels for standard fuels and preserves custom names and submitted factors', () => {
    const payload = mockEtsSubmittedPayload();
    payload.performanceData.fuels = [
      {
        ...payload.performanceData.fuels[0],
        name: 'Gas oil/Diesel',
        fixedConversionFactorCode: 'GAS_DIESEL_OIL',
      },
      {
        ...payload.performanceData.fuels[0],
        name: 'Fuel Oil',
        fixedConversionFactorCode: 'FUEL_OIL',
      },
      payload.performanceData.fuels[1],
    ];
    store.setAction(mockEtsSubmittedAction(payload));
    fixture.detectChanges();

    expect(Array.from(element.querySelectorAll('tbody th'), (cell) => cell.textContent.trim())).toEqual([
      'Gas oil / diesel',
      'Fuel oil',
      'Zero-carbon fuel',
    ]);
    expect(element.querySelector('tbody tr td').textContent.trim()).toBe('0.2500001');
    expect(payload.performanceData.fuels[0].name).toBe('Gas oil/Diesel');
  });
});
