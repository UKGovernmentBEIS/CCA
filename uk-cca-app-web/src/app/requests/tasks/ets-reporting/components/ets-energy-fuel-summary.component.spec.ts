import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { EmissionsTradingSchemeDataInputDetails } from 'cca-api';

import { EtsMeasurementType } from '../ets-reporting.types';
import { EtsEnergyFuelSummaryComponent } from './ets-energy-fuel-summary.component';

describe('EtsEnergyFuelSummaryComponent', () => {
  let fixture: ComponentFixture<EtsEnergyFuelSummaryComponent>;
  let element: HTMLElement;
  const inputDetails: EmissionsTradingSchemeDataInputDetails = {
    isUltraSmallEmitter: false,
    energyFuelDetails: {
      standardFuels: {
        NATURAL_GAS: { deliveredEnergy: '1000.1234567', primaryEnergy: '0' },
        LPG: { deliveredEnergy: '0', primaryEnergy: '0' },
      },
      nonStandardFuels: [
        { name: 'Zero factor fuel', conversionFactor: '0', deliveredEnergy: '10', primaryEnergy: '0' },
      ],
      totalPrimaryEnergy: '0',
      totalCo2Emissions: '0',
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EtsEnergyFuelSummaryComponent],
      providers: [provideRouter([])],
    }).compileComponents();
    fixture = TestBed.createComponent(EtsEnergyFuelSummaryComponent);
    element = fixture.nativeElement;
    fixture.componentRef.setInput('measurementType', 'ENERGY_KWH');
    fixture.componentRef.setInput('inputDetails', inputDetails);
  });

  it('shows positive delivered rows including a custom zero-factor fuel and recalculates totals', () => {
    fixture.detectChanges();
    expect(element.querySelectorAll('tbody tr')).toHaveLength(2);
    expect(element.textContent).toContain('Zero factor fuel');
    expect(element.textContent).not.toContain('LPG');
    expect(element.textContent).toContain('1,000.1234567');
    expect(element.textContent).toContain('1,010.1234567');
    expect(element.textContent).toContain('0.1825625 tCO2e');
    expect(element.textContent).not.toContain('70%');
    expect(element.textContent).not.toContain('voluntarily');
    const primaryFactors = Array.from(element.querySelectorAll('tbody tr'), (row) =>
      row.querySelectorAll('td')[2].textContent.trim(),
    );
    expect(primaryFactors).toEqual(['1.0', '1.0']);
  });

  it('shows both answers and fuels when a USE facility opts in', () => {
    fixture.componentRef.setInput('inputDetails', {
      ...inputDetails,
      isUltraSmallEmitter: true,
      provideFuelDetails: true,
    });
    fixture.detectChanges();
    expect(element.querySelectorAll('dl > div')).toHaveLength(2);
    expect(element.querySelectorAll('tbody tr')).toHaveLength(2);
    expect(element.textContent).toContain('Do you want to voluntarily provide details of the fuels used in the year?');
  });

  it('shows only two answers when a USE facility opts out, even with stale fuels present', () => {
    fixture.componentRef.setInput('inputDetails', {
      ...inputDetails,
      isUltraSmallEmitter: true,
      provideFuelDetails: false,
    });
    fixture.detectChanges();
    expect(element.querySelectorAll('dl > div')).toHaveLength(2);
    expect(element.querySelector('table')).toBeNull();
    expect(element.textContent).not.toContain('Total');
    expect(element.textContent).not.toContain('No delivered');
  });

  it('routes changes to the forms and emits the selected custom fuel for deletion', () => {
    fixture.componentRef.setInput('isEditable', true);
    const removeFuel = vi.fn();
    fixture.componentInstance.removeFuel.subscribe(removeFuel);
    fixture.detectChanges();
    const links = Array.from(element.querySelectorAll('a'));
    expect(links).toHaveLength(3);
    expect(links[0].getAttribute('href')).toBe('/ultra-small-emitter');
    expect(links.slice(1).map((link) => link.getAttribute('href'))).toEqual(['/details', '/details']);
    const deleteButton = element.querySelector('button');
    expect(deleteButton.textContent).toContain('Delete');
    deleteButton.click();
    expect(removeFuel).toHaveBeenCalledWith(0);
  });

  it('keeps deletion indexes tied to custom fuels when standard fuels precede them', () => {
    fixture.componentRef.setInput('isEditable', true);
    fixture.componentRef.setInput('inputDetails', {
      ...inputDetails,
      energyFuelDetails: {
        ...inputDetails.energyFuelDetails,
        nonStandardFuels: [
          { name: 'Other fuel', conversionFactor: '0.5', deliveredEnergy: '100', primaryEnergy: '100' },
          ...inputDetails.energyFuelDetails.nonStandardFuels,
        ],
      },
    });
    const removeFuel = vi.fn();
    fixture.componentInstance.removeFuel.subscribe(removeFuel);
    fixture.detectChanges();
    element.querySelectorAll('button')[1].click();
    expect(removeFuel).toHaveBeenCalledWith(1);
  });

  it('omits action controls and the action column in read-only mode', () => {
    fixture.detectChanges();
    expect(element.querySelectorAll('a')).toHaveLength(0);
    expect(element.querySelector('button')).toBeNull();
    expect(element.textContent).not.toContain('Actions');
  });

  it.each<[EtsMeasurementType, string, string]>([
    ['ENERGY_KWH', 'Primary energy (kWh)', 'kgCO2e/kWh'],
    ['ENERGY_MWH', 'Primary energy (MWh)', 'kgCO2e/MWh'],
    ['ENERGY_GJ', 'Primary energy (GJ)', 'kgCO2e/GJ'],
    ['CARBON_KG', 'Primary CO2e (kg)', 'kgCO2e/kWh'],
    ['CARBON_TONNE', 'Primary CO2e (tonne)', 'kgCO2e/kWh'],
  ])('uses snapshot units for %s', (measurementType, primaryHeader, conversionUnit) => {
    fixture.componentRef.setInput('measurementType', measurementType);
    fixture.detectChanges();
    expect(element.querySelector('thead').textContent).toContain(primaryHeader);
    expect(element.querySelector('thead').textContent).toContain(conversionUnit);
    expect(element.querySelector('thead').textContent).toContain('Delivered energy');
  });

  it('recalculates GJ totals before display-factor rounding', () => {
    fixture.componentRef.setInput('measurementType', 'ENERGY_GJ');
    fixture.componentRef.setInput('inputDetails', {
      isUltraSmallEmitter: false,
      energyFuelDetails: { standardFuels: { NATURAL_GAS: { deliveredEnergy: '1000000', primaryEnergy: '0' } } },
    });
    fixture.detectChanges();
    expect(element.textContent).toContain('50,705.5555556 tCO2e');
  });

  it('shows the empty fuel state with zero totals for an incomplete fuel-required branch', () => {
    fixture.componentRef.setInput('inputDetails', { isUltraSmallEmitter: false });
    fixture.detectChanges();
    expect(element.textContent).toContain('No delivered energy values provided');
    expect(element.textContent).toContain('0 kWh');
    expect(element.textContent).toContain('0 tCO2e');
  });
});
